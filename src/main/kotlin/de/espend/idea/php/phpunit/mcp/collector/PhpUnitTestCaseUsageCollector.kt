package de.espend.idea.php.phpunit.mcp.collector

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiSearchHelper
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.ClassConstantReference
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.NewExpression
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpExpression
import com.jetbrains.php.lang.psi.elements.PhpReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.stubs.indexes.PhpClassFqnIndex
import com.jetbrains.php.lang.psi.stubs.indexes.PhpInterfaceFqnIndex
import com.jetbrains.php.phpunit.PhpUnitUtil
import de.espend.idea.php.phpunit.mcp.McpCsvUtil
import de.espend.idea.php.phpunit.mcp.McpGlobMatcher
import de.espend.idea.php.phpunit.mcp.McpPathUtil
import de.espend.idea.php.phpunit.utils.MockeryReferencingUtil
import de.espend.idea.php.phpunit.utils.PhpElementsUtil
import de.espend.idea.php.phpunit.utils.PhpUnitPluginUtil
import org.apache.commons.lang3.StringUtils
import java.util.Locale
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

class PhpUnitTestCaseUsageCollector(private val project: Project) {
    fun collect(target: String?, fileGlob: String?): String {
        val subjects = linkedSetOf<SearchSubject>()

        if (!fileGlob.isNullOrBlank()) {
            subjects.addAll(collectSubjectsFromFileGlob(fileGlob))
        }

        if (!target.isNullOrBlank()) {
            subjects.addAll(collectSubjectsFromTarget(target))
        }

        if (subjects.isEmpty()) {
            return ""
        }

        val subjectSet = SubjectSet(subjects)
        val testCases = linkedSetOf<String>()

        for (phpFile in collectCandidatePhpFiles(subjectSet.searchWords)) {
            collectTestCasesFromFile(phpFile, subjectSet, testCases)
        }

        return testCases
            .sortedWith(String.CASE_INSENSITIVE_ORDER)
            .joinToString(",") { McpCsvUtil.escape(it) }
    }

    private fun collectSubjectsFromFileGlob(fileGlob: String): Set<SearchSubject> {
        val scope = GlobalSearchScope.getScopeRestrictedByFileTypes(GlobalSearchScope.projectScope(project), PhpFileType.INSTANCE)
        val psiManager = PsiManager.getInstance(project)
        val result = linkedSetOf<SearchSubject>()

        for (virtualFile in FileTypeIndex.getFiles(PhpFileType.INSTANCE, scope)) {
            val relativePath = McpPathUtil.getRelativeProjectPath(project, virtualFile)
            if (!McpGlobMatcher.matches(relativePath, fileGlob)) {
                continue
            }

            val phpFile = psiManager.findFile(virtualFile) as? PhpFile ?: continue
            for (phpClass in PsiTreeUtil.findChildrenOfType(phpFile, PhpClass::class.java)) {
                addClassSubjects(result, phpClass, methodSelector = null, allowShortClassName = false)
            }
        }

        return result
    }

    private fun collectSubjectsFromTarget(target: String): Set<SearchSubject> {
        val result = linkedSetOf<SearchSubject>()

        for (rawPart in target.split(',')) {
            val part = rawPart.trim()
            if (part.isEmpty()) {
                continue
            }

            val selector = parseTargetSelector(part)
            val matchedClasses = resolveClasses(selector)

            if (matchedClasses.isEmpty()) {
                if (!selector.className.isRegex) {
                    result.add(
                        SearchSubject(
                            normalizeClassName(selector.className.text),
                            selector.methodName?.takeIf { !it.isRegex }?.text,
                            selector.className.isShortClassName,
                        )
                    )
                }

                continue
            }

            for (phpClass in matchedClasses) {
                addClassSubjects(result, phpClass, selector.methodName, selector.className.isShortClassName)
            }
        }

        return result
    }

    private fun resolveClasses(selector: TargetSelector): Collection<PhpClass> {
        val phpIndex = PhpIndex.getInstance(project)

        if (!selector.needsClassEnumeration) {
            return phpIndex.getAnyByFQN(normalizeClassName(selector.className.text))
        }

        val fqns = linkedSetOf<String>()
        val index = FileBasedIndex.getInstance()

        index.processAllKeys(PhpClassFqnIndex.KEY, { fqn ->
            if (selector.matchesClass(fqn)) {
                fqns.add(fqn)
            }
            true
        }, project)

        index.processAllKeys(PhpInterfaceFqnIndex.KEY, { fqn ->
            if (selector.matchesClass(fqn)) {
                fqns.add(fqn)
            }
            true
        }, project)

        return fqns.flatMap { phpIndex.getAnyByFQN(it) }
    }

    private fun addClassSubjects(
        result: MutableSet<SearchSubject>,
        phpClass: PhpClass,
        methodSelector: SelectorValue?,
        allowShortClassName: Boolean,
    ) {
        val className = normalizeClassName(phpClass.fqn)

        if (methodSelector == null) {
            result.add(SearchSubject(className, null, allowShortClassName))

            for (method in phpClass.methods) {
                val methodName = method.name
                if (methodName.isNotBlank() && !methodName.startsWith("__")) {
                    result.add(SearchSubject(className, methodName, allowShortClassName))
                }
            }

            return
        }

        if (!methodSelector.isRegex) {
            result.add(SearchSubject(className, methodSelector.text, allowShortClassName))
            return
        }

        for (method in phpClass.methods) {
            val methodName = method.name
            if (methodSelector.matches(methodName)) {
                result.add(SearchSubject(className, methodName, allowShortClassName))
            }
        }
    }

    private fun collectCandidatePhpFiles(searchWords: Set<String>): List<PhpFile> {
        if (searchWords.isEmpty()) {
            return emptyList()
        }

        val scope = GlobalSearchScope.getScopeRestrictedByFileTypes(GlobalSearchScope.projectScope(project), PhpFileType.INSTANCE)
        val searchHelper = PsiSearchHelper.getInstance(project)
        val psiManager = PsiManager.getInstance(project)
        val visitedFiles = linkedSetOf<VirtualFile>()
        val phpFiles = ArrayList<PhpFile>()

        for (word in searchWords) {
            searchHelper.processAllFilesWithWord(word, scope, { psiFile: PsiFile ->
                val virtualFile = psiFile.virtualFile ?: return@processAllFilesWithWord true
                if (!visitedFiles.add(virtualFile)) {
                    return@processAllFilesWithWord true
                }

                val phpFile = when (psiFile) {
                    is PhpFile -> psiFile
                    else -> psiManager.findFile(virtualFile) as? PhpFile
                } ?: return@processAllFilesWithWord true

                phpFiles.add(phpFile)
                true
            }, false)
        }

        return phpFiles
    }

    private fun collectTestCasesFromFile(phpFile: PhpFile, subjectSet: SubjectSet, testCases: MutableSet<String>) {
        val testClasses = PsiTreeUtil.findChildrenOfType(phpFile, PhpClass::class.java)
            .filter { isTestClass(it) }

        if (testClasses.isEmpty()) {
            return
        }

        val testClassRanges = testClasses.associateWith { it.textRange }
        val processedElements = hashSetOf<PsiElement>()

        phpFile.accept(object : PsiRecursiveElementWalkingVisitor() {
            override fun visitElement(element: PsiElement) {
                if (processedElements.add(element) && isUsageElement(element, subjectSet)) {
                    val testClass = testClassRanges.entries
                        .firstOrNull { it.value.contains(element.textRange) }
                        ?.key

                    if (testClass != null) {
                        testCases.add(testCaseName(testClass, element))
                    }
                }

                super.visitElement(element)
            }
        })
    }

    private fun isUsageElement(element: PsiElement, subjectSet: SubjectSet): Boolean {
        return when (element) {
            is ClassReference -> subjectSet.matchesClass(element.fqn)
            is ClassConstantReference -> matchesClassExpression(element.classReference, subjectSet)
            is NewExpression -> matchesClassExpression(element.classReference, subjectSet)
            is MethodReference -> matchesMethodReference(element, subjectSet)
            is StringLiteralExpression -> matchesStringLiteral(element, subjectSet)
            else -> false
        }
    }

    private fun matchesClassExpression(classReference: PhpExpression?, subjectSet: SubjectSet): Boolean {
        return classReference is PhpReference && subjectSet.matchesClass(classReference.fqn)
    }

    private fun matchesMethodReference(methodReference: MethodReference, subjectSet: SubjectSet): Boolean {
        val methodName = methodReference.name ?: return false
        if (!subjectSet.hasMethodName(methodName)) {
            return false
        }

        for (resolveResult in methodReference.multiResolve(false)) {
            val method = resolveResult.element as? Method ?: continue
            val containingClass = method.containingClass ?: continue

            if (subjectSet.matchesMethod(containingClass.fqn, method.name)) {
                return true
            }
        }

        val classReference = methodReference.classReference
        if (classReference is PhpReference && subjectSet.matchesMethod(classReference.fqn, methodName)) {
            return true
        }

        return false
    }

    private fun matchesStringLiteral(literal: StringLiteralExpression, subjectSet: SubjectSet): Boolean {
        if (matchesReferenceResolvedStringLiteral(literal, subjectSet)) {
            return true
        }

        if (matchesMockClassStringLiteral(literal, subjectSet)) {
            return true
        }

        for ((className, methodNames) in findPartialMockMethods(literal)) {
            if (methodNames.any { subjectSet.matchesMethod(className, it) }) {
                return true
            }
        }

        val contents = literal.contents
        if (contents.isBlank() || !subjectSet.hasMethodName(contents)) {
            return false
        }

        PhpUnitPluginUtil.findCreateMockParameterOnParameterScope(literal)
            ?.let { className ->
                if (subjectSet.matchesMethod(className, contents)) {
                    return true
                }
            }

        for (className in findMockeryMockClasses(literal)) {
            if (subjectSet.matchesMethod(className, contents)) {
                return true
            }
        }

        return false
    }

    private fun matchesReferenceResolvedStringLiteral(literal: StringLiteralExpression, subjectSet: SubjectSet): Boolean {
        for (reference in literal.references) {
            if (reference is PsiPolyVariantReference) {
                for (resolveResult in reference.multiResolve(false)) {
                    if (matchesResolvedMethod(resolveResult.element, subjectSet)) {
                        return true
                    }
                }

                continue
            }

            if (matchesResolvedMethod(reference.resolve(), subjectSet)) {
                return true
            }
        }

        return false
    }

    private fun matchesResolvedMethod(element: PsiElement?, subjectSet: SubjectSet): Boolean {
        val method = element as? Method ?: return false
        val containingClass = method.containingClass ?: return false

        return subjectSet.matchesMethod(containingClass.fqn, method.name)
    }

    private fun matchesMockClassStringLiteral(literal: StringLiteralExpression, subjectSet: SubjectSet): Boolean {
        val className = literal.contents.takeIf { it.isNotBlank() } ?: return false
        if (!subjectSet.matchesClass(className)) {
            return false
        }

        val methodReference = PsiTreeUtil.getParentOfType(literal, MethodReference::class.java) ?: return false
        val parameterList = literal.parent as? ParameterList ?: return false
        val parameterIndex = parameterList.parameters.indexOfFirst { it == literal }

        if (parameterIndex != 0) {
            return false
        }

        return isMockClassFactoryMethod(methodReference)
    }

    private fun isMockClassFactoryMethod(methodReference: MethodReference): Boolean {
        return PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "\\PHPUnit\\Framework\\TestCase", "createMock") ||
            PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "PHPUnit_Framework_TestCase", "createMock") ||
            PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "\\PHPUnit\\Framework\\TestCase", "createPartialMock") ||
            PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "PHPUnit_Framework_TestCase", "createPartialMock") ||
            PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "\\PHPUnit\\Framework\\TestCase", "getMockBuilder") ||
            PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "PHPUnit_Framework_TestCase", "getMockBuilder") ||
            PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "Mockery", "mock")
    }

    private fun findMockeryMockClasses(literal: StringLiteralExpression): List<String> {
        return sequenceOf(
            MockeryReferencingUtil.findMockeryMockParametersOnParameterScope(literal),
            MockeryReferencingUtil.findMockeryMockParametersOnArrayHashScope(literal),
            MockeryReferencingUtil.findMockeryMockParametersOnArrayElementScope(literal),
        )
            .filterNotNull()
            .flatMap { it.asSequence() }
            .mapNotNull { it?.takeIf { value -> value.isNotBlank() } }
            .toList()
    }

    private fun findPartialMockMethods(literal: StringLiteralExpression): List<Pair<String, List<String>>> {
        return sequenceOf(
            MockeryReferencingUtil.findMockeryMockParametersOnPartialMockStringDeclarationScope(literal),
            MockeryReferencingUtil.findMockeryMockParametersOnPartialMockConcatenationDeclarationScope(literal),
        )
            .filterNotNull()
            .filter { it.size > 1 }
            .mapNotNull { values ->
                val className = values[0]?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                val methodNames = values.drop(1).mapNotNull { it?.takeIf { value -> value.isNotBlank() } }
                if (methodNames.isEmpty()) {
                    null
                } else {
                    className to methodNames
                }
            }
            .toList()
    }

    private fun isTestClass(phpClass: PhpClass): Boolean {
        return PhpUnitUtil.isTestClass(phpClass) || PhpUnitPluginUtil.isTestClassWithoutIndexAccess(phpClass)
    }

    private fun testCaseName(testClass: PhpClass, usageElement: PsiElement): String {
        val className = normalizeClassName(testClass.fqn)
        val method = PsiTreeUtil.getParentOfType(usageElement, Method::class.java)

        if (method != null && method.containingClass == testClass && PhpUnitUtil.isTestMethod(testClass, method)) {
            return "$className::${method.name}"
        }

        return className
    }

    private data class SearchSubject(
        val className: String,
        val methodName: String?,
        val allowShortClassName: Boolean,
    ) {
        val classShortName: String = className.substringAfterLast('\\')
        val methodNameLower: String? = methodName?.lowercase(Locale.ROOT)
    }

    private class SubjectSet(subjects: Set<SearchSubject>) {
        private val allSubjects = subjects.toList()
        private val classSubjects = allSubjects.filter { it.methodName == null }
        private val methodSubjectsByName = allSubjects
            .filter { it.methodName != null }
            .groupBy { it.methodNameLower!! }

        val searchWords: Set<String> = buildSet {
            for (subject in allSubjects) {
                subject.classShortName.takeIf { it.isNotBlank() }?.let(::add)
                subject.methodName?.takeIf { it.isNotBlank() }?.let(::add)
            }
        }

        fun matchesClass(className: String?): Boolean {
            val normalized = normalizeClassName(className ?: return false)
            val shortName = normalized.substringAfterLast('\\')

            return classSubjects.any { it.matchesClass(normalized, shortName) }
        }

        fun hasMethodName(methodName: String): Boolean {
            return methodSubjectsByName.containsKey(methodName.lowercase(Locale.ROOT))
        }

        fun matchesMethod(className: String?, methodName: String?): Boolean {
            val normalizedMethod = methodName?.lowercase(Locale.ROOT) ?: return false
            val candidates = methodSubjectsByName[normalizedMethod] ?: return false
            val normalizedClass = normalizeClassName(className ?: return false)
            val shortName = normalizedClass.substringAfterLast('\\')

            return candidates.any { it.matchesClass(normalizedClass, shortName) }
        }

        private fun SearchSubject.matchesClass(normalizedClass: String, shortName: String): Boolean {
            if (className.equals(normalizedClass, ignoreCase = true)) {
                return true
            }

            return allowShortClassName && classShortName.equals(shortName, ignoreCase = true)
        }
    }

    private data class TargetSelector(
        val className: SelectorValue,
        val methodName: SelectorValue?,
    ) {
        val needsClassEnumeration: Boolean =
            className.isRegex || className.isShortClassName || methodName?.isRegex == true

        fun matchesClass(fqn: String): Boolean {
            val normalized = normalizeClassName(fqn)
            val shortName = normalized.substringAfterLast('\\')

            return if (className.isRegex) {
                className.matches(normalized) || className.matches(StringUtils.stripStart(normalized, "\\")) || className.matches(shortName)
            } else if (className.isShortClassName) {
                className.text.equals(shortName, ignoreCase = true)
            } else {
                normalizeClassName(className.text).equals(normalized, ignoreCase = true)
            }
        }

    }

    private data class SelectorValue(
        val text: String,
        val pattern: Pattern?,
    ) {
        val isRegex: Boolean = pattern != null
        val isShortClassName: Boolean = !isRegex && '\\' !in text

        fun matches(value: String): Boolean {
            return pattern?.matcher(value)?.find() ?: text.equals(value, ignoreCase = true)
        }
    }

    private fun parseTargetSelector(value: String): TargetSelector {
        val methodSeparator = value.lastIndexOf(':')
        val classPart = if (methodSeparator >= 0) value.substring(0, methodSeparator) else value
        val methodPart = if (methodSeparator >= 0) value.substring(methodSeparator + 1) else null

        return TargetSelector(
            parseSelectorValue(classPart.trim()),
            methodPart?.trim()?.takeIf { it.isNotBlank() }?.let(::parseSelectorValue),
        )
    }

    private fun parseSelectorValue(value: String): SelectorValue {
        val trimmed = value.trim()
        val regex = parseRegexSelector(trimmed)
        return SelectorValue(
            trimmed.takeUnless { regex != null } ?: trimmed.substring(1, trimmed.length - 1),
            regex,
        )
    }

    private fun parseRegexSelector(value: String): Pattern? {
        if (value.length < 2 || !value.startsWith("/") || !value.endsWith("/")) {
            return null
        }

        return try {
            Pattern.compile(value.substring(1, value.length - 1), Pattern.CASE_INSENSITIVE)
        } catch (_: PatternSyntaxException) {
            null
        }
    }
}

private fun normalizeClassName(className: String): String {
    return "\\" + StringUtils.stripStart(className.trim(), "\\")
}
