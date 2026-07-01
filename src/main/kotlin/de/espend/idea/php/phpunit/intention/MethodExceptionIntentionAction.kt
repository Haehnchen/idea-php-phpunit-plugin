package de.espend.idea.php.phpunit.intention

import com.intellij.codeInsight.hint.HintManager
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.codeInsight.intention.preview.IntentionPreviewUtils
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.Iconable
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementVisitor
import com.intellij.psi.PsiReference
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.codeInsight.PhpScopeHolder
import com.jetbrains.php.lang.inspections.PhpThrownExceptionsAnalyzer
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.phpunit.PhpUnitUtil
import de.espend.idea.php.phpunit.PhpUnitIcons
import de.espend.idea.php.phpunit.utils.PhpUnitPluginUtil
import one.util.streamex.StreamEx
import org.apache.commons.lang3.StringUtils
import org.jetbrains.annotations.Nls
import java.util.ArrayList
import java.util.HashSet
import java.util.LinkedHashSet
import java.util.stream.Collectors
import javax.swing.Icon

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
class MethodExceptionIntentionAction : PsiElementBaseIntentionAction(), Iconable {
    override fun invoke(project: Project, editor: Editor, psiElement: PsiElement) {
        val method = getMethodScope(psiElement)
        if (method == null) {
            return
        }

        val exceptions = getException(method)
        if (exceptions.size == 0) {
            if (!IntentionPreviewUtils.isPreviewElement(psiElement)) {
                HintManager.getInstance().showErrorHint(editor, "No exception in method scope found")
            }

            return
        }

        if (IntentionPreviewUtils.isPreviewElement(psiElement) || exceptions.size == 1) {
            PhpUnitPluginUtil.insertExpectedException(method, psiElement, exceptions.iterator().next())

            return
        }

        val list = ArrayList(exceptions)

        JBPopupFactory.getInstance().createPopupChooserBuilder(list)
            .setTitle("PHPUnit: Select Exception")
            .setItemChosenCallback { s ->
                WriteCommandAction.runWriteCommandAction(
                    psiElement.project,
                    text,
                    "",
                    Runnable { PhpUnitPluginUtil.insertExpectedException(method, psiElement, s) },
                    psiElement.containingFile
                )
            }
            .createPopup()
            .showInBestPositionFor(editor)
    }

    override fun isAvailable(project: Project, editor: Editor, psiElement: PsiElement): Boolean {
        return getMethodScope(psiElement) != null
    }

    private fun getMethodScope(psiElement: PsiElement): Method? {
        val method = PsiTreeUtil.getParentOfType(psiElement, Method::class.java)
        if (method != null && PhpUnitUtil.isTestMethod(method)) {
            return method
        }

        return null
    }

    @Nls
    override fun getFamilyName(): String {
        return "PHPUnit"
    }

    override fun getText(): String {
        return "PHPUnit: Expected exception"
    }

    private fun getException(psiElement: PsiElement): Set<String> {
        val methodReferences: MutableCollection<MethodReference> = HashSet()

        psiElement.acceptChildren(object : PsiRecursiveElementVisitor() {
            override fun visitElement(element: PsiElement) {
                if (element is MethodReference) {
                    methodReferences.add(element)
                }

                super.visitElement(element)
            }
        })

        return StreamEx.of(methodReferences)
            .map(PsiReference::resolve)
            .select(PhpScopeHolder::class.java)
            .flatCollection(PhpThrownExceptionsAnalyzer::getExceptionClasses)
            .map { phpType -> StringUtils.stripStart(phpType.toString(), "\\")!! }
            .filter { s -> !s.lowercase().contains("phpunit") }
            .sorted()
            .collect(Collectors.toCollection { LinkedHashSet() })
    }

    override fun getIcon(flags: Int): Icon {
        return PhpUnitIcons.PHPUNIT
    }
}
