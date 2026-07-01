package de.espend.idea.php.phpunit.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.completion.PhpLookupElement
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import de.espend.idea.php.phpunit.utils.MockeryReferencingUtil
import de.espend.idea.php.phpunit.utils.PatternUtil

class MockeryCompletionContributor : CompletionContributor() {
    private enum class Scope(
        val pattern: PsiElementPattern.Capture<PsiElement>,
        val getMockCreationParametersMethod: (StringLiteralExpression) -> Array<String>?,
        val processMockCreationParametersMethod: (CompletionResultSet, PsiElement, Array<String>) -> Unit
    ) {
        PARAMETER(
            PatternUtil.getMethodReferenceWithParameterInsideTokenStringPattern(),
            MockeryReferencingUtil::findMockeryMockParametersOnParameterScope,
            MockeryCompletionContributor::processParametersAsClasses
        ),
        ARRAY_HASH(
            PatternUtil.getMethodReferenceWithArrayHashInsideTokenStringPattern(),
            MockeryReferencingUtil::findMockeryMockParametersOnArrayHashScope,
            MockeryCompletionContributor::processParametersAsClasses
        ),
        ARRAY_ELEMENT(
            PatternUtil.getMethodReferenceWithArrayElementInsideTokenStringPattern(),
            MockeryReferencingUtil::findMockeryMockParametersOnArrayElementScope,
            MockeryCompletionContributor::processParametersAsClasses
        ),
        PARTIAL_STRING(
            PatternUtil.getMethodReferenceWithParameterInsideTokenStringPattern(),
            MockeryReferencingUtil::findMockeryMockParametersOnPartialMockStringDeclarationScope,
            MockeryCompletionContributor::processParametersAsClassAndMethodNames
        ),
        PARTIAL_CONCATENATION(
            PatternUtil.getMethodReferenceWithConcatenationInsideTokenStringPattern(),
            MockeryReferencingUtil::findMockeryMockParametersOnPartialMockConcatenationDeclarationScope,
            MockeryCompletionContributor::processParametersAsClassAndMethodNames
        );

        fun getMockCreationParameters(exp: StringLiteralExpression): Array<String>? {
            return getMockCreationParametersMethod(exp)
        }

        fun processMockCreationParameters(
            resultSet: CompletionResultSet,
            psiElement: PsiElement,
            mockCreationParameters: Array<String>
        ) {
            processMockCreationParametersMethod(resultSet, psiElement, mockCreationParameters)
        }
    }

    init {
        extendByScope(Scope.PARAMETER)
        extendByScope(Scope.ARRAY_HASH)
        extendByScope(Scope.ARRAY_ELEMENT)
        extendByScope(Scope.PARTIAL_STRING)
        extendByScope(Scope.PARTIAL_CONCATENATION)
    }

    private fun extendByScope(scope: Scope) {
        extend(
            CompletionType.BASIC,
            scope.pattern,
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    completionParameters: CompletionParameters,
                    processingContext: ProcessingContext,
                    resultSet: CompletionResultSet
                ) {
                    val psiElement = completionParameters.position
                    val parent = psiElement.parent
                    if (parent is StringLiteralExpression) {
                        val mockCreationParameters = scope.getMockCreationParameters(parent)
                        if (mockCreationParameters != null) {
                            scope.processMockCreationParameters(resultSet, psiElement, mockCreationParameters)
                        }
                    }
                }
            }
        )
    }

    private companion object {
        fun processParametersAsClasses(
            resultSet: CompletionResultSet,
            psiElement: PsiElement,
            mockCreationParameters: Array<String>
        ) {
            for (parameter in mockCreationParameters) {
                for (phpClass in PhpIndex.getInstance(psiElement.project).getAnyByFQN(parameter)) {
                    resultSet.addAllElements(
                        phpClass.methods
                            .filter { method: Method -> !method.access.isPublic || !method.name.startsWith("__") }
                            .map { method: Method -> PhpLookupElement(method) as LookupElement }
                            .toSet()
                    )
                }
            }
        }

        fun processParametersAsClassAndMethodNames(
            resultSet: CompletionResultSet,
            psiElement: PsiElement,
            mockCreationParameters: Array<String>
        ) {
            if (mockCreationParameters.isNotEmpty()) {
                val className = mockCreationParameters[0]

                processParametersAsClasses(resultSet, psiElement, arrayOf(className))
            }
        }
    }
}
