package de.espend.idea.php.phpunit.utils

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.PhpPsiElementFactory
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.impl.PhpPsiElementImpl

object MockeryPsiRefactoringUtil {
    private fun getMethodSequence(startingMethod: MethodReference): Array<MethodReference> {
        val methodSequence = ArrayList<MethodReference>()
        methodSequence.add(startingMethod)
        var current = startingMethod

        while (current.parent is MethodReference) {
            current = current.parent as MethodReference
            methodSequence.add(current)
        }

        return methodSequence.toTypedArray()
    }

    private fun getBaseMethodInSequence(startingMethod: MethodReference): MethodReference {
        var current = startingMethod
        while (current.firstChild != null && current.firstChild is MethodReference) {
            current = current.firstChild as MethodReference
        }
        return current
    }

    /**
     * @return True if once() is found
     */
    @JvmStatic
    fun checkForOnceInMethodSequence(startingMethod: MethodReference): Boolean {
        return checkForOnceInMethodSequence(getMethodSequence(startingMethod))
    }

    /**
     * @return True if once() is found
     */
    private fun checkForOnceInMethodSequence(methods: Array<MethodReference>): Boolean {
        for (method in methods) {
            if (PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery\\Expectation", "once") ||
                PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery_Expectation", "once")
            ) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun checkForCountInMethodSequence(startingMethod: MethodReference): Boolean {
        return checkForCountInMethodSequence(getMethodSequence(startingMethod))
    }

    private fun checkForCountInMethodSequence(methods: Array<MethodReference>): Boolean {
        for (method in methods) {
            if (PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery\\Expectation", "twice") ||
                PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery_Expectation", "twice") ||
                PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery\\Expectation", "times") ||
                PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery_Expectation", "times") ||
                PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery\\Expectation", "between") ||
                PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery_Expectation", "between")
            ) {
                return true
            }
        }
        return false
    }

    @Suppress("FunctionName")
    private fun ReplaceIdentifier(project: Project, element: PsiElement, newIdentifier: String) {
        element.replace(
            PhpPsiElementFactory.createStatement(project, newIdentifier).firstChild.firstChild
        )
    }

    private fun getMethodIdentifier(methodReference: MethodReference): PsiElement? {
        return if (methodReference.nameNode != null) methodReference.nameNode!!.psi else null
    }

    @JvmStatic
    fun convertMultipleParametersToArrayParameter(project: Project, baseMethod: MethodReference): MethodReference? {
        var currentBaseMethod = baseMethod
        if (currentBaseMethod.parameterList == null) {
            return null
        }

        // Check if there is a return
        var args: String? = null

        for (method in getMethodSequence(currentBaseMethod)) {
            if (PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery_Expectation", "andReturn") ||
                PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery\\Expectation", "andReturn") ||
                PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery_Expectation", "andReturns") ||
                PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery\\Expectation", "andReturns")
            ) {
                args = if (method.parameterList != null) method.parameterList!!.text else null
                // Now remove the andReturn(s). This requires finding the baseMethod again.
                currentBaseMethod = getBaseMethodInSequence(method.replace(method.firstChild) as MethodReference)
                break
            }
        }

        val parametersAsString: String = if (args != null) {
            currentBaseMethod.parameters
                .map(PsiElement::getText)
                .joinToString(", ") { s -> "$s => $args" }
        } else {
            currentBaseMethod.parameters
                .map(PsiElement::getText)
                .joinToString(", ")
        }

        val statement = PhpPsiElementFactory.createStatement(
            project,
            String.format("[%s]", parametersAsString)
        )

        if (currentBaseMethod.parameterList != null) {
            val parameterList: ParameterList = currentBaseMethod.parameterList!!
            parameterList.deleteChildRange(
                parameterList.firstChild, parameterList.lastChild
            )
            parameterList.add(statement.firstChild)
        }

        return currentBaseMethod
    }

    @JvmStatic
    fun replaceShouldReceiveFromMethodReference(
        project: Project,
        method: MethodReference,
        type: String,
        useFunctionNotation: Boolean,
        useMultipleStatements: Boolean
    ) {
        if (useFunctionNotation) {
            // Then must use multiple statements
            // For simplicity we treat all function notation conversions as if they were multiple methods
            replaceShouldReceiveFromMethodReferenceMultipleStatements(project, method, type, true)
        } else {
            val hasMultipleParameters = method.parameterList != null && method.parameters.isNotEmpty() &&
                (method.parameters.size > 1 || method.getParameter(0) is ArrayCreationExpression)

            if (useMultipleStatements && hasMultipleParameters) {
                // Will convert to multiple standard statements
                replaceShouldReceiveFromMethodReferenceMultipleStatements(project, method, type, false)
            } else {
                // Will convert to Array form if there are multiple method parameters,
                // but if not has the advantage of leaving method reference order closer to original
                StandardReplacement(project, method, type)
            }
        }
    }

    private fun replaceShouldReceiveFromMethodReferenceMultipleStatements(
        project: Project,
        methodReference: MethodReference,
        type: String,
        functionNotation: Boolean
    ) {
        var currentMethodReference = methodReference
        if (currentMethodReference.getParameter(0) !is ArrayCreationExpression) {
            currentMethodReference = convertMultipleParametersToArrayParameter(project, currentMethodReference)!!
        }

        val methodReferenceSequence = getMethodSequence(currentMethodReference)
        var argument = ""

        for (method in methodReferenceSequence) {
            val methodIdentifier = getMethodIdentifier(method)
            if (methodIdentifier == null) {
                continue
            }

            // We only remove with statement if targeting the function form
            if (functionNotation && (PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery_Expectation", "with") ||
                    PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery\\Expectation", "with"))
            ) {
                if (method.parameterList != null) {
                    argument = method.parameterList!!.text
                }
                val newElement = method.replace(method.firstChild)
                currentMethodReference = getBaseMethodInSequence(newElement as MethodReference)
            }
            if (PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery_Expectation", "once") ||
                PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery\\Expectation", "once")
            ) {
                val newElement = method.replace(method.firstChild)
                // need to recover parent from newElement
                currentMethodReference = getBaseMethodInSequence(newElement as MethodReference)
            }
            if (PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery_Expectation", "andReturn") ||
                PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery\\Expectation", "andReturn")
            ) {
                ReplaceIdentifier(project, methodIdentifier, "andReturns")
            }
            if (PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery_LegacyMockInterface", "shouldReceive") ||
                PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery\\LegacyMockInterface", "shouldReceive")
            ) {
                ReplaceIdentifier(project, methodIdentifier, type)
            }
        }

        val statementElement = PsiTreeUtil.getParentOfType(currentMethodReference, Statement::class.java)

        if (statementElement == null) {
            return
        }

        val statementElementRootParent = statementElement.parent
        val methodPrefix = currentMethodReference.firstChild.text + "->"
        val statementText = statementElement.text
        val shouldReceiveMethodText = currentMethodReference.text
        val methodSuffix = statementText.replace(shouldReceiveMethodText, "")

        if (currentMethodReference.parameterList == null) {
            return
        }

        val arrayElements = currentMethodReference.parameterList!!.firstChild.children
        for (arrayElement in arrayElements) {
            val newStatement: PsiElement
            if (arrayElement is ArrayHashElement) {
                var key = if (arrayElement.key != null) arrayElement.key!!.text else ""
                val value = if (arrayElement.value != null) arrayElement.value!!.text else ""

                if (functionNotation) {
                    key = key.replace(Regex("['\"]"), "")
                    newStatement = PhpPsiElementFactory.createStatement(
                        project,
                        methodPrefix + type + "()->" + key + "(" + argument + ")->andReturns(" + value + ")" + methodSuffix
                    )
                } else {
                    newStatement = PhpPsiElementFactory.createStatement(
                        project,
                        methodPrefix + type + "(" + key + ")->andReturns(" + value + ")" + methodSuffix
                    )
                }

                statementElementRootParent.addBefore(newStatement, statementElement)
            } else if (arrayElement is PhpPsiElementImpl<*>) {
                if (functionNotation) {
                    newStatement = PhpPsiElementFactory.createStatement(
                        project,
                        methodPrefix + type + "()->" + arrayElement.text.replace(Regex("['\"]"), "") + "(" + argument + ")" + methodSuffix
                    )
                } else {
                    newStatement = PhpPsiElementFactory.createStatement(
                        project,
                        methodPrefix + type + "(" + arrayElement.text + ")" + methodSuffix
                    )
                }

                statementElementRootParent.addBefore(newStatement, statementElement)
            }
        }
        statementElement.delete()
    }

    @JvmStatic
    fun replaceShouldNotReceive(
        project: Project,
        method: MethodReference,
        type: String,
        useFunctionNotation: Boolean,
        useMultipleStatements: Boolean
    ) {
        val statementElement = PsiTreeUtil.getParentOfType(method, Statement::class.java)

        if (statementElement == null) {
            return
        }

        val statementElementRootParent = statementElement.parent

        val statementText = statementElement.text
        val newStatementText = statementText.replace("shouldNotReceive", "shouldReceive")
            .replace(";", "->never();")

        var newStatement: PsiElement = PhpPsiElementFactory.createStatement(project, newStatementText)
        newStatement = statementElementRootParent.addBefore(newStatement, statementElement)
        statementElement.delete()

        // Now can do normal conversion on the shouldReceive
        val baseMethod = getBaseMethodInSequence(newStatement.firstChild as MethodReference)

        replaceShouldReceiveFromMethodReference(project, baseMethod, type, useFunctionNotation, useMultipleStatements)
    }

    @Suppress("FunctionName")
    private fun StandardReplacement(project: Project, methodReference: MethodReference, type: String) {
        var currentMethodReference = methodReference

        if (currentMethodReference.parameterList != null && currentMethodReference.parameters.size > 1) {
            currentMethodReference = convertMultipleParametersToArrayParameter(project, currentMethodReference)!!
        }
        val methodReferenceSequence = getMethodSequence(currentMethodReference)

        for (method in methodReferenceSequence) {
            val methodIdentifier = getMethodIdentifier(method)
            if (methodIdentifier == null) {
                continue
            }

            if (PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery_Expectation", "once") ||
                PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery\\Expectation", "once")
            ) {
                method.replace(method.firstChild)
            }

            if (PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery_Expectation", "andReturn") ||
                PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery\\Expectation", "andReturn")
            ) {
                ReplaceIdentifier(project, methodIdentifier, "andReturns")
            }

            if (PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery_LegacyMockInterface", "shouldReceive") ||
                PhpElementsUtil.isMethodReferenceInstanceOf(method, "Mockery\\LegacyMockInterface", "shouldReceive")
            ) {
                ReplaceIdentifier(project, methodIdentifier, type)
            }
        }
    }
}
