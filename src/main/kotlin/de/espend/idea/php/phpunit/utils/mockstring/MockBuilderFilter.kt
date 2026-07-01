package de.espend.idea.php.phpunit.utils.mockstring

import com.jetbrains.php.lang.psi.elements.PhpModifier

class MockBuilderFilter(context: Filter.Context) : Filter() {
    init {
        allowMethods()
        allowModifier(PhpModifier.PUBLIC_ABSTRACT_DYNAMIC)
        allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC)
        allowModifier(PhpModifier.PROTECTED_ABSTRACT_DYNAMIC)
        allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_DYNAMIC)

        val methodReference = context.methodReference

        val classFinderResult = ClassFinder.find(methodReference)
        if (classFinderResult != null) {
            phpClass = classFinderResult.phpClass
        }

        disallowMethod("__construct")
        disallowMethod("__destruct")

        val parameterList = methodReference.parameterList
        if (parameterList != null) {
            val methodNames = MockStringPsiUtil.getArrayParameterValues(parameterList, context.filterConfigItem.parameterNumber)
            if (methodNames != null) {
                describeMethods(methodNames)
            }
        }
    }
}
