package de.espend.idea.php.phpunit.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import de.espend.idea.php.phpunit.utils.MockeryReferencingUtil;
import de.espend.idea.php.phpunit.utils.PatternUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MockeryAnnotator implements Annotator {

    private enum Scope {
        PARAMETER(PatternUtil.getMethodReferenceWithParameterPattern(),
                MockeryReferencingUtil::findMockeryMockParametersOnParameterScope),
        ARRAY_HASH(PatternUtil.getMethodReferenceWithArrayHashPattern(),
                MockeryReferencingUtil::findMockeryMockParametersOnArrayHashScope),
        ARRAY_ELEMENT(PatternUtil.getMethodReferenceWithArrayElementPattern(),
                MockeryReferencingUtil::findMockeryMockParametersOnArrayElementScope);

        private final PsiElementPattern.Capture<StringLiteralExpression> psiElementPattern;
        private final Function<StringLiteralExpression, String[]> getMockCreationParametersMethod;

        Scope(PsiElementPattern.Capture<StringLiteralExpression> psiElementPattern, Function<StringLiteralExpression, String[]> getMockCreationParametersMethod) {
            this.psiElementPattern = psiElementPattern;
            this.getMockCreationParametersMethod = getMockCreationParametersMethod;
        }

        public PsiElementPattern.Capture<StringLiteralExpression> getPsiElementPattern() {
            return psiElementPattern;
        }

        public String[] getMockCreationParameters(StringLiteralExpression exp) {
            return getMockCreationParametersMethod.apply(exp);
        }
    }

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        annotateByScope(Scope.PARAMETER, psiElement, annotationHolder);
        annotateByScope(Scope.ARRAY_HASH, psiElement, annotationHolder);
        annotateByScope(Scope.ARRAY_ELEMENT, psiElement, annotationHolder);
    }

    private void annotateByScope(Scope scope, @NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        PsiElementPattern.Capture<StringLiteralExpression> pattern = scope.getPsiElementPattern();

        if (pattern.accepts(psiElement)) {
            if (psiElement instanceof StringLiteralExpression) {
                // the method name put as input string in expects/allows etc
                String contents = ((StringLiteralExpression) psiElement).getContents();

                if (StringUtils.isNotBlank(contents)) {
                    String[] mockCreationParameters = scope.getMockCreationParameters((StringLiteralExpression) psiElement);

                    if (mockCreationParameters != null && mockCreationParameters.length > 0) {

                        Set<Method> allMethods = new HashSet<>();
                        Set<String> classNames = new HashSet<>();
                        for (String mockCreationParameter : mockCreationParameters) {
                            for (PhpClass phpClass : PhpIndex.getInstance(psiElement.getProject()).getAnyByFQN(mockCreationParameter)) {
                                classNames.add(phpClass.getName());

                                allMethods.addAll(phpClass.getMethods().stream()
                                        .filter(method -> !method.getAccess().isPublic() || !method.getName().startsWith("__"))
                                        .collect(Collectors.toSet())
                                );
                            }
                        }

                        Set<String> allMethodNames = allMethods.stream()
                                .map(PhpNamedElement::getName)
                                .collect(Collectors.toSet());

                        Set<String> privateMethodNames = allMethods.stream()
                                .filter(method -> method.getAccess().isPrivate())
                                .map(PhpNamedElement::getName)
                                .collect(Collectors.toSet());

                        Set<String> protectedMethodNames = allMethods.stream()
                                .filter(method -> method.getAccess().isProtected())
                                .map(PhpNamedElement::getName)
                                .collect(Collectors.toSet());


                        TextRange textRange = psiElement.getTextRange();
                        TextRange annotationTextRange = new TextRange(textRange.getStartOffset() + 1, textRange.getEndOffset() - 1);

                        if (!allMethodNames.contains(contents)) {
                            if (classNames.size() == 1) {
                                String className = classNames.toArray()[0].toString();
                                annotationHolder.newAnnotation(HighlightSeverity.WARNING, "Method '" + contents + "' not found in class " + className)
                                        .range(annotationTextRange).create();
                            } else {
                                annotationHolder.newAnnotation(HighlightSeverity.WARNING, "Method '" + contents + "' not found in any of classes " + classNames)
                                        .range(annotationTextRange).create();
                            }
                        } else if (privateMethodNames.contains(contents)) {
                            annotationHolder.newAnnotation(HighlightSeverity.WARNING, "Method '" + contents + "' is private, Mockery does not support private methods")
                                    .range(annotationTextRange).create();
                        } else if (protectedMethodNames.contains(contents)) {
                            annotationHolder.newAnnotation(HighlightSeverity.WARNING, "Method '" + contents + "' is protected. Mocking protected methods is not suggested.  Further guidance can be found here http://docs.mockery.io/en/latest/reference/protected_methods.html")
                                    .range(annotationTextRange).create();
                        }
                    }
                }
            }
        }
    }
}
