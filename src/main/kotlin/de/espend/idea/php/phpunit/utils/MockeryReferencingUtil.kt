package de.espend.idea.php.phpunit.utils

import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.ConcatenationExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.PhpPsiElement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import de.espend.idea.php.phpunit.utils.processor.CreateMockeryMockMethodReferencesProcessor
import org.apache.commons.lang3.ArrayUtils
import org.apache.commons.lang3.StringUtils

object MockeryReferencingUtil {
    @JvmField
    val expectedClassNamesAndMethods: Array<Array<String>> = arrayOf(
        arrayOf("Mockery_MockInterface", "expects"),
        arrayOf("Mockery_LegacyMockInterface", "shouldReceive"),
        arrayOf("Mockery_LegacyMockInterface", "shouldHaveReceived"),
        arrayOf("Mockery_LegacyMockInterface", "shouldNotReceive"),
        arrayOf("Mockery_LegacyMockInterface", "shouldNotHaveReceived"),
        arrayOf("Mockery_MockInterface", "allows"),
        arrayOf("Mockery\\MockInterface", "expects"),
        arrayOf("Mockery\\LegacyMockInterface", "shouldReceive"),
        arrayOf("Mockery\\LegacyMockInterface", "shouldHaveReceived"),
        arrayOf("Mockery\\LegacyMockInterface", "shouldNotReceive"),
        arrayOf("Mockery\\LegacyMockInterface", "shouldNotHaveReceived"),
        arrayOf("Mockery\\MockInterface", "allows"),
    )

    @JvmField
    val allowedChainClasses: Array<String> = arrayOf(
        "Mockery_MockInterface",
        "Mockery_LegacyMockInterface",
        "Mockery\\MockInterface",
        "Mockery\\LegacyMockInterface",
    )

    /**
     * $foo = Mockery::mock('Foobar')
     * $foo->expects('<caret>')
     */
    @JvmStatic
    fun findMockeryMockParametersOnParameterScope(psiElement: StringLiteralExpression): Array<String>? {
        return findMockeryMockParametersOnParameterScopeInternal(psiElement)
    }

    private fun findMockeryMockParametersOnParameterScopeInternal(psiElement: PsiElement): Array<String>? {
        val parameterList = psiElement.parent
        if (parameterList !is ParameterList) {
            return emptyArray()
        }

        val methodReference = parameterList.parent
        if (methodReference !is MethodReference) {
            return emptyArray()
        }

        for (classAndMethod in expectedClassNamesAndMethods) {
            if (PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, classAndMethod[0], classAndMethod[1])) {
                return CreateMockeryMockMethodReferencesProcessor.createParameters(methodReference)
            }
        }

        return emptyArray()
    }

    /**
     * Consider situation <code>$this->dependency->shouldReceive(['calledMethod' => 'mocked result',
     * 'secondCalledMethod' => 'mocked result']);</code>
     * If psi element is 'calledMethod'
     */
    @JvmStatic
    fun findMockeryMockParametersOnArrayHashScope(psiElement: StringLiteralExpression): Array<String>? {
        val phpPsiElement = psiElement.parent
        if (phpPsiElement is PhpPsiElement) {
            val arrayHashElement = phpPsiElement.parent

            // we need to also check that this is the first child of the hash element (the method name)
            if (arrayHashElement is ArrayHashElement && arrayHashElement.firstChild == phpPsiElement) {
                val arrayCreationExpression = arrayHashElement.parent

                if (arrayCreationExpression is ArrayCreationExpression) {
                    // We are now at the parameter list level so we can use findCreateMockParameterOnParameterScope
                    return findMockeryMockParametersOnParameterScopeInternal(arrayCreationExpression)
                }
            }
        }

        return emptyArray()
    }

    @JvmStatic
    fun findMockeryMockParametersOnArrayElementScope(psiElement: StringLiteralExpression): Array<String>? {
        val phpPsiElement = psiElement.parent
        if (phpPsiElement is PhpPsiElement) {
            val arrayCreationExpression = phpPsiElement.parent

            if (arrayCreationExpression is ArrayCreationExpression) {
                // We are now at the parameter list level so we can use findCreateMockParameterOnParameterScope
                return findMockeryMockParametersOnParameterScopeInternal(arrayCreationExpression)
            }
        }

        return emptyArray()
    }

    @JvmStatic
    fun findMockeryMockParametersOnPartialMockStringDeclarationScope(psiElement: PsiElement): Array<String>? {
        val parameterList = psiElement.parent
        if (parameterList !is ParameterList) {
            return emptyArray()
        }

        val methodReference = parameterList.parent
        if (methodReference !is MethodReference) {
            return emptyArray()
        }

        if (PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "Mockery", "mock")) {
            // Generated Partials are identified by methods in square brackets i.e. 'Dependency[calledMethod]'
            val elemText = StringUtils.deleteWhitespace(psiElement.text.replace("'", "").replace("\"", ""))

            if (elemText.contains("[")) {
                val className = StringUtils.substringBefore(elemText, "[")
                val methodNames = StringUtils.substringBetween(elemText, "[", "]").split(",").toTypedArray()

                return ArrayUtils.insert(0, methodNames, className)
            }
        }

        return emptyArray()
    }

    @JvmStatic
    fun findMockeryMockParametersOnPartialMockConcatenationDeclarationScope(psiElement: PsiElement): Array<String>? {
        val concatenationExpression = psiElement.parent
        if (concatenationExpression !is ConcatenationExpression) {
            return emptyArray()
        }

        val parameterList = concatenationExpression.parent
        if (parameterList !is ParameterList) {
            return emptyArray()
        }

        val methodReference = parameterList.parent
        if (methodReference !is MethodReference) {
            return emptyArray()
        }

        if (PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "Mockery", "mock")) {
            // Generated Partials are identified by methods in square brackets i.e. 'Dependency::class . [calledMethod]'
            val concatenationValue = StringUtils.deleteWhitespace(PhpElementsUtil.getStringValue(concatenationExpression))

            if (concatenationValue != null && concatenationValue.contains("[")) {
                val className = StringUtils.substringBefore(concatenationValue, "[")
                val methodNames = StringUtils.substringBetween(concatenationValue, "[", "]").split(",").toTypedArray()

                return ArrayUtils.insert(0, methodNames, className)
            }
        }

        return emptyArray()
    }
}
