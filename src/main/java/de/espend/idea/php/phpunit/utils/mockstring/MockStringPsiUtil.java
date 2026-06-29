package de.espend.idea.php.phpunit.utils.mockstring;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.SmartList;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import de.espend.idea.php.phpunit.utils.mockstring.ClassFinder.Result;
import de.espend.idea.php.phpunit.utils.ChainVisitorUtil;
import de.espend.idea.php.phpunit.utils.PhpElementsUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class MockStringPsiUtil {

    private static final int PARAMETER_NOT_FOUND = -2;

    private MockStringPsiUtil() {
    }

    @Nullable
    public static Method resolveMethod(@NotNull MethodReference methodReference) {
        Collection<Method> resolvedCollection = methodReference.multiResolveStrict(Method.class);
        if (!resolvedCollection.isEmpty()) {
            return resolvedCollection.iterator().next();
        }

        return null;
    }

    @Nullable
    public static MethodReference findMethodReference(@NotNull MethodReference entryPoint, @NotNull String methodName) {
        MethodReference[] result = new MethodReference[1];
        ChainVisitorUtil.visit(entryPoint, methodReference -> {
            if (methodName.equals(methodReference.getName())) {
                result[0] = methodReference;
                return false;
            }

            return true;
        });

        return result[0];
    }

    @Nullable
    public static MethodReference findClosestAssignment(@NotNull Variable variable) {
        String variableName = variable.getName();
        PsiElement cursor = variable;

        while (true) {
            cursor = cursor.getParent();
            if (cursor == null || cursor instanceof Method) {
                break;
            }

            if (!(cursor instanceof Statement)) {
                continue;
            }

            SmartList<Statement> statements = new SmartList<>();
            statements.add((Statement) cursor);
            statements.addAll(PsiTreeUtil.getChildrenOfTypeAsList(cursor, Statement.class));

            for (Statement statement : statements) {
                AssignmentExpression assignmentExpression = PsiTreeUtil.getChildOfType(statement, AssignmentExpression.class);
                if (assignmentExpression == null) {
                    continue;
                }

                Variable statementVariable = PsiTreeUtil.getChildOfType(assignmentExpression, Variable.class);
                if (statementVariable == null) {
                    continue;
                }

                String statementVariableName = statementVariable.getName();
                if (statementVariableName == null || !statementVariableName.equals(variableName)) {
                    continue;
                }

                MethodReference methodReference = PsiTreeUtil.getChildOfType(assignmentExpression, MethodReference.class);
                if (methodReference != null) {
                    return methodReference;
                }
            }
        }

        return null;
    }

    @Nullable
    public static PhpClass resolveClassFromMethodReference(@NotNull MethodReference methodReference) {
        ParameterList parameterList = methodReference.getParameterList();
        return parameterList == null ? null : resolveClassFromParameterList(parameterList);
    }

    @Nullable
    public static PhpClass resolveClassFromParameterList(@NotNull ParameterList parameterList) {
        PsiElement parameter = parameterList.getParameter(0);
        if (parameter == null) {
            return null;
        }

        if (parameter instanceof Variable) {
            return resolveClassFromVariable((Variable) parameter);
        }

        String className = PhpElementsUtil.getStringValue(parameter);
        if (className == null || className.isEmpty()) {
            return null;
        }

        className = className.replace("\\\\", "\\");
        Collection<PhpClass> phpClasses = PhpIndex.getInstance(parameter.getProject()).getAnyByFQN(className);
        if (!phpClasses.isEmpty()) {
            return phpClasses.iterator().next();
        }

        return null;
    }

    @Nullable
    public static PhpClass resolveClassFromVariable(@NotNull Variable variable) {
        Result classFinderResult = ClassFinder.find(variable);
        return classFinderResult == null ? null : classFinderResult.getPhpClass();
    }

    public static int getParameterNumber(@NotNull PsiElement parameter) {
        ParameterList parameterList = PsiTreeUtil.getParentOfType(parameter, ParameterList.class);
        if (parameterList == null) {
            return PARAMETER_NOT_FOUND;
        }

        Integer parameterIndex = PhpElementsUtil.getParameterIndex(parameterList, parameter);
        return parameterIndex == null ? PARAMETER_NOT_FOUND : parameterIndex + 1;
    }

    @Nullable
    public static List<String> getArrayParameterValues(@NotNull ParameterList parameterList, int parameterNumber) {
        PsiElement parameter = getParameter(parameterList, parameterNumber);
        if (!(parameter instanceof ArrayCreationExpression)) {
            return null;
        }

        ArrayCreationExpression arrayCreationExpression = (ArrayCreationExpression) parameter;
        List<String> values = new ArrayList<>();
        boolean hasHashElements = false;
        for (ArrayHashElement hashElement : arrayCreationExpression.getHashElements()) {
            hasHashElements = true;
            String value = getArrayHashValue(hashElement);
            if (value != null) {
                values.add(value);
            }
        }

        return hasHashElements ? values : getListArrayValues(arrayCreationExpression);
    }

    @Nullable
    private static String getArrayHashValue(@NotNull ArrayHashElement hashElement) {
        String value = PhpElementsUtil.getStringValue(hashElement.getValue());
        if (value != null) {
            return value;
        }

        value = PhpElementsUtil.getStringValue(hashElement.getKey());
        if (value != null) {
            return value;
        }

        return null;
    }

    @NotNull
    private static List<String> getListArrayValues(@NotNull ArrayCreationExpression arrayCreationExpression) {
        List<String> values = new ArrayList<>();

        PhpPsiElement child = arrayCreationExpression.getFirstPsiChild();
        while (child != null) {
            if (!(child instanceof ArrayHashElement)) {
                String value = StringUtil.unquoteString(child.getText());
                if (!value.isEmpty()) {
                    values.add(value);
                }
            }

            child = child.getNextPsiSibling();
        }

        return values;
    }

    @Nullable
    private static PsiElement getParameter(@NotNull ParameterList parameterList, int parameterNumber) {
        if (parameterNumber < 1) {
            return null;
        }

        PsiElement parameter = parameterList.getParameter(parameterNumber - 1);
        return parameter instanceof PhpPsiElement ? parameter : null;
    }
}
