package de.espend.idea.php.phpunit.intention;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.intention.HighPriorityAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInsight.intention.preview.IntentionPreviewUtils;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.*;
import de.espend.idea.php.phpunit.PhpUnitIcons;
import de.espend.idea.php.phpunit.utils.processor.MethodReferenceNameProcessor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Add mock based on given context:
 * $foobar->method('foobar')->willReturn();
 *
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class AddMockMethodIntention extends PsiElementBaseIntentionAction implements Iconable, HighPriorityAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) throws IncorrectOperationException {
        String parameter = getMockInstanceFromMethodReferenceScope(psiElement);

        if(parameter == null) {
            if (!IntentionPreviewUtils.isPreviewElement(psiElement)) {
                HintManager.getInstance().showErrorHint(editor, "No mock context found");
            }

            return;
        }

        Set<String> methods = new TreeSet<>();
        for (PhpClass phpClass : PhpIndex.getInstance(psiElement.getProject()).getAnyByFQN(parameter)) {
            methods.addAll(phpClass.getMethods().stream()
                .filter(method -> method.getAccess().isPublic() && !method.getName().startsWith("__") && !method.isStatic() && !method.isFinal())
                .map(PhpNamedElement::getName).collect(Collectors.toSet())
            );
        }

        if(methods.size() == 0) {
            if (!IntentionPreviewUtils.isPreviewElement(psiElement)) {
                HintManager.getInstance().showErrorHint(editor, "No public method found");
            }

            return;
        }

        if (IntentionPreviewUtils.isPreviewElement(psiElement)) {
            new MyMockWriteCommand(editor, methods, psiElement, false).run();

            return;
        }

        // Single item direct execution without selection
        if(methods.size() == 1) {
            WriteCommandAction.runWriteCommandAction(
                psiElement.getProject(),
                getText(),
                "",
                new MyMockWriteCommand(editor, new ArrayList<>(methods), psiElement, true),
                psiElement.getContainingFile()
            );

            return;
        }

        final List<String> list = new ArrayList<>(methods);

        JBPopupFactory.getInstance().createPopupChooserBuilder(list)
            .setTitle("PHPUnit: Mock Method")
            .setItemsChosenCallback(strings -> WriteCommandAction.runWriteCommandAction(
                psiElement.getProject(),
                getText(),
                "",
                new MyMockWriteCommand(editor, new ArrayList<>(strings), psiElement, true),
                psiElement.getContainingFile()
            ))
            .createPopup()
            .showInBestPositionFor(editor);

    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) {
        return getMockInstanceFromMethodReferenceScope(psiElement) != null;
    }

    @Nullable
    private String getMockInstanceFromMethodReferenceScope(@NotNull PsiElement psiElement) {
        // $foo = $this->creat<caret>eMock()
        MethodReference methodReference = PsiTreeUtil.getTopmostParentOfType(psiElement, MethodReference.class);

        if(methodReference == null) {
            // scope outside method reference chaining
            // $f<caret>oo = $this->createMock()
            PsiElement variable = psiElement.getParent();
            if(variable instanceof Variable) {
                PsiElement assignmentExpression = variable.getParent();
                if(assignmentExpression instanceof AssignmentExpression) {
                    methodReference = PsiTreeUtil.getChildOfAnyType(assignmentExpression, MethodReference.class);
                }
            }
        }

        if(methodReference == null) {
            return null;
        }

        return MethodReferenceNameProcessor.createParameterWithCurrent(methodReference, "createMock", "getMockBuilder");
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "PHPUnit";
    }

    @NotNull
    @Override
    public String getText() {
        return "PHPUnit: Add mock method";
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }

    private static class MyMockWriteCommand implements Runnable {
        @NotNull
        private final Editor editor;

        @NotNull
        private final Collection<String> selectedValues;

        @NotNull
        private final PsiElement psiElement;
        private final boolean jumpToLastElement;

        private MyMockWriteCommand(@NotNull Editor editor, @NotNull Collection<String> selectedValues, @NotNull PsiElement psiElement, boolean jumpToLastElement) {
            this.editor = editor;
            this.selectedValues = selectedValues;
            this.psiElement = psiElement;
            this.jumpToLastElement = jumpToLastElement;
        }

        public void run() {
            Statement statement = PsiTreeUtil.getParentOfType(psiElement, Statement.class);
            if(statement == null) {
                HintManager.getInstance().showErrorHint(editor, "No mock context found");
                return;
            }

            PhpReference childOfAnyType = PsiTreeUtil.findChildOfAnyType(statement, FieldReference.class, Variable.class);
            if(childOfAnyType == null) {
                return;
            }

            // $this->foobar
            // $foobar
            String prefix = childOfAnyType.getText();

            PsiElement elementJumpTo = null;

            for (String selectedValue : selectedValues) {
                Project project = psiElement.getProject();

                Statement methodReference = PhpPsiElementFactory.createStatement(
                    project,
                    String.format("%s->method('%s')->willReturn();", prefix, selectedValue)
                );

                elementJumpTo = statement.add(methodReference);
                statement.add(PhpPsiElementFactory.createNewLine(project));
            }

            if (this.jumpToLastElement && elementJumpTo != null) {
                for (MethodReference reference : PsiTreeUtil.getChildrenOfTypeAsList(elementJumpTo, MethodReference.class)) {
                    if(!"willReturn".equals(reference.getName())) {
                        continue;
                    }

                    PsiElement lastChild = reference.getLastChild();
                    if(lastChild != null) {
                        editor.getCaretModel().moveToOffset(lastChild.getTextRange().getStartOffset());
                        editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
                    }

                    break;
                }
            }

        }
    }

    @Override
    public Icon getIcon(int flags) {
        return PhpUnitIcons.PHPUNIT;
    }
}
