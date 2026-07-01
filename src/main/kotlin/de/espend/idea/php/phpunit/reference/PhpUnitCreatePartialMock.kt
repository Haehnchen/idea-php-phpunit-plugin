package de.espend.idea.php.phpunit.reference

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import de.espend.idea.php.phpunit.utils.PatternUtil
import de.espend.idea.php.phpunit.utils.PhpElementsUtil
import de.espend.idea.php.phpunit.utils.PhpUnitPluginUtil
import org.apache.commons.lang3.StringUtils

/**
 * "$this->createPartialMock(Foo::class, ['foobar']);"
 */
open class PhpUnitCreatePartialMock {
    open class Completion : CompletionContributor() {
        init {
            extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement().withParent(PatternUtil.getArrayParameterPattern()),
                object : CompletionProvider<CompletionParameters>() {
                    override fun addCompletions(
                        completionParameters: CompletionParameters,
                        processingContext: ProcessingContext,
                        resultSet: CompletionResultSet
                    ) {
                        val psiElement1 = completionParameters.position

                        val psiElement = psiElement1.parent
                        if (psiElement !is StringLiteralExpression) {
                            return
                        }

                        val parentOfType = PsiTreeUtil.getParentOfType(psiElement, MethodReference::class.java)
                        if (parentOfType == null) {
                            return
                        }

                        if (PhpUnitPluginUtil.isCreatePartialMockMethod(parentOfType)) {
                            val originalClassName = parentOfType.getParameter("originalClassName", 0)

                            if (originalClassName == null) {
                                return
                            }

                            val stringValue = PhpElementsUtil.getStringValue(originalClassName)
                            if (StringUtils.isBlank(stringValue)) {
                                return
                            }

                            resultSet.addAllElements(PhpUnitPluginUtil.getMockableMethods(psiElement.project, stringValue!!))
                        }
                    }
                }
            )
        }
    }

    open class ReferenceContributor : PsiReferenceContributor() {
        override fun registerReferenceProviders(psiReferenceRegistrar: PsiReferenceRegistrar) {
            psiReferenceRegistrar.registerReferenceProvider(
                PatternUtil.getArrayParameterPattern(),
                object : PsiReferenceProvider() {
                    override fun getReferencesByElement(
                        psiElement: PsiElement,
                        processingContext: ProcessingContext
                    ): Array<PsiReference> {
                        if (psiElement !is StringLiteralExpression) {
                            return emptyArray()
                        }

                        val contents = psiElement.contents
                        if (contents.isBlank()) {
                            return emptyArray()
                        }

                        val parentOfType = PsiTreeUtil.getParentOfType(psiElement, MethodReference::class.java)
                        if (parentOfType == null) {
                            return emptyArray()
                        }

                        if (PhpUnitPluginUtil.isCreatePartialMockMethod(parentOfType)) {
                            val originalClassName = parentOfType.getParameter("originalClassName", 0)

                            if (originalClassName == null) {
                                return emptyArray()
                            }

                            val stringValue = PhpElementsUtil.getStringValue(originalClassName)
                            if (StringUtils.isBlank(stringValue)) {
                                return emptyArray()
                            }

                            return arrayOf(PhpClassMethodReference(psiElement, contents, stringValue!!))
                        }

                        return emptyArray()
                    }
                }
            )
        }
    }
}
