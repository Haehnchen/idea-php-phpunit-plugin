package de.espend.idea.php.phpunit.intention;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInsight.intention.preview.IntentionPreviewUtils;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.php.codeInsight.PhpScopeHolder;
import com.jetbrains.php.lang.inspections.PhpThrownExceptionsAnalyzer;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.phpunit.PhpUnitUtil;
import de.espend.idea.php.phpunit.PhpUnitIcons;
import de.espend.idea.php.phpunit.utils.PhpUnitPluginUtil;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class MethodExceptionIntentionAction extends PsiElementBaseIntentionAction implements Iconable {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) throws IncorrectOperationException {
        Method method = getMethodScope(psiElement);
        if(method == null) {
            return;
        }

        Collection<String> exceptions = getException(method);
        if(exceptions.size() == 0) {
            if (!IntentionPreviewUtils.isPreviewElement(psiElement)) {
                HintManager.getInstance().showErrorHint(editor, "No exception in method scope found");
            }

            return;
        }

        if (IntentionPreviewUtils.isPreviewElement(psiElement) || exceptions.size() == 1) {
            PhpUnitPluginUtil.insertExpectedException(editor.getDocument(), method, psiElement, exceptions.iterator().next());

            return;
        }

        final List<String> list = new ArrayList<>(exceptions);

        JBPopupFactory.getInstance().createPopupChooserBuilder(list)
            .setTitle("PHPUnit: Select Exception")
            .setItemChosenCallback(s -> WriteCommandAction.runWriteCommandAction(
                psiElement.getProject(),
                getText(),
                "",
                () -> PhpUnitPluginUtil.insertExpectedException(editor.getDocument(), method, psiElement, s),
                psiElement.getContainingFile()
            ))
            .createPopup()
            .showInBestPositionFor(editor);
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) {
        return getMethodScope(psiElement) != null;
    }

    @Nullable
    private Method getMethodScope(@NotNull PsiElement psiElement) {
        Method method = PsiTreeUtil.getParentOfType(psiElement, Method.class);
        if(method != null && PhpUnitUtil.isTestMethod(method)) {
            return method;
        }

        return null;
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
        return "PHPUnit: Expected exception";
    }

    @NotNull
    private Set<String> getException(@NotNull PsiElement psiElement) {
        Collection<MethodReference> methodReferences = new HashSet<>();

        psiElement.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if(element instanceof MethodReference) {
                    methodReferences.add((MethodReference) element);
                }

                super.visitElement(element);
            }
        });

        return StreamEx.of(methodReferences)
                .map(PsiReference::resolve)
                .select(PhpScopeHolder.class)
                .flatCollection(PhpThrownExceptionsAnalyzer::getExceptionClasses)
                .map(phpType -> StringUtils.stripStart(phpType.toString(), "\\"))
                .filter(s -> !s.toLowerCase().contains("phpunit"))
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Icon getIcon(int flags) {
        return PhpUnitIcons.PHPUNIT;
    }
}
