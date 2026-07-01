package de.espend.idea.php.phpunit.reference;

import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import de.espend.idea.php.phpunit.utils.MockeryReferencingUtil;
import de.espend.idea.php.phpunit.utils.PatternUtil;
import kotlin.jvm.functions.Function3;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class MockeryReferenceContributor extends PsiReferenceContributor {

    private enum Scope {
        PARAMETER(PatternUtil.getMethodReferenceWithParameterPattern(),
                MockeryReferencingUtil::findMockeryMockParametersOnParameterScope,
                MockeryReferenceContributor::processParametersAsClasses),
        ARRAY_HASH(PatternUtil.getMethodReferenceWithArrayHashPattern(),
                MockeryReferencingUtil::findMockeryMockParametersOnArrayHashScope,
                MockeryReferenceContributor::processParametersAsClasses),
        ARRAY_ELEMENT(PatternUtil.getMethodReferenceWithArrayElementPattern(),
                MockeryReferencingUtil::findMockeryMockParametersOnArrayElementScope,
                MockeryReferenceContributor::processParametersAsClasses),
        PARTIAL_STRING(PatternUtil.getMethodReferenceWithParameterPattern(),
                MockeryReferencingUtil::findMockeryMockParametersOnPartialMockStringDeclarationScope,
                (psiElement, contents, mockCreationParameters) -> processParametersAsClassAndMethodNames(psiElement, mockCreationParameters)),
        PARTIAL_CONCATENATION(PatternUtil.getMethodReferenceWithConcatenationPattern(),
                MockeryReferencingUtil::findMockeryMockParametersOnPartialMockConcatenationDeclarationScope,
                (psiElement, contents, mockCreationParameters) -> processParametersAsClassAndMethodNames(psiElement, mockCreationParameters));
        ;

        private final PsiElementPattern.Capture<StringLiteralExpression> psiElementPattern;
        private final Function<StringLiteralExpression, String[]> getMockCreationParametersMethod;
        private final Function3<PsiElement, String, String[], PsiReference[]> processMockCreationParametersMethod;

        Scope(PsiElementPattern.Capture<StringLiteralExpression> psiElementPattern,
              Function<StringLiteralExpression, String[]> getMockCreationParametersMethod,
              Function3<PsiElement, String, String[], PsiReference[]> processMockCreationParametersMethod) {
            this.psiElementPattern = psiElementPattern;
            this.getMockCreationParametersMethod = getMockCreationParametersMethod;
            this.processMockCreationParametersMethod = processMockCreationParametersMethod;
        }

        public PsiElementPattern.Capture<StringLiteralExpression> getPsiElementPattern() {
            return psiElementPattern;
        }

        public String[] getMockCreationParameters(StringLiteralExpression exp) {
            return getMockCreationParametersMethod.apply(exp);
        }

        public PsiReference[] processMockCreationParameters(PsiElement psiElement, String contents, String[] mockCreationParameters) {
            return processMockCreationParametersMethod.invoke(psiElement, contents, mockCreationParameters);
        }
    }

    /**
     * Provides a reference provider. The provider will take a psi element and, if able, return a Reference
     * ({@link PhpClassMethodReference}) to it's declaration.
     * Provider wants to check that the psi element is a mocked method.
     * <p>
     * The reference has three parts: The psi element we want a reference for; the method (<code>content</code>)
     * which the psi element is associated with; and the clazz (<code>parameter</code>) where the method is found.
     * <p>
     * For example the element may be <code>'calledMethod'</code>, the method <code>calledMethod</code>
     * and the clazz would be the <code>Dependency</code> class.
     *
     * @param psiReferenceRegistrar we register a reference provider with this registrar.
     */
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar psiReferenceRegistrar) {
        registerReferenceByScope(psiReferenceRegistrar, Scope.PARAMETER);
        registerReferenceByScope(psiReferenceRegistrar, Scope.ARRAY_HASH);
        registerReferenceByScope(psiReferenceRegistrar, Scope.ARRAY_ELEMENT);
        registerReferenceByScope(psiReferenceRegistrar, Scope.PARTIAL_STRING);
        registerReferenceByScope(psiReferenceRegistrar, Scope.PARTIAL_CONCATENATION);
    }

    private void registerReferenceByScope(@NotNull PsiReferenceRegistrar psiReferenceRegistrar, Scope scope) {
        psiReferenceRegistrar.registerReferenceProvider(
                scope.getPsiElementPattern(),
                getProvider(scope)
        );
    }

    @NotNull
    private PsiReferenceProvider getProvider(Scope scope) {
        return new PsiReferenceProvider() {
            @Override
            public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
                if (psiElement instanceof StringLiteralExpression) {
                    String contents = ((StringLiteralExpression) psiElement).getContents();
                    if (StringUtils.isNotBlank(contents)) {
                        String[] mockCreationParameters = scope.getMockCreationParameters((StringLiteralExpression) psiElement);

                        if (mockCreationParameters != null) {
                            return scope.processMockCreationParameters(psiElement, contents, mockCreationParameters);
                        }
                    }
                }
                return new PsiReference[0];
            }
        };
    }

    private static PsiReference[] processParametersAsClasses(PsiElement psiElement, String contents, String[] mockCreationParameters) {
        List<PsiReference> references = new ArrayList<>();

        for (String mockCreationParameter : mockCreationParameters) {
            references.add(new PhpClassMethodReference((StringLiteralExpression) psiElement, contents, mockCreationParameter));
        }

        return references.toArray(new PsiReference[0]);
    }

    private static PsiReference[] processParametersAsClassAndMethodNames(PsiElement psiElement, String[] mockCreationParameters) {
        if (mockCreationParameters == null || mockCreationParameters.length <= 1) {
            return new PsiReference[0];
        }

        String className = mockCreationParameters[0];
        String[] methodNames = Arrays.copyOfRange(mockCreationParameters, 1, mockCreationParameters.length);

        List<PsiReference> references = new ArrayList<>();

        for (String method : methodNames) {
            references.add(new PhpClassMethodReferenceForPartialMock((StringLiteralExpression) psiElement, method, className));
        }
        return references.toArray(new PsiReference[0]);
    }
}
