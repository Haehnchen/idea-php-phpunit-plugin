package de.espend.idea.php.phpunit.utils.mockstring

import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.SmartList
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.AssignmentExpression
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpPsiElement
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.Variable
import de.espend.idea.php.phpunit.utils.ChainVisitorUtil
import de.espend.idea.php.phpunit.utils.PhpElementsUtil

class MockStringPsiUtil private constructor() {
    companion object {
        private const val PARAMETER_NOT_FOUND = -2

        @JvmStatic
        fun resolveMethod(methodReference: MethodReference): Method? {
            val resolvedCollection = methodReference.multiResolveStrict(Method::class.java)
            if (resolvedCollection.isNotEmpty()) {
                return resolvedCollection.iterator().next()
            }

            return null
        }

        @JvmStatic
        fun findMethodReference(entryPoint: MethodReference, methodName: String): MethodReference? {
            val result = arrayOfNulls<MethodReference>(1)
            ChainVisitorUtil.visit(entryPoint, object : ChainVisitorUtil.ChainProcessorInterface {
                override fun process(methodReference: MethodReference): Boolean {
                    if (methodName == methodReference.name) {
                        result[0] = methodReference
                        return false
                    }

                    return true
                }
            })

            return result[0]
        }

        @JvmStatic
        fun findClosestAssignment(variable: Variable): MethodReference? {
            val variableName = variable.name
            var cursor: PsiElement? = variable

            while (true) {
                cursor = cursor?.parent
                if (cursor == null || cursor is Method) {
                    break
                }

                if (cursor !is Statement) {
                    continue
                }

                val statements = SmartList<Statement>()
                statements.add(cursor)
                statements.addAll(PsiTreeUtil.getChildrenOfTypeAsList(cursor, Statement::class.java))

                for (statement in statements) {
                    val assignmentExpression = PsiTreeUtil.getChildOfType(statement, AssignmentExpression::class.java)
                    if (assignmentExpression == null) {
                        continue
                    }

                    val statementVariable = PsiTreeUtil.getChildOfType(assignmentExpression, Variable::class.java)
                    if (statementVariable == null) {
                        continue
                    }

                    val statementVariableName = statementVariable.name
                    if (statementVariableName == null || statementVariableName != variableName) {
                        continue
                    }

                    val methodReference = PsiTreeUtil.getChildOfType(assignmentExpression, MethodReference::class.java)
                    if (methodReference != null) {
                        return methodReference
                    }
                }
            }

            return null
        }

        @JvmStatic
        fun resolveClassFromMethodReference(methodReference: MethodReference): PhpClass? {
            val parameterList = methodReference.parameterList
            return if (parameterList == null) null else resolveClassFromParameterList(parameterList)
        }

        @JvmStatic
        fun resolveClassFromParameterList(parameterList: ParameterList): PhpClass? {
            val parameter = parameterList.getParameter(0)
            if (parameter == null) {
                return null
            }

            if (parameter is Variable) {
                return resolveClassFromVariable(parameter)
            }

            var className = PhpElementsUtil.getStringValue(parameter)
            if (className == null || className.isEmpty()) {
                return null
            }

            className = className.replace("\\\\", "\\")
            val phpClasses = PhpIndex.getInstance(parameter.project).getAnyByFQN(className)
            if (phpClasses.isNotEmpty()) {
                return phpClasses.iterator().next()
            }

            return null
        }

        @JvmStatic
        fun resolveClassFromVariable(variable: Variable): PhpClass? {
            val classFinderResult = ClassFinder.find(variable)
            return if (classFinderResult == null) null else classFinderResult.phpClass
        }

        @JvmStatic
        fun getParameterNumber(parameter: PsiElement): Int {
            val parameterList = PsiTreeUtil.getParentOfType(parameter, ParameterList::class.java)
            if (parameterList == null) {
                return PARAMETER_NOT_FOUND
            }

            val parameterIndex = PhpElementsUtil.getParameterIndex(parameterList, parameter)
            return if (parameterIndex == null) PARAMETER_NOT_FOUND else parameterIndex + 1
        }

        @JvmStatic
        fun getArrayParameterValues(parameterList: ParameterList, parameterNumber: Int): List<String>? {
            val parameter = getParameter(parameterList, parameterNumber)
            if (parameter !is ArrayCreationExpression) {
                return null
            }

            val arrayCreationExpression = parameter
            val values: MutableList<String> = ArrayList()
            var hasHashElements = false
            for (hashElement in arrayCreationExpression.hashElements) {
                hasHashElements = true
                val value = getArrayHashValue(hashElement)
                if (value != null) {
                    values.add(value)
                }
            }

            return if (hasHashElements) values else getListArrayValues(arrayCreationExpression)
        }

        private fun getArrayHashValue(hashElement: ArrayHashElement): String? {
            var value = PhpElementsUtil.getStringValue(hashElement.value)
            if (value != null) {
                return value
            }

            value = PhpElementsUtil.getStringValue(hashElement.key)
            if (value != null) {
                return value
            }

            return null
        }

        private fun getListArrayValues(arrayCreationExpression: ArrayCreationExpression): List<String> {
            val values: MutableList<String> = ArrayList()

            var child: PhpPsiElement? = arrayCreationExpression.firstPsiChild
            while (child != null) {
                if (child !is ArrayHashElement) {
                    val value = StringUtil.unquoteString(child.text)
                    if (value.isNotEmpty()) {
                        values.add(value)
                    }
                }

                child = child.nextPsiSibling
            }

            return values
        }

        private fun getParameter(parameterList: ParameterList, parameterNumber: Int): PsiElement? {
            if (parameterNumber < 1) {
                return null
            }

            val parameter = parameterList.getParameter(parameterNumber - 1)
            return if (parameter is PhpPsiElement) parameter else null
        }
    }
}
