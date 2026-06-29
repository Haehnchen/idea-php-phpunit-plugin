package de.espend.idea.php.phpunit.utils.mockstring;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import de.espend.idea.php.phpunit.utils.mockstring.Filter.Context;
import de.espend.idea.php.phpunit.utils.mockstring.FilterConfig.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FilterFactory {

    private static final FilterConfig CONFIG = createConfig();

    private FilterFactory() {
    }

    private static FilterConfig createConfig() {
        return new FilterConfig()
                .add(new Item("PHPUnit_Framework_MockObject_MockBuilder", "setMethods", 1, MockBuilderFilter.class))
                .add(new Item("PHPUnit_Framework_TestCase", "getMock", 2, MockBuilderFilter.class))
                .add(new Item("PHPUnit_Framework_TestCase", "getMockClass", 2, MockBuilderFilter.class))
                .add(new Item("PHPUnit_Framework_TestCase", "getMockForAbstractClass", 7, MockBuilderFilter.class))
                .add(new Item("PHPUnit_Framework_TestCase", "getMockForTrait", 7, MockBuilderFilter.class))
                .add(new Item("PHPUnit_Framework_MockObject_Builder_InvocationMocker", "method", 1, InvocationMockerFilter.class))
                .add(new Item("PHPUnit_Framework_MockObject_MockObject", "method", 1, InvocationMockerFilter.class))
                .add(new Item("PHPUnit\\Framework\\MockObject\\Builder\\InvocationMocker", "method", 1, InvocationMockerFilter.class))
                .add(new Item("PHPUnit\\Framework\\MockObject\\MockObject", "method", 1, InvocationMockerFilter.class))
                .add(new Item("MethodMock", "resetMethodCalledStack", 2, MethodMockFilter.class))
                .add(new Item("MethodMock", "getCalledArgs", 2, MethodMockFilter.class))
                .add(new Item("MethodMock", "isMethodCalled", 2, MethodMockFilter.class))
                .add(new Item("MethodMock", "countMethodCalled", 2, MethodMockFilter.class))
                .add(new Item("MethodMock", "revertMethod", 2, MethodMockFilter.class))
                .add(new Item("MethodMock", "interceptMethodByCode", 2, MethodMockFilter.class))
                .add(new Item("MethodMock", "interceptMethod", 2, MethodMockFilter.class))
                .add(new Item("MethodMock", "mockMethodResult", 2, MethodMockFilter.class))
                .add(new Item("MethodMock", "mockMethodResultByMap", 2, MethodMockFilter.class))
                .add(new Item("MethodMock", "revertMethodResult", 2, MethodMockFilter.class))
                .add(new Item("MethodMock", "callProtectedMethod", 2, MethodMockFilter.class))
                .add(new Item("PHPUnit_Helper", "getProtectedPropertyValue", 2, MethodMockFilter.class))
                .add(new Item("PHPUnit_Helper", "setProtectedPropertyValue", 2, MethodMockFilter.class))
                .add(new Item("PHPUnit_Helper", "callProtectedMethod", 2, MethodMockFilter.class));
    }

    @Nullable
    public static Filter getFilter(@NotNull PsiElement parameter) {
        PsiElement parentParameter = PsiTreeUtil.getParentOfType(parameter, ArrayCreationExpression.class);
        if (parentParameter != null) {
            parameter = parentParameter;
        }

        MethodReference methodReference = PsiTreeUtil.getParentOfType(parameter, MethodReference.class);
        if (methodReference == null) {
            return null;
        }

        Method resolvedMethod = MockStringPsiUtil.resolveMethod(methodReference);
        if (resolvedMethod == null) {
            return null;
        }

        PhpClass resolvedClass = resolvedMethod.getContainingClass();
        if (resolvedClass == null) {
            return null;
        }

        String methodName = resolvedMethod.getName();
        int parameterNumber = MockStringPsiUtil.getParameterNumber(parameter);

        do {
            String className = resolvedClass.getName();
            Item filterConfigItem = CONFIG.getItem(className, methodName);
            if (filterConfigItem != null && filterConfigItem.getParameterNumber() == parameterNumber) {
                Class<? extends Filter> filterClass = filterConfigItem.getFilterClass();
                Context filterContext = new Context(filterConfigItem, methodReference);
                return getFilter(filterClass, filterContext);
            }
        }
        while ((resolvedClass = resolvedClass.getSuperClass()) != null);

        return null;
    }

    @Nullable
    private static Filter getFilter(@NotNull Class<? extends Filter> filterClass, @NotNull Context filterContext) {
        Filter filter;
        try {
            filter = filterClass.getDeclaredConstructor(Context.class).newInstance(filterContext);
        } catch (Exception e) {
            filter = null;
        }
        return filter;
    }

    public static FilterConfig getConfig() {
        return new FilterConfig(CONFIG);
    }
}
