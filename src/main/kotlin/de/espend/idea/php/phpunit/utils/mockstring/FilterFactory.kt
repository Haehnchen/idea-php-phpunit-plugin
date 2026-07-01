package de.espend.idea.php.phpunit.utils.mockstring

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.MethodReference

object FilterFactory {
    private val CONFIG = createConfig()

    private fun createConfig(): FilterConfig {
        return FilterConfig()
            .add(FilterConfig.Item("PHPUnit_Framework_MockObject_MockBuilder", "setMethods", 1, MockBuilderFilter::class.java))
            .add(FilterConfig.Item("PHPUnit_Framework_TestCase", "getMock", 2, MockBuilderFilter::class.java))
            .add(FilterConfig.Item("PHPUnit_Framework_TestCase", "getMockClass", 2, MockBuilderFilter::class.java))
            .add(FilterConfig.Item("PHPUnit_Framework_TestCase", "getMockForAbstractClass", 7, MockBuilderFilter::class.java))
            .add(FilterConfig.Item("PHPUnit_Framework_TestCase", "getMockForTrait", 7, MockBuilderFilter::class.java))
            .add(FilterConfig.Item("PHPUnit_Framework_MockObject_Builder_InvocationMocker", "method", 1, InvocationMockerFilter::class.java))
            .add(FilterConfig.Item("PHPUnit_Framework_MockObject_MockObject", "method", 1, InvocationMockerFilter::class.java))
            .add(FilterConfig.Item("PHPUnit\\Framework\\MockObject\\Builder\\InvocationMocker", "method", 1, InvocationMockerFilter::class.java))
            .add(FilterConfig.Item("PHPUnit\\Framework\\MockObject\\MockObject", "method", 1, InvocationMockerFilter::class.java))
            .add(FilterConfig.Item("MethodMock", "resetMethodCalledStack", 2, MethodMockFilter::class.java))
            .add(FilterConfig.Item("MethodMock", "getCalledArgs", 2, MethodMockFilter::class.java))
            .add(FilterConfig.Item("MethodMock", "isMethodCalled", 2, MethodMockFilter::class.java))
            .add(FilterConfig.Item("MethodMock", "countMethodCalled", 2, MethodMockFilter::class.java))
            .add(FilterConfig.Item("MethodMock", "revertMethod", 2, MethodMockFilter::class.java))
            .add(FilterConfig.Item("MethodMock", "interceptMethodByCode", 2, MethodMockFilter::class.java))
            .add(FilterConfig.Item("MethodMock", "interceptMethod", 2, MethodMockFilter::class.java))
            .add(FilterConfig.Item("MethodMock", "mockMethodResult", 2, MethodMockFilter::class.java))
            .add(FilterConfig.Item("MethodMock", "mockMethodResultByMap", 2, MethodMockFilter::class.java))
            .add(FilterConfig.Item("MethodMock", "revertMethodResult", 2, MethodMockFilter::class.java))
            .add(FilterConfig.Item("MethodMock", "callProtectedMethod", 2, MethodMockFilter::class.java))
            .add(FilterConfig.Item("PHPUnit_Helper", "getProtectedPropertyValue", 2, MethodMockFilter::class.java))
            .add(FilterConfig.Item("PHPUnit_Helper", "setProtectedPropertyValue", 2, MethodMockFilter::class.java))
            .add(FilterConfig.Item("PHPUnit_Helper", "callProtectedMethod", 2, MethodMockFilter::class.java))
    }

    fun getFilter(parameter: PsiElement): Filter? {
        var currentParameter = parameter
        val parentParameter = PsiTreeUtil.getParentOfType(currentParameter, ArrayCreationExpression::class.java)
        if (parentParameter != null) {
            currentParameter = parentParameter
        }

        val methodReference = PsiTreeUtil.getParentOfType(currentParameter, MethodReference::class.java)
        if (methodReference == null) {
            return null
        }

        val resolvedMethod = MockStringPsiUtil.resolveMethod(methodReference)
        if (resolvedMethod == null) {
            return null
        }

        var resolvedClass = resolvedMethod.containingClass
        if (resolvedClass == null) {
            return null
        }

        val methodName = resolvedMethod.name
        val parameterNumber = MockStringPsiUtil.getParameterNumber(currentParameter)

        do {
            val className = resolvedClass!!.name
            val filterConfigItem = CONFIG.getItem(className, methodName)
            if (filterConfigItem != null && filterConfigItem.parameterNumber == parameterNumber) {
                val filterClass = filterConfigItem.filterClass
                val filterContext = Filter.Context(filterConfigItem, methodReference)
                return getFilter(filterClass, filterContext)
            }

            resolvedClass = resolvedClass.superClass
        } while (resolvedClass != null)

        return null
    }

    private fun getFilter(filterClass: Class<out Filter>, filterContext: Filter.Context): Filter? {
        val filter: Filter?
        try {
            filter = filterClass.getDeclaredConstructor(Filter.Context::class.java).newInstance(filterContext)
        } catch (e: Exception) {
            return null
        }
        return filter
    }

    fun getConfig(): FilterConfig {
        return FilterConfig(CONFIG)
    }
}
