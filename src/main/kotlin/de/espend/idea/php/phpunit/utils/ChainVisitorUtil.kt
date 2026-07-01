package de.espend.idea.php.phpunit.utils

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.AssignmentExpression
import com.jetbrains.php.lang.psi.elements.FieldReference
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpPsiElement
import com.jetbrains.php.lang.psi.elements.Variable

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
object ChainVisitorUtil {
    fun visit(methodReference: MethodReference, processor: ChainProcessorInterface, skipCurrent: Boolean) {
        visit(if (skipCurrent) methodReference.firstChild else methodReference, processor, 10)
    }

    fun visit(methodReference: MethodReference, processor: ChainProcessorInterface) {
        visit(methodReference, processor, true)
    }

    private fun visit(psiElement: PsiElement?, processor: ChainProcessorInterface, depth: Int) {
        if (depth <= 0) {
            return
        }

        if (psiElement == null) {
            return
        }

        if (psiElement is MethodReference) {
            if (!processor.process(psiElement)) {
                return
            }

            visit(psiElement.firstChild, processor, depth - 1)
        } else if (psiElement is FieldReference) {
            val phpPsiElement = resolveField(psiElement)
            if (phpPsiElement is MethodReference) {
                if (!processor.process(phpPsiElement)) {
                    return
                }

                visit(phpPsiElement.firstChild, processor, depth - 1)
            }
        } else if (psiElement is Variable && !psiElement.isDeclaration) {
            for (phpPsiElement in resolveVariable(psiElement)) {
                if (phpPsiElement is MethodReference && !processor.process(phpPsiElement)) {
                    return
                }

                visit(phpPsiElement.firstChild, processor, depth - 1)
            }
        }
    }

    /**
     * Find variables inside a function scope, ignoring closures
     */
    private fun resolveVariable(variable: Variable): Collection<PhpPsiElement> {
        val name = variable.name

        val methodScope = getNonClosureFunction(variable)

        if (methodScope == null) {
            return emptyList()
        }

        val psiElements: MutableCollection<PhpPsiElement> = ArrayList()

        for (variable1 in PsiTreeUtil.collectElementsOfType(methodScope, Variable::class.java)) {
            if (!variable1.isDeclaration || name != variable1.name) {
                continue
            }

            val parent = variable1.parent
            if (parent !is AssignmentExpression) {
                continue
            }

            val value = parent.value
            if (value == null) {
                continue
            }

            psiElements.add(value)
        }

        return psiElements
    }

    private fun resolveField(fieldReference: FieldReference): PhpPsiElement? {
        val name = fieldReference.name
        if (name != null) {
            // find method scope, we not directly search for class as Method is our parent scope
            val methodScope = PsiTreeUtil.getStubOrPsiParentOfType(fieldReference, Method::class.java)
            if (methodScope != null) {
                val phpClass = methodScope.containingClass
                if (phpClass != null) {
                    for (setupMethod in arrayOf("setUp", "setUpBeforeTest")) {
                        val method = phpClass.findOwnMethodByName(setupMethod)

                        // "setUp" is our "constructor" for test classes
                        if (method != null) {
                            for (assignmentExpression in PsiTreeUtil.collectElementsOfType(method, AssignmentExpression::class.java)) {
                                val variable = assignmentExpression.variable

                                // remember or field name and attach is for a later resolve
                                if (variable is FieldReference && name == variable.name) {
                                    return assignmentExpression.value
                                }
                            }
                        }
                    }
                }
            }
        }

        return null
    }

    private fun getNonClosureFunction(psiElement: PsiElement): Function? {
        var parent = PsiTreeUtil.getStubOrPsiParent(psiElement)
        while (parent != null) {
            if (parent is Function && !parent.isClosure) {
                return parent
            }

            if (parent is PsiFile) {
                return null
            }

            parent = PsiTreeUtil.getStubOrPsiParent(parent)
        }

        return null
    }

    interface ChainProcessorInterface {
        fun process(methodReference: MethodReference): Boolean
    }
}
