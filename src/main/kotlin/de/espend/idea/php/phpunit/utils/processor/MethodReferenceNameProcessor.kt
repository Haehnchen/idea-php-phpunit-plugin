package de.espend.idea.php.phpunit.utils.processor

import de.espend.idea.php.phpunit.utils.ChainVisitorUtil
import de.espend.idea.php.phpunit.utils.PhpElementsUtil
import com.jetbrains.php.lang.psi.elements.MethodReference

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
class MethodReferenceNameProcessor private constructor(private val methodName: String) : ChainVisitorUtil.ChainProcessorInterface {
    var parameter: String? = null
        private set

    override fun process(methodReference: MethodReference): Boolean {
        if (methodName == methodReference.name) {
            val parameters = methodReference.parameters

            if (parameters.isNotEmpty()) {
                parameter = PhpElementsUtil.getStringValue(parameters[0])
            }

            return false
        }

        return true
    }

    companion object {
        fun createParameterWithCurrent(methodReference: MethodReference, vararg methodNames: String): String? {
            for (methodName in methodNames) {
                val processor = MethodReferenceNameProcessor(methodName)
                ChainVisitorUtil.visit(methodReference, processor, false)

                val parameter = processor.parameter
                if (parameter != null) {
                    return parameter
                }
            }

            return null
        }
    }
}
