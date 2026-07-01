package de.espend.idea.php.phpunit.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import de.espend.idea.php.phpunit.utils.MockeryReferencingUtil
import de.espend.idea.php.phpunit.utils.PatternUtil
import org.apache.commons.lang3.StringUtils
import java.util.HashSet

class MockeryAnnotator : Annotator {
    private enum class Scope(
        val psiElementPattern: PsiElementPattern.Capture<StringLiteralExpression>,
        val getMockCreationParametersMethod: (StringLiteralExpression) -> Array<String>?
    ) {
        PARAMETER(
            PatternUtil.getMethodReferenceWithParameterPattern(),
            MockeryReferencingUtil::findMockeryMockParametersOnParameterScope
        ),
        ARRAY_HASH(
            PatternUtil.getMethodReferenceWithArrayHashPattern(),
            MockeryReferencingUtil::findMockeryMockParametersOnArrayHashScope
        ),
        ARRAY_ELEMENT(
            PatternUtil.getMethodReferenceWithArrayElementPattern(),
            MockeryReferencingUtil::findMockeryMockParametersOnArrayElementScope
        );

        fun getMockCreationParameters(exp: StringLiteralExpression): Array<String>? {
            return getMockCreationParametersMethod(exp)
        }
    }

    override fun annotate(psiElement: PsiElement, annotationHolder: AnnotationHolder) {
        annotateByScope(Scope.PARAMETER, psiElement, annotationHolder)
        annotateByScope(Scope.ARRAY_HASH, psiElement, annotationHolder)
        annotateByScope(Scope.ARRAY_ELEMENT, psiElement, annotationHolder)
    }

    private fun annotateByScope(scope: Scope, psiElement: PsiElement, annotationHolder: AnnotationHolder) {
        val pattern = scope.psiElementPattern

        if (pattern.accepts(psiElement)) {
            if (psiElement is StringLiteralExpression) {
                // the method name put as input string in expects/allows etc
                val contents = psiElement.contents

                if (StringUtils.isNotBlank(contents)) {
                    val mockCreationParameters = scope.getMockCreationParameters(psiElement)

                    if (mockCreationParameters != null && mockCreationParameters.isNotEmpty()) {
                        val allMethods = HashSet<Method>()
                        val classNames = HashSet<String>()
                        for (mockCreationParameter in mockCreationParameters) {
                            for (phpClass in PhpIndex.getInstance(psiElement.project).getAnyByFQN(mockCreationParameter)) {
                                classNames.add(phpClass.name)

                                allMethods.addAll(
                                    phpClass.methods
                                        .filter { method -> !method.access.isPublic || !method.name.startsWith("__") }
                                        .toSet()
                                )
                            }
                        }

                        val allMethodNames = allMethods
                            .map(PhpNamedElement::getName)
                            .toSet()

                        val privateMethodNames = allMethods
                            .filter { method -> method.access.isPrivate }
                            .map(PhpNamedElement::getName)
                            .toSet()

                        val protectedMethodNames = allMethods
                            .filter { method -> method.access.isProtected }
                            .map(PhpNamedElement::getName)
                            .toSet()

                        val textRange = psiElement.textRange
                        val annotationTextRange = TextRange(textRange.startOffset + 1, textRange.endOffset - 1)

                        if (!allMethodNames.contains(contents)) {
                            if (classNames.size == 1) {
                                val className = classNames.first()
                                annotationHolder.newAnnotation(
                                    HighlightSeverity.WARNING,
                                    "Method '$contents' not found in class $className"
                                ).range(annotationTextRange).create()
                            } else {
                                annotationHolder.newAnnotation(
                                    HighlightSeverity.WARNING,
                                    "Method '$contents' not found in any of classes $classNames"
                                ).range(annotationTextRange).create()
                            }
                        } else if (privateMethodNames.contains(contents)) {
                            annotationHolder.newAnnotation(
                                HighlightSeverity.WARNING,
                                "Method '$contents' is private, Mockery does not support private methods"
                            ).range(annotationTextRange).create()
                        } else if (protectedMethodNames.contains(contents)) {
                            annotationHolder.newAnnotation(
                                HighlightSeverity.WARNING,
                                "Method '$contents' is protected. Mocking protected methods is not suggested.  Further guidance can be found here http://docs.mockery.io/en/latest/reference/protected_methods.html"
                            ).range(annotationTextRange).create()
                        }
                    }
                }
            }
        }
    }
}
