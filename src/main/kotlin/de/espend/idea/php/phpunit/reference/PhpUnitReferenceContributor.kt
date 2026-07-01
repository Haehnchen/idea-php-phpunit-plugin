package de.espend.idea.php.phpunit.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import de.espend.idea.php.phpunit.utils.PatternUtil
import de.espend.idea.php.phpunit.utils.PhpUnitPluginUtil
import org.apache.commons.lang3.StringUtils

open class PhpUnitReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(psiReferenceRegistrar: PsiReferenceRegistrar) {
        psiReferenceRegistrar.registerReferenceProvider(
            PatternUtil.getMethodReferenceWithParameterPattern(),
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(
                    psiElement: PsiElement,
                    processingContext: ProcessingContext
                ): Array<PsiReference> {
                    if (psiElement is StringLiteralExpression) {
                        val contents = psiElement.contents
                        if (StringUtils.isNotBlank(contents)) {
                            val parameter = PhpUnitPluginUtil.findCreateMockParameterOnParameterScope(psiElement)
                            if (parameter != null) {
                                return arrayOf(PhpClassMethodReference(psiElement, contents, parameter))
                            }
                        }
                    }
                    return emptyArray()
                }
            }
        )
    }
}
