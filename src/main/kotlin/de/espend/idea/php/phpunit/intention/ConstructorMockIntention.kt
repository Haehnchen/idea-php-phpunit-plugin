package de.espend.idea.php.phpunit.intention

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.codeInsight.intention.preview.IntentionPreviewUtils
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Iconable
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.parser.PhpElementTypes
import com.jetbrains.php.lang.psi.PhpPsiElementFactory
import com.jetbrains.php.lang.psi.PhpPsiUtil
import com.jetbrains.php.lang.psi.elements.AssignmentExpression
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.NewExpression
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.Variable
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import de.espend.idea.php.phpunit.PhpUnitIcons
import de.espend.idea.php.phpunit.utils.PhpElementsUtil
import org.apache.commons.lang3.StringUtils
import org.jetbrains.annotations.Nls
import java.util.ArrayList
import javax.swing.Icon

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
class ConstructorMockIntention : PsiElementBaseIntentionAction(), Iconable, HighPriorityAction {
    override fun invoke(project: Project, editor: Editor, psiElement: PsiElement) {
        val newExpression = getScopeForOperation(psiElement)
        if (newExpression != null) {
            val classReference = newExpression.getClassReference()
            if (classReference != null) {
                val fqn = classReference.getFQN()

                for (phpClass in PhpIndex.getInstance(project).getAnyByFQN(fqn)) {
                    val constructor = phpClass.constructor

                    // first constructor wins on non unique class names
                    if (constructor == null) {
                        continue
                    }

                    val runnable = MyConstructorCommandActionArgument(
                        psiElement,
                        PsiTreeUtil.getChildOfType(newExpression, ParameterList::class.java),
                        constructor,
                        newExpression
                    )

                    // support cross API version compatibility
                    if (IntentionPreviewUtils.isPreviewElement(psiElement)) {
                        runnable.run()
                    } else {
                        WriteCommandAction.runWriteCommandAction(
                            psiElement.project,
                            text,
                            "",
                            runnable,
                            psiElement.containingFile
                        )
                    }

                    return
                }
            }
        }
    }

    override fun isAvailable(project: Project, editor: Editor, psiElement: PsiElement): Boolean {
        val newExpression = getScopeForOperation(psiElement)
        if (newExpression == null) {
            return false
        }

        val phpClass = PsiTreeUtil.getParentOfType(psiElement, PhpClass::class.java)
        if (phpClass == null) {
            return false
        }

        return PhpElementsUtil.isInstanceOf(phpClass, "\\PHPUnit\\Framework\\TestCase") ||
            PhpElementsUtil.isInstanceOf(phpClass, "\\PHPUnit_Framework_TestCase")
    }

    @Nls
    override fun getFamilyName(): String {
        return "PHPUnit"
    }

    override fun getText(): String {
        return "PHPUnit: Add constructor mocks"
    }

    private fun getScopeForOperation(psiElement: PsiElement): NewExpression? {
        // $foo = new Foo<caret>bar();
        var newExpression = PsiTreeUtil.getParentOfType(psiElement, NewExpression::class.java)

        if (newExpression == null) {
            // scope outside method reference chaining
            // $f<caret>oo = new Foobar();
            val variable = psiElement.parent
            if (variable is Variable) {
                val assignmentExpression = variable.parent
                if (assignmentExpression is AssignmentExpression) {
                    newExpression = PsiTreeUtil.getChildOfAnyType(assignmentExpression, NewExpression::class.java)
                }
            }
        }

        return newExpression
    }

    override fun getIcon(flags: Int): Icon {
        return PhpUnitIcons.PHPUNIT
    }

    /**
     * new Foobar($this->createMock(Foobar::class))
     */
    private data class MyConstructorCommandActionArgument(
        private val scope: PsiElement,
        private val parameterList: ParameterList?,
        private val method: Method,
        private val newExpression: NewExpression
    ) : Runnable {
        override fun run() {
            // current parameter state
            val parameters: Array<PsiElement> = parameterList?.parameters ?: emptyArray()
            val length = parameters.size

            // pre insert "use imports"
            var pos = 0
            val classes = ArrayList<String?>()
            for (parameter in method.parameters) {
                val className = parameter.declaredType.toString()

                if (pos++ < length) {
                    continue
                }

                val primitiveType = PhpType.isPrimitiveType(className)
                if (primitiveType) {
                    classes.add("\\$className")
                } else {
                    // try import and get class name result; result can also be an alias
                    classes.add(PhpElementsUtil.insertUseIfNecessary(newExpression, className))
                }
            }

            val collect = classes
                .map { type ->
                    // PrimitiveType
                    if (PhpType.isPrimitiveType(type)) {
                        val s1 = "\\" + StringUtils.stripStart(type, "\\")

                        if (s1.equals("\\int", ignoreCase = true)) {
                            "-1"
                        } else if (s1.equals("\\float", ignoreCase = true)) {
                            "0.0"
                        } else if (s1.equals("\\array", ignoreCase = true)) {
                            "[]"
                        } else if (s1.equals("\\bool", ignoreCase = true) || s1.equals("\\boolean", ignoreCase = true)) {
                            "true"
                        } else {
                            // fallback
                            "'?'"
                        }
                    } else {
                        String.format("\$this->createMock(%s::class)", type)
                    }
                }

            val insert = StringUtils.join(collect, ", ")
            val argumentList = PhpPsiElementFactory.createArgumentList(scope.project, insert)

            for (parameter in argumentList.parameters) {
                appendParameterToNewExpression(newExpression, parameter)
            }

            val statement = newExpression.parent

            CodeStyleManager.getInstance(scope.project).reformatText(
                newExpression.containingFile,
                statement.textRange.startOffset,
                statement.textRange.endOffset + insert.length
            )
        }
    }

    private companion object {
        fun appendParameterToNewExpression(function: NewExpression, psiElement: PsiElement) {
            var parameterList = PhpPsiUtil.getChildOfType(function, PhpElementTypes.PARAMETER_LIST)

            if (parameterList == null) {
                val psiElement1 = function.addAfter(
                    PhpPsiElementFactory.createFromText(psiElement.project, PhpTokenTypes.chLPAREN, "new Foo()"),
                    function.lastChild
                )
                parameterList = function.addAfter(
                    PhpPsiElementFactory.createParameterList(psiElement.project, "new Foo()"),
                    psiElement1
                )
                function.addAfter(
                    PhpPsiElementFactory.createFromText(psiElement.project, PhpTokenTypes.chRPAREN, "new Foo()"),
                    parameterList
                )
            }

            val lastChild = parameterList.lastChild
            if (lastChild == null) {
                parameterList.add(psiElement)
            } else {
                parameterList.addAfter(
                    psiElement,
                    parameterList.addAfter(PhpPsiElementFactory.createComma(parameterList.project), lastChild)
                )
            }
        }
    }
}
