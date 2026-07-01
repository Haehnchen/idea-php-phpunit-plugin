package de.espend.idea.php.phpunit.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

open class PhpClassMethodReference(
    psiElement: StringLiteralExpression,
    private val method: String,
    private val clazz: String
) : PsiPolyVariantReferenceBase<PsiElement>(psiElement) {
    override fun multiResolve(b: Boolean): Array<ResolveResult> {
        val resolveResults: MutableCollection<ResolveResult> = ArrayList()

        for (phpClass in PhpIndex.getInstance(element.project).getAnyByFQN(clazz)) {
            val method = phpClass.findMethodByName(method)
            if (method != null) {
                resolveResults.add(PsiElementResolveResult(method))
            }
        }

        return resolveResults.toTypedArray()
    }

    override fun getVariants(): Array<Any> {
        return emptyArray()
    }
}
