package de.espend.idea.php.phpunit.utils.mockstring

import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.Variable

object ClassFinder {
    @JvmStatic
    fun find(methodReference: MethodReference): Result? {
        var methodNameToFind = "setMethods"
        var mockBuilderMethodReference = MockStringPsiUtil.findMethodReference(methodReference, "getMockBuilder")
        if (mockBuilderMethodReference == null) {
            val methodName = methodReference.name
            if (methodName != null && (methodName.startsWith("getMock") || methodName.startsWith("createMock"))) {
                mockBuilderMethodReference = methodReference
                methodNameToFind = if (methodName.startsWith("createMock")) {
                    "getMock"
                } else {
                    methodName
                }
            }
        }

        if (mockBuilderMethodReference == null) {
            return null
        }

        val filterConfigItem = FilterFactory.getConfig().getItem(methodNameToFind)
        if (filterConfigItem == null) {
            return null
        }

        val phpClass = MockStringPsiUtil.resolveClassFromMethodReference(mockBuilderMethodReference)
        if (phpClass == null) {
            return null
        }

        return Result(phpClass, filterConfigItem.parameterNumber)
    }

    @JvmStatic
    fun find(variable: Variable): Result? {
        val methodReference = MockStringPsiUtil.findClosestAssignment(variable)
        return if (methodReference == null) null else find(methodReference)
    }

    data class Result(
        val phpClass: PhpClass,
        val parameterNumber: Int
    )
}
