package de.espend.idea.php.phpunit.utils.processor

import de.espend.idea.php.phpunit.utils.ChainVisitorUtil
import de.espend.idea.php.phpunit.utils.PhpElementsUtil
import com.jetbrains.php.lang.psi.elements.MethodReference

/**
 * Try to find a "createMock" method inside a call chain
 *
 *  - $this->createMock(...)->expects()->method('foobar')
 *  - $this->foo->expects()->method('foobar') // via eg "setup" method
 *
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
class CreateMockMethodReferenceProcessor : ChainVisitorUtil.ChainProcessorInterface {
    var parameter: String? = null
        private set

    override fun process(methodReference: MethodReference): Boolean {
        val isMockShortcutMethod = PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "\\PHPUnit\\Framework\\TestCase", "createMock")
            || PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "PHPUnit_Framework_TestCase", "createMock")
            || PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "\\PHPUnit\\Framework\\TestCase", "createPartialMock")
            || PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "PHPUnit_Framework_TestCase", "createPartialMock")

        if (isMockShortcutMethod) {
            val parameters = methodReference.parameters

            if (parameters.isNotEmpty()) {
                parameter = PhpElementsUtil.getStringValue(parameters[0])
            }

            return false
        }

        // allowed chain of classes types
        return PhpElementsUtil.isMethodReferenceInstanceOf(
            methodReference,
            "PHPUnit_Framework_MockObject_MockObject",
            "PHPUnit_Framework_MockObject_Builder_InvocationMocker",
            "PHPUnit\\Framework\\MockObject\\MockObject",
            "PHPUnit\\Framework\\MockObject\\Builder\\InvocationMocker"
        )
    }

    companion object {
        @JvmStatic
        fun createParameter(methodReference: MethodReference): String? {
            val processor = CreateMockMethodReferenceProcessor()
            ChainVisitorUtil.visit(methodReference, processor)
            return processor.parameter
        }
    }
}
