package de.espend.idea.php.phpunit.utils.processor

import de.espend.idea.php.phpunit.type.utils.PhpTypeProviderUtil
import de.espend.idea.php.phpunit.utils.ChainVisitorUtil
import com.jetbrains.php.lang.psi.elements.MethodReference
import java.util.Arrays

class IndexLessMethodParameterChainProcessor(vararg methods: String) : ChainVisitorUtil.ChainProcessorInterface {
    private val methods: Collection<String> = Arrays.asList(*methods)

    var parameter: String? = null
        private set

    override fun process(methodReference: MethodReference): Boolean {
        if (methods.contains(methodReference.name)) {
            parameter = PhpTypeProviderUtil.getReferenceSignatureByFirstParameter(methodReference)
            return false
        }

        return true
    }

    companion object {
        @JvmStatic
        fun createParameter(methodReference: MethodReference, vararg methods: String): String? {
            val processor = IndexLessMethodParameterChainProcessor(*methods)
            ChainVisitorUtil.visit(methodReference, processor)
            return processor.parameter
        }
    }
}
