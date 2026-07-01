package de.espend.idea.php.phpunit.utils.mockstring

import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpModifier
import com.jetbrains.php.lang.psi.elements.Variable

class InvocationMockerFilter(context: Filter.Context) : Filter() {
    init {
        val variable = PsiTreeUtil.getDeepestFirst(context.methodReference).parent as Variable
        val methodReference = MockStringPsiUtil.findClosestAssignment(variable)

        if (methodReference != null) {
            val classFinderResult = ClassFinder.find(methodReference)
            if (classFinderResult != null) {
                allowModifier(PhpModifier.PUBLIC_ABSTRACT_DYNAMIC)
                allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC)
                allowModifier(PhpModifier.PROTECTED_ABSTRACT_DYNAMIC)
                allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_DYNAMIC)

                phpClass = classFinderResult.phpClass

                var definitionMethodReference: MethodReference? = MockStringPsiUtil.findMethodReference(methodReference, "setMethods")
                if (definitionMethodReference == null) {
                    definitionMethodReference = methodReference
                    allowMethods()
                }

                val parameterList = definitionMethodReference.parameterList
                if (parameterList != null) {
                    val methodNames = MockStringPsiUtil.getArrayParameterValues(parameterList, classFinderResult.parameterNumber)
                    if (methodNames != null) {
                        allowMethods(methodNames)
                    }
                }
            }
        }
    }
}
