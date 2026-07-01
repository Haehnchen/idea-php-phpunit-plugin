package de.espend.idea.php.phpunit.utils.mockstring

import com.jetbrains.php.lang.psi.elements.PhpModifier

class MethodMockFilter(context: Filter.Context) : Filter() {
    init {
        val methodReference = context.methodReference
        val phpClass = MockStringPsiUtil.resolveClassFromMethodReference(methodReference)

        if (phpClass != null) {
            this.phpClass = phpClass

            val methodName = context.filterConfigItem.methodName
            if (methodName == "callProtectedMethod") {
                allowMethods()
                allowModifier(PhpModifier.PUBLIC_FINAL_DYNAMIC)
                allowModifier(PhpModifier.PUBLIC_FINAL_STATIC)
                allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC)
                allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_STATIC)
                allowModifier(PhpModifier.PROTECTED_FINAL_DYNAMIC)
                allowModifier(PhpModifier.PROTECTED_FINAL_STATIC)
                allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_DYNAMIC)
                allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_STATIC)
            } else if (methodName.endsWith("ProtectedPropertyValue")) {
                allowFields()
                allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC)
                allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_STATIC)
                allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_DYNAMIC)
                allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_STATIC)
            } else {
                allowMethods()
            }
        }
    }
}
