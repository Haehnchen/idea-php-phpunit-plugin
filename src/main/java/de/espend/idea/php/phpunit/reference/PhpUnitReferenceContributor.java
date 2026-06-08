package de.espend.idea.php.phpunit.reference;

import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import de.espend.idea.php.phpunit.utils.PatternUtil;
import de.espend.idea.php.phpunit.utils.PhpUnitPluginUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpUnitReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar psiReferenceRegistrar) {
        psiReferenceRegistrar.registerReferenceProvider(
            PatternUtil.getMethodReferenceWithParameterPattern(),
            new PsiReferenceProvider() {
                @NotNull
                @Override
                public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
                    if(psiElement instanceof StringLiteralExpression) {
                        String contents = ((StringLiteralExpression) psiElement).getContents();
                        if(StringUtils.isNotBlank(contents)) {
                            String parameter = PhpUnitPluginUtil.findCreateMockParameterOnParameterScope((StringLiteralExpression) psiElement);
                            if(parameter != null) {
                                return new PsiReference[] {
                                    new PhpClassMethodReference((StringLiteralExpression) psiElement, contents, parameter)
                                };
                            }
                        }
                    }
                    return new PsiReference[0];
                }
            }
        );
    }
}
