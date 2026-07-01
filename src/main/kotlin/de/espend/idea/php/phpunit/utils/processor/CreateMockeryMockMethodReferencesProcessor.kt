package de.espend.idea.php.phpunit.utils.processor

import de.espend.idea.php.phpunit.utils.ChainVisitorUtil
import de.espend.idea.php.phpunit.utils.MockeryReferencingUtil
import de.espend.idea.php.phpunit.utils.PhpElementsUtil
import com.jetbrains.php.lang.psi.elements.MethodReference

class CreateMockeryMockMethodReferencesProcessor : ChainVisitorUtil.ChainProcessorInterface {
    private var returnParameters: MutableList<String>? = null

    /**
     * Takes methodReference psiElement and checks if this is a mock declaration (e.g. Mockery::mock))
     * or if ChainVisitorUtil can keep searching. See [ChainVisitorUtil]
     */
    override fun process(methodReference: MethodReference): Boolean {
        /*
         check if methodReference looks like: $Mockery::mock(Dependency::class) etc
         In this case we want to set parameter to: Dependency::class
         */
        if (!PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "Mockery", "mock") &&
            !PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "Mockery", "spy") &&
            !PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "Mockery", "namedMock")
        ) {
            // allowed chain of classes types
            return PhpElementsUtil.isMethodReferenceInstanceOf(
                methodReference,
                *MockeryReferencingUtil.allowedChainClasses
            )
        }

        val parameters = methodReference.parameters
        returnParameters = ArrayList()

        for (parameter in parameters) {
            var value = PhpElementsUtil.getStringValue(parameter)
            if (value == null) {
                continue
            }

            // Used to alter the mock created, but has no effect on referencing
            value = value.replace(" ", "")
            value = value.replace("alias:", "")
            value = value.replace("overload:", "")

            // remove anything in square brackets (these are from generated partials
            value = value.replace("\\[(.*?)\\]".toRegex(), "")

            // Can have situation:
            // = Mockery::mock('MockeryPlugin\DemoProject\Dependency, MockeryPlugin\DemoProject\AlternativeInterface');
            returnParameters!!.addAll(value.split(","))
        }

        return false
    }

    fun getParameters(): Array<String>? {
        return returnParameters?.toTypedArray()
    }

    companion object {
        /**
         * initiates the visit loop in ChainVisitorUtil.
         *
         * @param methodReference the psiElement we are finding a target for
         * @return the name of the class for the target of the reference
         */
        fun createParameters(methodReference: MethodReference): Array<String>? {
            val processor = CreateMockeryMockMethodReferencesProcessor()
            ChainVisitorUtil.visit(methodReference, processor)
            return processor.getParameters()
        }
    }
}
