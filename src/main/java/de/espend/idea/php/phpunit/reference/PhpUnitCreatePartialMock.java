package de.espend.idea.php.phpunit.reference;

import com.intellij.codeInsight.completion.*;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern.Capture;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.*;
import de.espend.idea.php.phpunit.utils.PhpElementsUtil;
import de.espend.idea.php.phpunit.utils.PhpUnitPluginUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * "$this->createPartialMock(Foo::class, ['foobar']);"
 *
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpUnitCreatePartialMock {
    public static class Completion extends CompletionContributor {
        public Completion() {
            extend(CompletionType.BASIC, PlatformPatterns.psiElement().withParent(getArrayParameterPattern()), new CompletionProvider<>() {
                @Override
                protected void addCompletions(@NotNull CompletionParameters completionParameters, @NotNull ProcessingContext processingContext, @NotNull CompletionResultSet resultSet) {
                    PsiElement psiElement1 = completionParameters.getPosition();

                    PsiElement psiElement = psiElement1.getParent();
                    if (!(psiElement instanceof StringLiteralExpression)) {
                        return;
                    }

                    MethodReference parentOfType = PsiTreeUtil.getParentOfType(psiElement, MethodReference.class);
                    if (parentOfType == null) {
                        return;
                    }

                    if(PhpElementsUtil.isMethodReferenceInstanceOf(parentOfType,  "\\PHPUnit\\Framework\\TestCase", "createPartialMock") ||
                        PhpElementsUtil.isMethodReferenceInstanceOf(parentOfType,  "PHPUnit_Framework_TestCase", "createPartialMock")
                    ) {
                        PsiElement originalClassName = parentOfType.getParameter("originalClassName", 0);

                        if (originalClassName == null) {
                            return;
                        }

                        String stringValue = PhpElementsUtil.getStringValue(originalClassName);
                        if (StringUtils.isBlank(stringValue)) {
                            return;
                        }

                        resultSet.addAllElements(PhpUnitPluginUtil.getMockableMethods(psiElement.getProject(), stringValue));
                    }
                }
            });
        }
    }

    public static class ReferenceContributor extends PsiReferenceContributor {
        @Override
        public void registerReferenceProviders(@NotNull PsiReferenceRegistrar psiReferenceRegistrar) {
            psiReferenceRegistrar.registerReferenceProvider(getArrayParameterPattern(),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
                        if (!(psiElement instanceof StringLiteralExpression)) {
                            return new PsiReference[0];
                        }

                        String contents = ((StringLiteralExpression) psiElement).getContents();
                        if (contents.isBlank()) {
                            return new PsiReference[0];
                        }

                        MethodReference parentOfType = PsiTreeUtil.getParentOfType(psiElement, MethodReference.class);
                        if (parentOfType == null) {
                            return new PsiReference[0];
                        }

                        if(PhpElementsUtil.isMethodReferenceInstanceOf(parentOfType,  "\\PHPUnit\\Framework\\TestCase", "createPartialMock") ||
                            PhpElementsUtil.isMethodReferenceInstanceOf(parentOfType,  "PHPUnit_Framework_TestCase", "createPartialMock")
                        ) {
                            PsiElement originalClassName = parentOfType.getParameter("originalClassName", 0);

                            if (originalClassName == null) {
                                return new PsiReference[0];
                            }

                            String stringValue = PhpElementsUtil.getStringValue(originalClassName);
                            if (StringUtils.isBlank(stringValue)) {
                                return new PsiReference[0];
                            }

                            return new PsiReference[] {
                                new PhpClassMethodReference((StringLiteralExpression) psiElement, contents, stringValue)
                            };
                        }

                        return new PsiReference[0];
                    }
                }
            );
        }
    }

    private static @NotNull Capture<StringLiteralExpression> getArrayParameterPattern() {
        return PlatformPatterns.psiElement(StringLiteralExpression.class)
            .withParent(PlatformPatterns.psiElement(PhpPsiElement.class)
                .withParent(PlatformPatterns.psiElement(ArrayCreationExpression.class)
                    .withParent(PlatformPatterns.psiElement(ParameterList.class)
                        .withParent(MethodReference.class))));

    }
}
