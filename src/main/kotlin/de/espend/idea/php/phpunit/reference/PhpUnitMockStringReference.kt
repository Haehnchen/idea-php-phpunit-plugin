package de.espend.idea.php.phpunit.reference

import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.util.IncorrectOperationException
import com.jetbrains.php.lang.parser.PhpElementTypes
import com.jetbrains.php.lang.psi.PhpPsiElementFactory
import de.espend.idea.php.phpunit.utils.mockstring.FilterFactory

/**
 * Navigates and renames supported PHPUnit mock method/property strings.
 *
 * <pre>
 * $mock->method('methodName');
 * PHPUnit_Helper::callProtectedMethod(Foo::class, 'protectedMethod');
 * </pre>
 */
open class PhpUnitMockStringReference(private val psiElement: PsiElement) : PsiReference {
    override fun getElement(): PsiElement {
        return psiElement
    }

    override fun getRangeInElement(): TextRange {
        return TextRange(1, element.textLength - 1)
    }

    override fun resolve(): PsiElement? {
        var resolvedElement: PsiElement? = null
        val filter = FilterFactory.getFilter(element)
        if (filter != null) {
            val phpClass = filter.phpClass
            if (phpClass != null) {
                val name = getName()
                resolvedElement = phpClass.findMethodByName(name)
                if (resolvedElement == null) {
                    resolvedElement = phpClass.findFieldByName(name, false)
                }
            }
        }

        return resolvedElement
    }

    override fun getCanonicalText(): String {
        return getName()
    }

    @Throws(IncorrectOperationException::class)
    override fun handleElementRename(s: String): PsiElement {
        val element = element
        val nameNode = element.node
        if (nameNode != null && canonicalText != s) {
            val replacement = getText().replace(getName(), s)
            val node = PhpPsiElementFactory.createFromText(element.project, PhpElementTypes.STRING, replacement).node
            nameNode.treeParent.replaceChild(nameNode, node)
        }

        return element
    }

    @Throws(IncorrectOperationException::class)
    override fun bindToElement(psiElement: PsiElement): PsiElement? {
        return null
    }

    override fun isReferenceTo(psiElement: PsiElement): Boolean {
        val resolvedElement = resolve()
        return resolvedElement != null && resolvedElement == psiElement
    }

    override fun getVariants(): Array<Any> {
        return emptyArray()
    }

    override fun isSoft(): Boolean {
        return false
    }

    protected open fun getName(): String {
        return StringUtil.unquoteString(getText())
    }

    protected open fun getText(): String {
        return element.text
    }
}
