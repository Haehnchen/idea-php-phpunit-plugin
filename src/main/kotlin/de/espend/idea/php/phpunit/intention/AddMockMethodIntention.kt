package de.espend.idea.php.phpunit.intention

import com.intellij.codeInsight.hint.HintManager
import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.codeInsight.intention.preview.IntentionPreviewUtils
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.Iconable
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.PhpPsiElementFactory
import com.jetbrains.php.lang.psi.elements.AssignmentExpression
import com.jetbrains.php.lang.psi.elements.FieldReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.elements.PhpReference
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.Variable
import de.espend.idea.php.phpunit.PhpUnitIcons
import de.espend.idea.php.phpunit.utils.processor.MethodReferenceNameProcessor
import org.jetbrains.annotations.Nls
import java.util.ArrayList
import java.util.TreeSet
import javax.swing.Icon

/**
 * Add mock based on given context:
 * $foobar->method('foobar')->willReturn();
 *
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
class AddMockMethodIntention : PsiElementBaseIntentionAction(), Iconable, HighPriorityAction {
    override fun invoke(project: Project, editor: Editor, psiElement: PsiElement) {
        val parameter = getMockInstanceFromMethodReferenceScope(psiElement)

        if (parameter == null) {
            if (!IntentionPreviewUtils.isPreviewElement(psiElement)) {
                HintManager.getInstance().showErrorHint(editor, "No mock context found")
            }

            return
        }

        val methods = TreeSet<String>()
        for (phpClass in PhpIndex.getInstance(psiElement.project).getAnyByFQN(parameter)) {
            methods.addAll(
                phpClass.methods
                    .filter { method -> method.access.isPublic && !method.name.startsWith("__") && !method.isStatic && !method.isFinal }
                    .map(PhpNamedElement::getName)
                    .toSet()
            )
        }

        if (methods.size == 0) {
            if (!IntentionPreviewUtils.isPreviewElement(psiElement)) {
                HintManager.getInstance().showErrorHint(editor, "No public method found")
            }

            return
        }

        if (IntentionPreviewUtils.isPreviewElement(psiElement)) {
            MyMockWriteCommand(editor, methods, psiElement, false).run()

            return
        }

        // Single item direct execution without selection
        if (methods.size == 1) {
            WriteCommandAction.runWriteCommandAction(
                psiElement.project,
                text,
                "",
                MyMockWriteCommand(editor, ArrayList(methods), psiElement, true),
                psiElement.containingFile
            )

            return
        }

        val list = ArrayList(methods)

        JBPopupFactory.getInstance().createPopupChooserBuilder(list)
            .setTitle("PHPUnit: Mock Method")
            .setItemsChosenCallback { strings ->
                WriteCommandAction.runWriteCommandAction(
                    psiElement.project,
                    text,
                    "",
                    MyMockWriteCommand(editor, ArrayList(strings), psiElement, true),
                    psiElement.containingFile
                )
            }
            .createPopup()
            .showInBestPositionFor(editor)
    }

    override fun isAvailable(project: Project, editor: Editor, psiElement: PsiElement): Boolean {
        return getMockInstanceFromMethodReferenceScope(psiElement) != null
    }

    private fun getMockInstanceFromMethodReferenceScope(psiElement: PsiElement): String? {
        // $foo = $this->creat<caret>eMock()
        var methodReference = PsiTreeUtil.getTopmostParentOfType(psiElement, MethodReference::class.java)

        if (methodReference == null) {
            // scope outside method reference chaining
            // $f<caret>oo = $this->createMock()
            val variable = psiElement.parent
            if (variable is Variable) {
                val assignmentExpression = variable.parent
                if (assignmentExpression is AssignmentExpression) {
                    methodReference = PsiTreeUtil.getChildOfAnyType(assignmentExpression, MethodReference::class.java)
                }
            }
        }

        if (methodReference == null) {
            return null
        }

        return MethodReferenceNameProcessor.createParameterWithCurrent(methodReference, "createMock", "getMockBuilder")
    }

    @Nls
    override fun getFamilyName(): String {
        return "PHPUnit"
    }

    override fun getText(): String {
        return "PHPUnit: Add mock method"
    }

    override fun startInWriteAction(): Boolean {
        return true
    }

    private data class MyMockWriteCommand(
        private val editor: Editor,
        private val selectedValues: Collection<String>,
        private val psiElement: PsiElement,
        private val jumpToLastElement: Boolean
    ) : Runnable {
        override fun run() {
            val statement = PsiTreeUtil.getParentOfType(psiElement, Statement::class.java)
            if (statement == null) {
                HintManager.getInstance().showErrorHint(editor, "No mock context found")
                return
            }

            val childOfAnyType = PsiTreeUtil.findChildOfAnyType<PhpReference>(
                statement,
                FieldReference::class.java,
                Variable::class.java
            )
            if (childOfAnyType == null) {
                return
            }

            // $this->foobar
            // $foobar
            val prefix = childOfAnyType.text

            var elementJumpTo: PsiElement? = null

            for (selectedValue in selectedValues) {
                val project = psiElement.project

                val methodReference = PhpPsiElementFactory.createStatement(
                    project,
                    String.format("%s->method('%s')->willReturn();", prefix, selectedValue)
                )

                elementJumpTo = statement.add(methodReference)
                statement.add(PhpPsiElementFactory.createNewLine(project))
            }

            if (jumpToLastElement && elementJumpTo != null) {
                for (reference in PsiTreeUtil.getChildrenOfTypeAsList(elementJumpTo, MethodReference::class.java)) {
                    if ("willReturn" != reference.name) {
                        continue
                    }

                    val lastChild = reference.lastChild
                    if (lastChild != null) {
                        editor.caretModel.moveToOffset(lastChild.textRange.startOffset)
                        editor.scrollingModel.scrollToCaret(ScrollType.RELATIVE)
                    }

                    break
                }
            }
        }
    }

    override fun getIcon(flags: Int): Icon {
        return PhpUnitIcons.PHPUNIT
    }
}
