package de.espend.idea.php.phpunit.utils.mockstring;

import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.PhpModifier;
import de.espend.idea.php.phpunit.utils.mockstring.Filter.Context;

import java.util.List;

public class MockBuilderFilter extends Filter {

    public MockBuilderFilter(Context context) {
        allowMethods();
        allowModifier(PhpModifier.PUBLIC_ABSTRACT_DYNAMIC);
        allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC);
        allowModifier(PhpModifier.PROTECTED_ABSTRACT_DYNAMIC);
        allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_DYNAMIC);

        MethodReference methodReference = context.getMethodReference();

        ClassFinder.Result classFinderResult = ClassFinder.find(methodReference);
        if (classFinderResult != null) {
            setPhpClass(classFinderResult.getPhpClass());
        }

        disallowMethod("__construct");
        disallowMethod("__destruct");

        ParameterList parameterList = methodReference.getParameterList();
        if (parameterList != null) {
            List<String> methodNames = MockStringPsiUtil.getArrayParameterValues(parameterList, context.getFilterConfigItem().getParameterNumber());
            if (methodNames != null) {
                describeMethods(methodNames);
            }
        }
    }
}
