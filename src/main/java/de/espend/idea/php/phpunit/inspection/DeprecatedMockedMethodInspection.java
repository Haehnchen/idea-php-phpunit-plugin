package de.espend.idea.php.phpunit.inspection;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import de.espend.idea.php.phpunit.utils.PatternUtil;
import de.espend.idea.php.phpunit.utils.PhpElementsUtil;
import de.espend.idea.php.phpunit.utils.PhpUnitPluginUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class DeprecatedMockedMethodInspection extends LocalInspectionTool {
    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement psiElement) {
                if (!(psiElement instanceof StringLiteralExpression)) {
                    super.visitElement(psiElement);
                    return;
                }

                if (PatternUtil.getMethodReferenceWithParameterPattern().accepts(psiElement)) {
                    visitCreateMock((StringLiteralExpression) psiElement, holder);
                }

                if (PatternUtil.getArrayParameterPattern().accepts(psiElement)) {
                    visitCreatePartialMock((StringLiteralExpression) psiElement, holder);
                }

                super.visitElement(psiElement);
            }
        };
    }
    private static void visitCreateMock(@NotNull StringLiteralExpression psiElement, @NotNull ProblemsHolder holder) {
        String clazz = PhpUnitPluginUtil.findCreateMockParameterOnParameterScope(psiElement);
        if (clazz == null) {
            return;
        }

        String method = psiElement.getContents();
        if (!method.isBlank()) {
            registerProblemIfDeprecated(psiElement, holder, clazz, method);
        }
    }

    private static void registerProblemIfDeprecated(@NotNull StringLiteralExpression psiElement, @NotNull ProblemsHolder holder, @NotNull String clazz, @NotNull String method) {
        boolean isDeprecated = PhpIndex.getInstance(psiElement.getProject())
            .getAnyByFQN(clazz).stream()
            .map(phpClass -> phpClass.findMethodByName(method))
            .filter(Objects::nonNull)
            .anyMatch(PhpNamedElement::isDeprecated);

        if (isDeprecated) {
            holder.registerProblem(psiElement, "Method '" + method + "' is deprecated ", ProblemHighlightType.LIKE_DEPRECATED);
        }
    }

    private static void visitCreatePartialMock(@NotNull StringLiteralExpression psiElement, @NotNull ProblemsHolder holder) {
        MethodReference parentOfType = PsiTreeUtil.getParentOfType(psiElement, MethodReference.class);
        if (parentOfType == null) {
            return;
        }

        if (PhpUnitPluginUtil.isCreatePartialMockMethod(parentOfType)) {
            PsiElement originalClassName = parentOfType.getParameter("originalClassName", 0);

            if (originalClassName == null) {
                return;
            }

            String method = psiElement.getContents();
            if (method.isBlank()) {
                return;
            }

            String clazz = PhpElementsUtil.getStringValue(originalClassName);
            if (StringUtils.isBlank(clazz)) {
                return;
            }

            registerProblemIfDeprecated(psiElement, holder, clazz, method);
        }
    }
}
