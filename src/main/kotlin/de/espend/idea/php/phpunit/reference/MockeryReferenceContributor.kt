package de.espend.idea.php.phpunit.reference

import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import de.espend.idea.php.phpunit.utils.MockeryReferencingUtil
import de.espend.idea.php.phpunit.utils.PatternUtil
import org.apache.commons.lang3.StringUtils
import java.util.Arrays

class MockeryReferenceContributor : PsiReferenceContributor() {
    private enum class Scope(
        val psiElementPattern: PsiElementPattern.Capture<StringLiteralExpression>,
        val getMockCreationParametersMethod: (StringLiteralExpression) -> Array<String>?,
        val processMockCreationParametersMethod: (PsiElement, String, Array<String>) -> Array<PsiReference>
    ) {
        PARAMETER(
            PatternUtil.getMethodReferenceWithParameterPattern(),
            MockeryReferencingUtil::findMockeryMockParametersOnParameterScope,
            MockeryReferenceContributor::processParametersAsClasses
        ),
        ARRAY_HASH(
            PatternUtil.getMethodReferenceWithArrayHashPattern(),
            MockeryReferencingUtil::findMockeryMockParametersOnArrayHashScope,
            MockeryReferenceContributor::processParametersAsClasses
        ),
        ARRAY_ELEMENT(
            PatternUtil.getMethodReferenceWithArrayElementPattern(),
            MockeryReferencingUtil::findMockeryMockParametersOnArrayElementScope,
            MockeryReferenceContributor::processParametersAsClasses
        ),
        PARTIAL_STRING(
            PatternUtil.getMethodReferenceWithParameterPattern(),
            MockeryReferencingUtil::findMockeryMockParametersOnPartialMockStringDeclarationScope,
            { psiElement, _, mockCreationParameters ->
                processParametersAsClassAndMethodNames(psiElement, mockCreationParameters)
            }
        ),
        PARTIAL_CONCATENATION(
            PatternUtil.getMethodReferenceWithConcatenationPattern(),
            MockeryReferencingUtil::findMockeryMockParametersOnPartialMockConcatenationDeclarationScope,
            { psiElement, _, mockCreationParameters ->
                processParametersAsClassAndMethodNames(psiElement, mockCreationParameters)
            }
        );

        fun getMockCreationParameters(exp: StringLiteralExpression): Array<String>? {
            return getMockCreationParametersMethod(exp)
        }

        fun processMockCreationParameters(
            psiElement: PsiElement,
            contents: String,
            mockCreationParameters: Array<String>
        ): Array<PsiReference> {
            return processMockCreationParametersMethod(psiElement, contents, mockCreationParameters)
        }
    }

    /**
     * Provides a reference provider. The provider will take a psi element and, if able, return a Reference
     * ([PhpClassMethodReference]) to it's declaration.
     * Provider wants to check that the psi element is a mocked method.
     *
     * The reference has three parts: The psi element we want a reference for; the method (`content`)
     * which the psi element is associated with; and the clazz (`parameter`) where the method is found.
     *
     * For example the element may be `calledMethod`, the method `calledMethod`
     * and the clazz would be the `Dependency` class.
     *
     * @param psiReferenceRegistrar we register a reference provider with this registrar.
     */
    override fun registerReferenceProviders(psiReferenceRegistrar: PsiReferenceRegistrar) {
        registerReferenceByScope(psiReferenceRegistrar, Scope.PARAMETER)
        registerReferenceByScope(psiReferenceRegistrar, Scope.ARRAY_HASH)
        registerReferenceByScope(psiReferenceRegistrar, Scope.ARRAY_ELEMENT)
        registerReferenceByScope(psiReferenceRegistrar, Scope.PARTIAL_STRING)
        registerReferenceByScope(psiReferenceRegistrar, Scope.PARTIAL_CONCATENATION)
    }

    private fun registerReferenceByScope(psiReferenceRegistrar: PsiReferenceRegistrar, scope: Scope) {
        psiReferenceRegistrar.registerReferenceProvider(
            scope.psiElementPattern,
            getProvider(scope)
        )
    }

    private fun getProvider(scope: Scope): PsiReferenceProvider {
        return object : PsiReferenceProvider() {
            override fun getReferencesByElement(
                psiElement: PsiElement,
                processingContext: ProcessingContext
            ): Array<PsiReference> {
                if (psiElement is StringLiteralExpression) {
                    val contents = psiElement.contents
                    if (StringUtils.isNotBlank(contents)) {
                        val mockCreationParameters = scope.getMockCreationParameters(psiElement)

                        if (mockCreationParameters != null) {
                            return scope.processMockCreationParameters(psiElement, contents, mockCreationParameters)
                        }
                    }
                }
                return emptyArray()
            }
        }
    }

    private companion object {
        fun processParametersAsClasses(
            psiElement: PsiElement,
            contents: String,
            mockCreationParameters: Array<String>
        ): Array<PsiReference> {
            val references = ArrayList<PsiReference>()

            for (mockCreationParameter in mockCreationParameters) {
                references.add(PhpClassMethodReference(psiElement as StringLiteralExpression, contents, mockCreationParameter))
            }

            return references.toTypedArray()
        }

        fun processParametersAsClassAndMethodNames(
            psiElement: PsiElement,
            mockCreationParameters: Array<String>?
        ): Array<PsiReference> {
            if (mockCreationParameters == null || mockCreationParameters.size <= 1) {
                return emptyArray()
            }

            val className = mockCreationParameters[0]
            val methodNames = Arrays.copyOfRange(mockCreationParameters, 1, mockCreationParameters.size)

            val references = ArrayList<PsiReference>()

            for (method in methodNames) {
                references.add(PhpClassMethodReferenceForPartialMock(psiElement as StringLiteralExpression, method, className))
            }
            return references.toTypedArray()
        }
    }
}
