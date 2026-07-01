package de.espend.idea.php.phpunit.utils

import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.ConcatenationExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.PhpPsiElement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
class PatternUtil {
    companion object {
        fun getMethodReferenceWithParameterInsideTokenStringPattern(): PsiElementPattern.Capture<PsiElement> {
            return PlatformPatterns.psiElement().withParent(getMethodReferenceWithParameterPattern())
        }

        fun getMethodReferenceWithParameterPattern(): PsiElementPattern.Capture<StringLiteralExpression> {
            return PlatformPatterns.psiElement(StringLiteralExpression::class.java)
                .withParent(
                    PlatformPatterns.psiElement(ParameterList::class.java)
                        .withParent(MethodReference::class.java)
                )
        }

        fun getMethodReferenceWithArrayHashInsideTokenStringPattern(): PsiElementPattern.Capture<PsiElement> {
            return PlatformPatterns.psiElement().withParent(getMethodReferenceWithArrayHashPattern())
        }

        fun getMethodReferenceWithArrayHashPattern(): PsiElementPattern.Capture<StringLiteralExpression> {
            return createPatternFromClassSequence(
                StringLiteralExpression::class.java,
                PhpPsiElement::class.java,
                ArrayHashElement::class.java,
                ArrayCreationExpression::class.java,
                ParameterList::class.java,
                MethodReference::class.java
            )
        }

        fun getMethodReferenceWithArrayElementInsideTokenStringPattern(): PsiElementPattern.Capture<PsiElement> {
            return PlatformPatterns.psiElement().withParent(getMethodReferenceWithArrayElementPattern())
        }

        fun getMethodReferenceWithArrayElementPattern(): PsiElementPattern.Capture<StringLiteralExpression> {
            return createPatternFromClassSequence(
                StringLiteralExpression::class.java,
                PhpPsiElement::class.java,
                ArrayCreationExpression::class.java,
                ParameterList::class.java,
                MethodReference::class.java
            )
        }

        fun getMethodReferenceWithConcatenationInsideTokenStringPattern(): PsiElementPattern.Capture<PsiElement> {
            return PlatformPatterns.psiElement().withParent(getMethodReferenceWithConcatenationPattern())
        }

        fun getMethodReferenceWithConcatenationPattern(): PsiElementPattern.Capture<StringLiteralExpression> {
            return createPatternFromClassSequence(
                StringLiteralExpression::class.java,
                ConcatenationExpression::class.java,
                ParameterList::class.java
            )
        }

        private fun <T : PsiElement> createPatternFromClassSequence(
            baseClass: Class<T>,
            vararg classes: Class<out PsiElement>
        ): PsiElementPattern.Capture<T> {
            var currentCapture: PsiElementPattern.Capture<out PsiElement>? = null
            for (clazz in classes.reversed()) {
                currentCapture = if (currentCapture == null) {
                    PlatformPatterns.psiElement(clazz)
                } else {
                    PlatformPatterns.psiElement(clazz).withParent(currentCapture)
                }
            }

            if (currentCapture == null) {
                return PlatformPatterns.psiElement(baseClass)
            }

            return PlatformPatterns.psiElement(baseClass).withParent(currentCapture)
        }

        /**
         * "$this->createPartialMock(Foo::class, ['foobar']);"
         */
        fun getArrayParameterPattern(): PsiElementPattern.Capture<StringLiteralExpression> {
            return PlatformPatterns.psiElement(StringLiteralExpression::class.java)
                .withParent(
                    PlatformPatterns.psiElement(PhpPsiElement::class.java)
                        .withParent(
                            PlatformPatterns.psiElement(ArrayCreationExpression::class.java)
                                .withParent(
                                    PlatformPatterns.psiElement(ParameterList::class.java)
                                        .withParent(MethodReference::class.java)
                                )
                        )
                )
        }
    }
}
