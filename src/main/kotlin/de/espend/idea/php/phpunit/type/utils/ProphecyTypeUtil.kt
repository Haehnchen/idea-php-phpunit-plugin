package de.espend.idea.php.phpunit.type.utils

import com.jetbrains.php.lang.psi.elements.MethodReference
import de.espend.idea.php.phpunit.utils.processor.IndexLessMethodParameterChainProcessor

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
object ProphecyTypeUtil {
    /**
     * Find prophesize in chaining method references, does do access index
     * and its safe to use inside index processes
     *
     * $x->prophesize('foobar')->foo()
     * $x->prophesize(Foo::class)->foo()
     */
    @JvmStatic
    fun getLocalProphesizeType(methodReference: MethodReference): String? {
        return IndexLessMethodParameterChainProcessor.createParameter(methodReference, "prophesize")
    }
}
