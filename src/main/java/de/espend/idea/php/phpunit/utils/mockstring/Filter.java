package de.espend.idea.php.phpunit.utils.mockstring;

import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpModifier;
import de.espend.idea.php.phpunit.utils.mockstring.FilterConfig.Item;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class Filter {

    private boolean isMethodsAllowed = false;
    private boolean isFieldsAllowed = false;

    private final List<String> allowedMethods = new ArrayList<>();
    private final List<String> allowedModifiers = new ArrayList<>();
    private final List<String> disallowedMethods = new ArrayList<>();
    private final List<String> describedMethods = new ArrayList<>();

    private PhpClass phpClass;

    public void allowMethod(String methodName) {
        allowMethods();
        disallowedMethods.remove(methodName);
        allowedMethods.add(methodName);
    }

    public void disallowMethod(String methodName) {
        allowMethods();
        allowedMethods.remove(methodName);
        disallowedMethods.add(methodName);
    }

    public void describeMethod(String methodName) {
        describedMethods.add(methodName);
    }

    public void allowModifier(String modifierName) {
        allowedModifiers.add(modifierName);
    }

    public void allowModifier(PhpModifier modifier) {
        allowModifier(modifier.toString());
    }

    public void allowMethods() {
        isMethodsAllowed = true;
    }

    public void allowMethods(List<String> methodNames) {
        for (String methodName : methodNames) {
            allowMethod(methodName);
        }
    }

    public void describeMethods(List<String> methodNames) {
        for (String methodName : methodNames) {
            describeMethod(methodName);
        }
    }

    public void allowFields() {
        isFieldsAllowed = true;
    }

    protected boolean isMethodAllowed(String methodName) {
        return isMethodsAllowed && !disallowedMethods.contains(methodName) && (allowedMethods.isEmpty() || allowedMethods.contains(methodName));
    }

    public boolean isMethodAllowed(Method method) {
        return isMethodAllowed(method.getName()) && isModifierAllowed(method.getModifier());
    }

    public boolean isMethodDescribed(String methodName) {
        return describedMethods.contains(methodName);
    }

    public boolean isMethodDescribed(@NotNull Method method) {
        return isMethodDescribed(method.getName());
    }

    protected boolean isFieldAllowed(String fieldName) {
        return isFieldsAllowed;
    }

    public boolean isFieldAllowed(Field field) {
        return !(field instanceof PhpDocProperty) && isFieldAllowed(field.getName()) && isModifierAllowed(field.getModifier());
    }

    protected boolean isModifierAllowed(String modifierName) {
        return allowedModifiers.isEmpty() || allowedModifiers.contains(modifierName);
    }

    protected boolean isModifierAllowed(PhpModifier modifier) {
        return isModifierAllowed(modifier.toString());
    }

    public void setPhpClass(PhpClass phpClass) {
        this.phpClass = phpClass;
    }

    public PhpClass getPhpClass() {
        return phpClass;
    }

    public record Context(@NotNull Item filterConfigItem, @NotNull MethodReference methodReference) {
        @NotNull
        public Item getFilterConfigItem() {
            return filterConfigItem;
        }

        @NotNull
        public MethodReference getMethodReference() {
            return methodReference;
        }
    }
}
