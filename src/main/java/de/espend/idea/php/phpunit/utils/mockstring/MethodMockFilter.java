package de.espend.idea.php.phpunit.utils.mockstring;

import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpModifier;
import de.espend.idea.php.phpunit.utils.mockstring.Filter.Context;

public class MethodMockFilter extends Filter {

    public MethodMockFilter(Context context) {
        MethodReference methodReference = context.getMethodReference();
        PhpClass phpClass = MockStringPsiUtil.resolveClassFromMethodReference(methodReference);
        if (phpClass == null) {
            return;
        }

        setPhpClass(phpClass);

        String methodName = context.getFilterConfigItem().getMethodName();
        if (methodName.equals("callProtectedMethod")) {
            allowMethods();
            allowModifier(PhpModifier.PUBLIC_FINAL_DYNAMIC);
            allowModifier(PhpModifier.PUBLIC_FINAL_STATIC);
            allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC);
            allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_STATIC);
            allowModifier(PhpModifier.PROTECTED_FINAL_DYNAMIC);
            allowModifier(PhpModifier.PROTECTED_FINAL_STATIC);
            allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_DYNAMIC);
            allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_STATIC);
        } else if (methodName.endsWith("ProtectedPropertyValue")) {
            allowFields();
            allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC);
            allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_STATIC);
            allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_DYNAMIC);
            allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_STATIC);
        } else {
            allowMethods();
        }
    }
}
