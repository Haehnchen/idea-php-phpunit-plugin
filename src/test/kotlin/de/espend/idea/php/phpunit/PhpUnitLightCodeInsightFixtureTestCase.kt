package de.espend.idea.php.phpunit

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.daemon.LineMarkerProviders
import com.intellij.codeInsight.intention.IntentionManager
import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionEP
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.Pair
import com.intellij.patterns.ElementPattern
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.PsiRecursiveElementVisitor
import com.intellij.psi.PsiReference
import com.intellij.psi.ResolveResult
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpReference
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import java.util.HashSet

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
abstract class PhpUnitLightCodeInsightFixtureTestCase : LightJavaCodeInsightFixtureTestCase() {
    fun assertCompletionContains(
        languageFileType: LanguageFileType,
        configureByText: String,
        vararg lookupStrings: String
    ) {
        myFixture.configureByText(languageFileType, configureByText)
        myFixture.completeBasic()

        completionContainsAssert(lookupStrings)
    }

    private fun completionContainsAssert(lookupStrings: Array<out String>) {
        if (lookupStrings.isEmpty()) {
            fail("No lookup element given")
        }

        val lookupElements = myFixture.lookupElementStrings
        if (lookupElements == null || lookupElements.size == 0) {
            fail("failed that empty completion contains ${lookupStrings.contentToString()}")
        }

        for (s in lookupStrings.asList()) {
            if (!lookupElements!!.contains(s)) {
                fail("failed that completion contains $s in $lookupElements")
            }
        }
    }

    fun assertLineMarker(psiElement: PsiElement, assertMatch: LineMarker.Assert) {
        val elements = collectPsiElementsRecursive(psiElement)

        for (lineMarkerProvider: LineMarkerProvider in LineMarkerProviders.getInstance().allForLanguage(psiElement.language)) {
            val lineMarkerInfos: MutableCollection<LineMarkerInfo<*>> = ArrayList()
            lineMarkerProvider.collectSlowLineMarkers(elements, lineMarkerInfos)

            if (lineMarkerInfos.size == 0) {
                continue
            }

            for (lineMarkerInfo in lineMarkerInfos) {
                if (assertMatch.match(lineMarkerInfo)) {
                    return
                }
            }
        }

        fail("Fail that '${assertMatch.javaClass}' matches on of '${elements.size}' PsiElements")
    }

    fun assertIntentionIsAvailable(languageFileType: LanguageFileType, configureByText: String, intentionText: String) {
        myFixture.configureByText(languageFileType, configureByText)
        val psiElement = myFixture.file.findElementAt(myFixture.caretOffset)!!

        val items: MutableSet<String> = HashSet()

        for (intentionAction in IntentionManager.getInstance().intentionActions) {
            if (!intentionAction.isAvailable(project, editor, psiElement.containingFile)) {
                continue
            }

            val text = intentionAction.text
            items.add(text)

            if (text != intentionText) {
                continue
            }

            return
        }

        fail("Fail intention action '$intentionText' is available in element '${psiElement.text}' with '$items'")
    }

    fun assertPhpReferenceResolveTo(
        languageFileType: LanguageFileType,
        configureByText: String,
        pattern: ElementPattern<*>
    ) {
        myFixture.configureByText(languageFileType, configureByText)
        var psiElement: PsiElement? = myFixture.file.findElementAt(myFixture.caretOffset)

        psiElement = PsiTreeUtil.getParentOfType(psiElement, PhpReference::class.java)
        if (psiElement == null) {
            fail("Element is not PhpReference.")
        }

        val resolve = (psiElement as PhpReference).resolve()
        if (!pattern.accepts(resolve)) {
            fail("failed pattern matches element of '${resolve?.toString() ?: "null"}'")
        }

        assertTrue(pattern.accepts(resolve))
    }

    fun assertPhpReferenceNotResolveTo(
        languageFileType: LanguageFileType,
        configureByText: String,
        pattern: ElementPattern<*>
    ) {
        myFixture.configureByText(languageFileType, configureByText)
        var psiElement: PsiElement? = myFixture.file.findElementAt(myFixture.caretOffset)

        psiElement = PsiTreeUtil.getParentOfType(psiElement, PhpReference::class.java)
        if (psiElement == null) {
            fail("Element is not PhpReference.")
        }

        assertFalse(pattern.accepts((psiElement as PhpReference).resolve()))
    }

    fun assertReferencesMatch(
        languageFileType: LanguageFileType,
        configureByText: String,
        pattern: ElementPattern<*>
    ) {
        myFixture.configureByText(languageFileType, configureByText)
        val psiElement = myFixture.file.findElementAt(myFixture.caretOffset)!!

        // get parent for references; mostly we are inside a token element
        val parent = psiElement.parent

        for (psiReference: PsiReference in parent.references) {
            // multi resolve
            if (psiReference is PsiPolyVariantReference) {
                for (resolveResult: ResolveResult in psiReference.multiResolve(true)) {
                    val element = resolveResult.element
                    if (pattern.accepts(element)) {
                        return
                    }
                }
            }

            // single result
            val resolve = psiReference.resolve() ?: continue

            if (pattern.accepts(resolve)) {
                return
            }
        }

        fail("Failed pattern matches element of '${parent.references.size}' elements")
    }

    fun assertLocalInspectionContains(filename: String, content: String, contains: String) {
        val matches: MutableSet<String> = HashSet()

        val localInspectionsAtCaret = getLocalInspectionsAtCaret(filename, content)
        for (result: ProblemDescriptor in localInspectionsAtCaret.first) {
            val textRange = result.psiElement.textRange
            if (textRange.contains(localInspectionsAtCaret.second) && result.toString() == contains) {
                return
            }

            matches.add(result.toString())
        }

        fail("Fail matches '$contains' with one of $matches")
    }

    private fun getLocalInspectionsAtCaret(filename: String, content: String): Pair<List<ProblemDescriptor>, Int> {
        val psiFile = myFixture.configureByText(filename, content)

        val caretOffset = myFixture.caretOffset
        if (caretOffset <= 0) {
            fail("Please provide <caret> tag")
        }

        val problemsHolder = ProblemsHolder(InspectionManager.getInstance(project), psiFile.containingFile, false)

        for (localInspectionEP in LocalInspectionEP.LOCAL_INSPECTION.extensionList) {
            val instance = localInspectionEP.instance
            if (instance !is LocalInspectionTool) {
                continue
            }

            val psiElementVisitor: PsiElementVisitor = instance.buildVisitor(problemsHolder, false)

            psiFile.acceptChildren(object : PsiRecursiveElementVisitor() {
                override fun visitElement(element: PsiElement) {
                    psiElementVisitor.visitElement(element)
                    super.visitElement(element)
                }
            })

            psiElementVisitor.visitFile(psiFile.containingFile)
        }

        return Pair.create(problemsHolder.results, caretOffset)
    }

    fun assertMethodContainsTypes(
        languageFileType: LanguageFileType,
        configureByText: String,
        vararg types: String
    ) {
        myFixture.configureByText(languageFileType, configureByText)
        var psiElement: PsiElement? = myFixture.file.findElementAt(myFixture.caretOffset)

        psiElement = PsiTreeUtil.getParentOfType(psiElement, PhpTypedElement::class.java)
        if (psiElement == null) {
            fail("Element is not a PhpTypedElement.")
        }

        val phpType: PhpType = PhpIndex.getInstance(psiElement!!.project).completeType(
            psiElement.project,
            psiElement.type,
            HashSet()
        )

        assertContainsElements(phpType.types, *types)
    }

    private fun collectPsiElementsRecursive(psiElement: PsiElement): List<PsiElement> {
        val elements: MutableList<PsiElement> = ArrayList()
        elements.add(psiElement.containingFile)

        psiElement.acceptChildren(object : PsiRecursiveElementVisitor() {
            override fun visitElement(element: PsiElement) {
                elements.add(element)
                super.visitElement(element)
            }
        })
        return elements
    }

    @Suppress("unused")
    class LineMarker {
        interface Assert {
            fun match(markerInfo: LineMarkerInfo<*>): Boolean
        }

        class ToolTipEqualsAssert(private val toolTip: String) : Assert {
            override fun match(markerInfo: LineMarkerInfo<*>): Boolean {
                return markerInfo.lineMarkerTooltip != null && markerInfo.lineMarkerTooltip == toolTip
            }
        }
    }
}
