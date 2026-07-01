package de.espend.idea.php.phpunit.utils

import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiWhiteSpace
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.codeInsight.PhpCodeInsightUtil
import com.jetbrains.php.lang.PhpLanguage
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.psi.elements.ClassConstantReference
import com.jetbrains.php.lang.psi.elements.ConcatenationExpression
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.NewExpression
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpExpression
import com.jetbrains.php.lang.psi.elements.PhpPsiElement
import com.jetbrains.php.lang.psi.elements.PhpReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.refactoring.PhpAliasImporter
import org.apache.commons.lang3.StringUtils

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
class PhpElementsUtil {
    companion object {
        fun getStringValue(psiElement: PsiElement?): String? {
            return getStringValue(psiElement, 0)
        }

        /**
         * <code>Foo::class</code> to its class fqn include namespace
         */
        fun getClassConstantPhpFqn(classConstant: ClassConstantReference): String? {
            val classReference: PhpExpression? = classConstant.getClassReference()
            if (classReference !is PhpReference) {
                return null
            }

            val typeName = classReference.getFQN()
            return if (StringUtils.isNotBlank(typeName)) StringUtils.stripStart(typeName, "\\") else null
        }

        /**
         * <code>new Foo</code> to its class fqn include namespace
         */
        fun getNewExpressionPhpFqn(newExpression: NewExpression): String? {
            val classReference: PhpReference = newExpression.getClassReference() ?: return null

            val typeName = classReference.getFQN()
            return if (StringUtils.isNotBlank(typeName)) StringUtils.stripStart(typeName, "\\") else null
        }

        private fun getStringValue(psiElement: PsiElement?, depth: Int): String? {
            var currentDepth = depth
            if (psiElement == null || ++currentDepth > 5) {
                return null
            }

            if (psiElement is StringLiteralExpression) {
                val resolvedString = psiElement.contents
                if (StringUtils.isEmpty(resolvedString)) {
                    return null
                }

                return resolvedString
            } else if (psiElement is Field) {
                return getStringValue(psiElement.defaultValue, currentDepth)
            } else if (psiElement is ClassConstantReference && "class" == psiElement.name) {
                // Foobar::class
                return getClassConstantPhpFqn(psiElement)
            } else if (psiElement is PhpReference) {
                val psiReference: PsiReference = psiElement.reference ?: return null

                val ref = psiReference.resolve()
                if (ref is PhpReference) {
                    return getStringValue(psiElement, currentDepth)
                }

                if (ref is Field) {
                    return getStringValue(ref.defaultValue)
                }
            } else if (psiElement is NewExpression) {
                return getNewExpressionPhpFqn(psiElement)
            } else if (psiElement is ConcatenationExpression) {
                // Allows creation method like: Mockery::mock(Dependency::class . "[calledMethod]");
                val concatString = StringBuilder()

                for (e in psiElement.children) {
                    concatString.append(getStringValue(e, currentDepth))
                }
                return concatString.toString()
            }

            return null
        }

        /**
         * @param subjectClass eg DateTime
         * @param expectedClass eg DateTimeInterface
         */
        fun isInstanceOf(subjectClass: PhpClass, expectedClass: String): Boolean {
            return PhpType().add(expectedClass).isConvertibleFrom(
                PhpType().add(subjectClass),
                PhpIndex.getInstance(subjectClass.project)
            )
        }

        /**
         * Resolves MethodReference and compare containing class against implementations instances
         */
        fun isMethodReferenceInstanceOf(methodReference: MethodReference, vararg expectedClassNameAsOr: String): Boolean {
            for (resolveResult in methodReference.multiResolve(false)) {
                val resolve = resolveResult.element

                if (resolve !is Method) {
                    continue
                }

                val containingClass = resolve.containingClass
                if (containingClass == null) {
                    continue
                }

                for (expectedClassName in expectedClassNameAsOr) {
                    if (isInstanceOf(containingClass, expectedClassName)) {
                        return true
                    }
                }
            }

            return false
        }

        /**
         * Try to match the class instance of the method references without calling any phpindex to be usable in index process
         *
         * Note: only local file is taken so only direct instances are detected
         */
        fun isLocalResolveMethodReferenceInstanceOf(
            methodReference: MethodReference,
            vararg expectedClassNameAsOr: String
        ): Boolean {
            val classReference = methodReference.getClassReference()
            if (classReference != null) {
                for (type in classReference.getType().getTypes()) {
                    // check the class name based on the type; also normalize any slashes
                    val typeFqn = "\\" + StringUtils.stripStart(type, "\\")
                    for (expected in expectedClassNameAsOr) {
                        val expectedFqn = "\\" + StringUtils.stripStart(expected, "\\")
                        if (expectedFqn.equals(typeFqn, ignoreCase = true)) {
                            return true
                        }
                    }
                }
            }

            return false
        }

        /**
         * Get the position of the given element to its parent ParameterList
         */
        fun getParameterIndex(parameterList: ParameterList, parameter: PsiElement): Int? {
            val parameters = parameterList.parameters

            var i = 0
            while (i < parameters.size) {
                if (parameters[i] == parameter) {
                    return i
                }
                i += 1
            }

            return null
        }

        /**
         * Resolves MethodReference and compare containing class against implementations instances
         */
        fun isMethodReferenceInstanceOf(
            methodReference: MethodReference,
            expectedClassName: String,
            methodName: String
        ): Boolean {
            if (methodName != methodReference.name) {
                return false
            }

            return isMethodReferenceInstanceOf(methodReference, expectedClassName)
        }

        fun insertUseIfNecessary(scope: PsiElement, fqnClasName: String): String? {
            var className = fqnClasName
            if (!className.startsWith("\\")) {
                className = "\\$className"
            }

            val scopeForUseOperator: PhpPsiElement = PhpCodeInsightUtil.findScopeForUseOperator(scope) ?: return null

            if (!PhpCodeInsightUtil.getAliasesInScope(scopeForUseOperator).containsValue(className)) {
                PhpAliasImporter.insertUseStatement(className, scopeForUseOperator)
            }

            for (entry in PhpCodeInsightUtil.getAliasesInScope(scopeForUseOperator).entries) {
                if (className == entry.value) {
                    return entry.key
                }
            }

            return null
        }

        /**
         * class "Foo" extends
         */
        fun getClassNamePattern(): PsiElementPattern.Capture<PsiElement> {
            return PlatformPatterns
                .psiElement(PhpTokenTypes.IDENTIFIER)
                .afterLeafSkipping(
                    PlatformPatterns.psiElement(PsiWhiteSpace::class.java),
                    PlatformPatterns.psiElement(PhpTokenTypes.kwCLASS)
                )
                .withParent(PhpClass::class.java)
                .withLanguage(PhpLanguage.INSTANCE)
        }
    }
}
