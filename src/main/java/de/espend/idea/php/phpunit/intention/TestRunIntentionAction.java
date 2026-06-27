package de.espend.idea.php.phpunit.intention;

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.phpunit.PhpUnitUtil;
import de.espend.idea.php.phpunit.PhpUnitIcons;
import de.espend.idea.php.phpunit.utils.PhpUnitPluginUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class TestRunIntentionAction extends PsiElementBaseIntentionAction implements Iconable {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) throws IncorrectOperationException {
        PsiElement context = getTestContextElement(psiElement);
        if(context != null) {
            PhpUnitPluginUtil.executeDebugRunner(psiElement);
        }
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) {
        return getTestContextElement(psiElement) != null;
    }

    @Nullable
    private PsiElement getTestContextElement(@NotNull PsiElement psiElement) {
        Method method = PsiTreeUtil.getStubOrPsiParentOfType(psiElement, Method.class);
        if(method != null && PhpUnitUtil.isTestMethod(method)) {
            return method;
        }

        PhpClass phpClass = PsiTreeUtil.getStubOrPsiParentOfType(psiElement, PhpClass.class);
        if(phpClass != null && PhpUnitUtil.isTestClass(phpClass)) {
            return phpClass;
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
        return "PHPUnit: Run Test";
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    @Override
    public Icon getIcon(int flags) {
        return PhpUnitIcons.PHPUNIT;
    }
}
