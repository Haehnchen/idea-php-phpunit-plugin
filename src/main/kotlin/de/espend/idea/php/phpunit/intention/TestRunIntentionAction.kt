package de.espend.idea.php.phpunit.intention

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Iconable
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.phpunit.PhpUnitUtil
import de.espend.idea.php.phpunit.PhpUnitIcons
import de.espend.idea.php.phpunit.utils.PhpUnitPluginUtil
import org.jetbrains.annotations.Nls
import javax.swing.Icon

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
class TestRunIntentionAction : PsiElementBaseIntentionAction(), Iconable {
    override fun invoke(project: Project, editor: Editor, psiElement: PsiElement) {
        val context = getTestContextElement(psiElement)
        if (context != null) {
            PhpUnitPluginUtil.executeDebugRunner(psiElement)
        }
    }

    override fun isAvailable(project: Project, editor: Editor, psiElement: PsiElement): Boolean {
        return getTestContextElement(psiElement) != null
    }

    private fun getTestContextElement(psiElement: PsiElement): PsiElement? {
        val method = PsiTreeUtil.getStubOrPsiParentOfType(psiElement, Method::class.java)
        if (method != null && PhpUnitUtil.isTestMethod(method)) {
            return method
        }

        val phpClass = PsiTreeUtil.getStubOrPsiParentOfType(psiElement, PhpClass::class.java)
        if (phpClass != null && PhpUnitUtil.isTestClass(phpClass)) {
            return phpClass
        }

        return null
    }

    @Nls
    override fun getFamilyName(): String {
        return "PHPUnit"
    }

    override fun getText(): String {
        return "PHPUnit: Run Test"
    }

    override fun startInWriteAction(): Boolean {
        return false
    }

    override fun getIcon(flags: Int): Icon {
        return PhpUnitIcons.PHPUNIT
    }
}
