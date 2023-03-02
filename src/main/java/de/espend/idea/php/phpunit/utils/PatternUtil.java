package de.espend.idea.php.phpunit.utils;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.*;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PatternUtil {
    @NotNull
    public static PsiElementPattern.Capture<PsiElement> getMethodReferenceWithParameterInsideTokenStringPattern() {
        return PlatformPatterns.psiElement().withParent(getMethodReferenceWithParameterPattern());
    }

    @NotNull
    public static PsiElementPattern.Capture<StringLiteralExpression> getMethodReferenceWithParameterPattern() {
        return PlatformPatterns.psiElement(StringLiteralExpression.class)
            .withParent(PlatformPatterns.psiElement(ParameterList.class)
                .withParent(MethodReference.class)
            );
    }

    @NotNull
    public static PsiElementPattern.Capture<PsiElement> getMethodReferenceWithArrayHashInsideTokenStringPattern() {
        return PlatformPatterns.psiElement().withParent(getMethodReferenceWithArrayHashPattern());
    }

    @NotNull
    public static PsiElementPattern.Capture<StringLiteralExpression> getMethodReferenceWithArrayHashPattern() {
        return createPatternFromClassSequence(StringLiteralExpression.class,
                PhpPsiElement.class, ArrayHashElement.class, ArrayCreationExpression.class, ParameterList.class, MethodReference.class);
    }

    @NotNull
    public static PsiElementPattern.Capture<PsiElement> getMethodReferenceWithArrayElementInsideTokenStringPattern() {
        return PlatformPatterns.psiElement().withParent(getMethodReferenceWithArrayElementPattern());
    }

    @NotNull
    public static PsiElementPattern.Capture<StringLiteralExpression> getMethodReferenceWithArrayElementPattern() {
        return createPatternFromClassSequence(StringLiteralExpression.class,
                PhpPsiElement.class, ArrayCreationExpression.class, ParameterList.class, MethodReference.class);
    }

    @NotNull
    public static PsiElementPattern.Capture<PsiElement> getMethodReferenceWithConcatenationInsideTokenStringPattern() {
        return PlatformPatterns.psiElement().withParent(getMethodReferenceWithConcatenationPattern());
    }

    @NotNull
    public static PsiElementPattern.Capture<StringLiteralExpression> getMethodReferenceWithConcatenationPattern() {
        return createPatternFromClassSequence(StringLiteralExpression.class, ConcatenationExpression.class, ParameterList.class);
    }

    @SafeVarargs
    @NotNull
    private static <T extends PsiElement> PsiElementPattern.Capture<T> createPatternFromClassSequence(@NotNull Class<T> baseClass, Class<? extends PsiElement>... classes) {
        // Need to reverse classes as we build from highest parent down
        ArrayUtils.reverse(classes);

        PsiElementPattern.Capture<? extends PsiElement> currentCapture = null;
        for (Class<? extends PsiElement> clazz : classes) {
            if (currentCapture == null) {
                currentCapture = PlatformPatterns.psiElement(clazz);
            } else {
                currentCapture = PlatformPatterns.psiElement(clazz).withParent(currentCapture);
            }
        }

        if (currentCapture == null) {
            return PlatformPatterns.psiElement(baseClass);
        }
        return PlatformPatterns.psiElement(baseClass).withParent(currentCapture);
    }


    /**
     * "$this->createPartialMock(Foo::class, ['foobar']);"
     */
    public static @NotNull PsiElementPattern.Capture<StringLiteralExpression> getArrayParameterPattern() {
        return PlatformPatterns.psiElement(StringLiteralExpression.class)
            .withParent(PlatformPatterns.psiElement(PhpPsiElement.class)
                .withParent(PlatformPatterns.psiElement(ArrayCreationExpression.class)
                    .withParent(PlatformPatterns.psiElement(ParameterList.class)
                        .withParent(MethodReference.class))));

    }
}