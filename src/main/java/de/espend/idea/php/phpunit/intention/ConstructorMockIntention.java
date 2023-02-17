package de.espend.idea.php.phpunit.intention;

import com.intellij.codeInsight.intention.HighPriorityAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.PhpPsiUtil;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import de.espend.idea.php.phpunit.PhpUnitIcons;
import de.espend.idea.php.phpunit.utils.PhpElementsUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ConstructorMockIntention extends PsiElementBaseIntentionAction implements Iconable, HighPriorityAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) throws IncorrectOperationException {
        NewExpression newExpression = getScopeForOperation(psiElement);
        if(newExpression != null) {
            ClassReference classReference = newExpression.getClassReference();
            if (classReference != null) {
                String fqn = classReference.getFQN();

                for (PhpClass phpClass : PhpIndex.getInstance(project).getAnyByFQN(fqn)) {
                    Method constructor = phpClass.getConstructor();

                    // first constructor wins on non unique class names
                    if(constructor == null) {
                        continue;
                    }

                    WriteCommandAction.runWriteCommandAction(
                        psiElement.getProject(),
                        getText(),
                        "",
                        new MyConstructorCommandActionArgument(
                            psiElement,
                            PsiTreeUtil.getChildOfType(newExpression, ParameterList.class),
                            constructor,
                            newExpression
                        ),
                        psiElement.getContainingFile()
                    );

                    return;
                }
            }
        }
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) {
        NewExpression newExpression = getScopeForOperation(psiElement);
        if(newExpression == null) {
            return false;
        }

        PhpClass phpClass = PsiTreeUtil.getParentOfType(psiElement, PhpClass.class);
        if(phpClass == null) {
            return false;
        }

        return PhpElementsUtil.isInstanceOf(phpClass, "\\PHPUnit\\Framework\\TestCase")
            || PhpElementsUtil.isInstanceOf(phpClass, "\\PHPUnit_Framework_TestCase");
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "PHPUnit";
    }

    @NotNull
    @Override
    public String getText() {
        return "PHPUnit: Add constructor mocks";
    }

    @Nullable
    private NewExpression getScopeForOperation(@NotNull PsiElement psiElement) {
        // $foo = new Foo<caret>bar();
        NewExpression newExpression = PsiTreeUtil.getParentOfType(psiElement, NewExpression.class);

        if(newExpression == null) {
            // scope outside method reference chaining
            // $f<caret>oo = new Foobar();
            PsiElement variable = psiElement.getParent();
            if(variable instanceof Variable) {
                PsiElement assignmentExpression = variable.getParent();
                if(assignmentExpression instanceof AssignmentExpression) {
                    newExpression = PsiTreeUtil.getChildOfAnyType(assignmentExpression, NewExpression.class);
                }
            }
        }

        return newExpression;
    }

    @Override
    public Icon getIcon(int flags) {
        return PhpUnitIcons.PHPUNIT;
    }

    /**
     * new Foobar($this->createMock(Foobar::class))
     */
    private static class MyConstructorCommandActionArgument implements Runnable {
        @NotNull
        private final PsiElement scope;

        @Nullable
        private final ParameterList parameterList;

        @NotNull
        private final Method method;

        @NotNull
        private final NewExpression newExpression;

        private MyConstructorCommandActionArgument(@NotNull PsiElement scope, @Nullable ParameterList parameterList, @NotNull Method method, @NotNull NewExpression newExpression) {
            this.scope = scope;
            this.parameterList = parameterList;
            this.method = method;
            this.newExpression = newExpression;
        }

        @Override
        public void run() {
            // current parameter state
            PsiElement[] parameters = parameterList != null ? parameterList.getParameters() : new PsiElement[0];
            int length = parameters.length;

            // pre insert "use imports"
            int pos = 0;
            List<String> classes = new ArrayList<>();
            for (Parameter parameter : method.getParameters()) {
                String className = parameter.getDeclaredType().toString();

                if(pos++ < length) {
                    continue;
                }

                boolean primitiveType = PhpType.isPrimitiveType(className);
                if(primitiveType) {
                    classes.add("\\" + className);
                } else {
                    // try import and get class name result; result can also be an alias
                    classes.add(PhpElementsUtil.insertUseIfNecessary(newExpression, className));
                }
            }

            List<String> collect = classes
                .stream()
                .map(type -> {
                    // PrimitiveType
                    if(PhpType.isPrimitiveType(type)) {
                        String s1 = "\\" + StringUtils.stripStart(type, "\\");

                        if(s1.equalsIgnoreCase("\\int")) {
                            return "-1";
                        } else if(s1.equalsIgnoreCase("\\float")) {
                            return "0.0";
                        } else if(s1.equalsIgnoreCase("\\array")) {
                            return "[]";
                        } else if(s1.equalsIgnoreCase("\\bool") || s1.equalsIgnoreCase("\\boolean")) {
                            return "true";
                        }

                        // fallback
                        return "'?'";
                    }

                    return String.format("$this->createMock(%s::class)", type);
                })
                .collect(Collectors.toList());

            String insert = StringUtils.join(collect, ", ");
            ParameterList argumentList = PhpPsiElementFactory.createArgumentList(scope.getProject(), insert);

            for (PsiElement parameter : argumentList.getParameters()) {
                appendParameterToNewExpression(newExpression, parameter);
            }

            PsiElement statement = newExpression.getParent();

            CodeStyleManager.getInstance(scope.getProject()).reformatText(
                newExpression.getContainingFile(),
                statement.getTextRange().getStartOffset(),
                statement.getTextRange().getEndOffset() + insert.length()
            );
        }
    }

    private static void appendParameterToNewExpression(@NotNull NewExpression function, @NotNull PsiElement psiElement) {
        PsiElement parameterList = PhpPsiUtil.getChildOfType(function, PhpElementTypes.PARAMETER_LIST);

        if (parameterList == null) {
            PsiElement psiElement1 = function.addAfter(PhpPsiElementFactory.createFromText(psiElement.getProject(), PhpTokenTypes.chLPAREN, "new Foo()"), function.getLastChild());
            parameterList = function.addAfter(PhpPsiElementFactory.createParameterList(psiElement.getProject(), "new Foo()"), psiElement1);
            function.addAfter(PhpPsiElementFactory.createFromText(psiElement.getProject(), PhpTokenTypes.chRPAREN, "new Foo()"), parameterList);
        }

        PsiElement lastChild = parameterList.getLastChild();
        if (lastChild == null) {
            parameterList.add(psiElement);
        } else {
            parameterList.addAfter(psiElement, parameterList.addAfter(PhpPsiElementFactory.createComma(parameterList.getProject()), lastChild));
        }
    }
}
