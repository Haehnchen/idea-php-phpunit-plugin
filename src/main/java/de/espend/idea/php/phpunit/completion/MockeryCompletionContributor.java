package de.espend.idea.php.phpunit.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import de.espend.idea.php.phpunit.utils.MockeryReferencingUtil;
import de.espend.idea.php.phpunit.utils.PatternUtil;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.stream.Collectors;

public class MockeryCompletionContributor extends CompletionContributor {

    private enum Scope {
        PARAMETER(PatternUtil.getMethodReferenceWithParameterInsideTokenStringPattern(),
                MockeryReferencingUtil::findMockeryMockParametersOnParameterScope,
                MockeryCompletionContributor::processParametersAsClasses),
        ARRAY_HASH(PatternUtil.getMethodReferenceWithArrayHashInsideTokenStringPattern(),
                MockeryReferencingUtil::findMockeryMockParametersOnArrayHashScope,
                MockeryCompletionContributor::processParametersAsClasses),
        ARRAY_ELEMENT(PatternUtil.getMethodReferenceWithArrayElementInsideTokenStringPattern(),
                MockeryReferencingUtil::findMockeryMockParametersOnArrayElementScope,
                MockeryCompletionContributor::processParametersAsClasses),
        PARTIAL_STRING(PatternUtil.getMethodReferenceWithParameterInsideTokenStringPattern(),
                MockeryReferencingUtil::findMockeryMockParametersOnPartialMockStringDeclarationScope,
                MockeryCompletionContributor::processParametersAsClassAndMethodNames),
        PARTIAL_CONCATENATION(PatternUtil.getMethodReferenceWithConcatenationInsideTokenStringPattern(),
                MockeryReferencingUtil::findMockeryMockParametersOnPartialMockConcatenationDeclarationScope,
                MockeryCompletionContributor::processParametersAsClassAndMethodNames);

        private final PsiElementPattern.Capture<PsiElement> pattern;
        private final Function<StringLiteralExpression, String[]> getMockCreationParametersMethod;
        private final Consumer3<CompletionResultSet, PsiElement, String[]> processMockCreationParametersMethod;

        Scope(PsiElementPattern.Capture<PsiElement> pattern,
              Function<StringLiteralExpression, String[]> getMockCreationParametersMethod,
              Consumer3<CompletionResultSet, PsiElement, String[]> processMockCreationParametersMethod) {
            this.pattern = pattern;
            this.getMockCreationParametersMethod = getMockCreationParametersMethod;
            this.processMockCreationParametersMethod = processMockCreationParametersMethod;
        }

        public PsiElementPattern.Capture<PsiElement> getPattern() {
            return pattern;
        }

        public String[] getMockCreationParameters(StringLiteralExpression exp) {
            return getMockCreationParametersMethod.apply(exp);
        }

        public void processMockCreationParameters(CompletionResultSet resultSet, PsiElement psiElement, String[] mockCreationParameters) {
            processMockCreationParametersMethod.accept(resultSet, psiElement, mockCreationParameters);
        }
    }

    public MockeryCompletionContributor() {
        extendByScope(Scope.PARAMETER);
        extendByScope(Scope.ARRAY_HASH);
        extendByScope(Scope.ARRAY_ELEMENT);
        extendByScope(Scope.PARTIAL_STRING);
        extendByScope(Scope.PARTIAL_CONCATENATION);
    }

    private void extendByScope(@NotNull Scope scope) {
        extend(CompletionType.BASIC, scope.getPattern(), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters completionParameters, @NotNull ProcessingContext processingContext, @NotNull CompletionResultSet resultSet) {
                PsiElement psiElement = completionParameters.getPosition();
                PsiElement parent = psiElement.getParent();
                if (parent instanceof StringLiteralExpression) {
                    String[] mockCreationParameters = scope.getMockCreationParameters((StringLiteralExpression) parent);
                    if (mockCreationParameters != null) {
                        scope.processMockCreationParameters(resultSet, psiElement, mockCreationParameters);
                    }
                }
            }
        });
    }

    private static void processParametersAsClasses(CompletionResultSet resultSet, PsiElement psiElement, String[] mockCreationParameters) {
        for (String parameter : mockCreationParameters) {
            for (PhpClass phpClass : PhpIndex.getInstance(psiElement.getProject()).getAnyByFQN(parameter)) {
                resultSet.addAllElements(phpClass.getMethods().stream()
                        .filter(method -> !method.getAccess().isPublic() || !method.getName().startsWith("__"))
                        .map((Function<Method, LookupElement>) PhpLookupElement::new)
                        .collect(Collectors.toSet())
                );
            }
        }
    }

    private static void processParametersAsClassAndMethodNames(CompletionResultSet resultSet, PsiElement psiElement, String[] mockCreationParameters) {
        if (mockCreationParameters.length > 0) {
            String className = mockCreationParameters[0];

            processParametersAsClasses(resultSet, psiElement, new String[]{className});
        }
    }

    @FunctionalInterface
    private interface Consumer3<T, U, V> {
        void accept(T t, U u, V v);
    }
}
