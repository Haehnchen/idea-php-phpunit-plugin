package de.espend.idea.php.phpunit.utils.mockstring;

import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.Variable;
import de.espend.idea.php.phpunit.utils.mockstring.FilterConfig.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ClassFinder {

    private ClassFinder() {
    }

    @Nullable
    public static Result find(@NotNull MethodReference methodReference) {
        String methodNameToFind = "setMethods";
        MethodReference mockBuilderMethodReference = MockStringPsiUtil.findMethodReference(methodReference, "getMockBuilder");
        if (mockBuilderMethodReference == null) {
            String methodName = methodReference.getName();
            if (methodName != null && (methodName.startsWith("getMock") || methodName.startsWith("createMock"))) {
                mockBuilderMethodReference = methodReference;
                if (methodName.startsWith("createMock")) {
                    methodNameToFind = "getMock";
                } else {
                    methodNameToFind = methodName;
                }
            }
        }

        if (mockBuilderMethodReference == null) {
            return null;
        }

        Item filterConfigItem = FilterFactory.getConfig().getItem(methodNameToFind);
        if (filterConfigItem == null) {
            return null;
        }

        PhpClass phpClass = MockStringPsiUtil.resolveClassFromMethodReference(mockBuilderMethodReference);
        if (phpClass == null) {
            return null;
        }

        return new Result(phpClass, filterConfigItem.getParameterNumber());
    }

    @Nullable
    public static Result find(@NotNull Variable variable) {
        MethodReference methodReference = MockStringPsiUtil.findClosestAssignment(variable);
        return methodReference == null ? null : find(methodReference);
    }

    public record Result(@NotNull PhpClass phpClass, int parameterNumber) {
        @NotNull
        public PhpClass getPhpClass() {
            return phpClass;
        }

        public int getParameterNumber() {
            return parameterNumber;
        }
    }
}
