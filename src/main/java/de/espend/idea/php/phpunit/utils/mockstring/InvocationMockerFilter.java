package de.espend.idea.php.phpunit.utils.mockstring;

import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.PhpModifier;
import com.jetbrains.php.lang.psi.elements.Variable;
import de.espend.idea.php.phpunit.utils.mockstring.ClassFinder.Result;
import de.espend.idea.php.phpunit.utils.mockstring.Filter.Context;

import java.util.List;

public class InvocationMockerFilter extends Filter {

    public InvocationMockerFilter(Context context) {
        Variable variable = (Variable) PsiTreeUtil.getDeepestFirst(context.getMethodReference()).getParent();
        MethodReference methodReference = MockStringPsiUtil.findClosestAssignment(variable);

        if (methodReference != null) {
            Result classFinderResult = ClassFinder.find(methodReference);
            if (classFinderResult != null) {
                allowModifier(PhpModifier.PUBLIC_ABSTRACT_DYNAMIC);
                allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC);
                allowModifier(PhpModifier.PROTECTED_ABSTRACT_DYNAMIC);
                allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_DYNAMIC);

                setPhpClass(classFinderResult.getPhpClass());

                MethodReference definitionMethodReference = MockStringPsiUtil.findMethodReference(methodReference, "setMethods");
                if (definitionMethodReference == null) {
                    definitionMethodReference = methodReference;
                    allowMethods();
                }

                ParameterList parameterList = definitionMethodReference.getParameterList();
                if (parameterList != null) {
                    List<String> methodNames = MockStringPsiUtil.getArrayParameterValues(parameterList, classFinderResult.getParameterNumber());
                    if (methodNames != null) {
                        allowMethods(methodNames);
                    }
                }
            }
        }
    }
}
