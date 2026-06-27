package de.espend.idea.php.phpunit.utils;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ChainVisitorUtil {
    public static void visit(@NotNull MethodReference methodReference, @NotNull ChainProcessorInterface processor, boolean skipCurrent) {
        visit(skipCurrent ? methodReference.getFirstChild() : methodReference, processor, 10);
    }

    public static void visit(@NotNull MethodReference methodReference, @NotNull ChainProcessorInterface processor) {
        visit(methodReference, processor, true);
    }

    private static void visit(@Nullable PsiElement psiElement, @NotNull ChainProcessorInterface processor, int depth) {
        if(depth <= 0) {
            return;
        }

        if(psiElement == null) {
            return;
        }

        if(psiElement instanceof MethodReference) {
            if(!processor.process((MethodReference) psiElement)) {
                return;
            }

            visit(psiElement.getFirstChild(), processor, --depth);
        } else if(psiElement instanceof FieldReference) {
            PhpPsiElement phpPsiElement = resolveField((FieldReference) psiElement);
            if(phpPsiElement instanceof MethodReference) {
                if(!processor.process((MethodReference) phpPsiElement)) {
                    return;
                }

                visit(phpPsiElement.getFirstChild(), processor, --depth);
            }
        } else if(psiElement instanceof Variable && !((Variable) psiElement).isDeclaration()) {
            for (PhpPsiElement phpPsiElement : resolveVariable((Variable) psiElement)) {
                if(phpPsiElement instanceof MethodReference && !processor.process((MethodReference) phpPsiElement)) {
                    return;
                }

                visit(phpPsiElement.getFirstChild(), processor, --depth);
            }
        }
    }

    /**
     * Find variables inside a function scope, ignoring closures
     */
    @NotNull
    private static Collection<PhpPsiElement> resolveVariable(@NotNull Variable variable) {
        String name = variable.getName();

        Function methodScope = getNonClosureFunction(variable);

        if(methodScope == null) {
            return Collections.emptyList();
        }

        Collection<PhpPsiElement> psiElements = new ArrayList<>();

        for (Variable variable1 : PsiTreeUtil.collectElementsOfType(methodScope, Variable.class)) {
            if(!variable1.isDeclaration() || !name.equals(variable1.getName())) {
                continue;
            }

            PsiElement parent = variable1.getParent();
            if(!(parent instanceof AssignmentExpression)) {
                continue;
            }

            PhpPsiElement value = ((AssignmentExpression) parent).getValue();
            if(value == null) {
                continue;
            }

            psiElements.add(value);
        }

        return psiElements;
    }

    @Nullable
    private static PhpPsiElement resolveField(@NotNull FieldReference fieldReference) {
        String name = fieldReference.getName();
        if(name != null) {
            // find method scope, we not directly search for class as Method is our parent scope
            Method methodScope = PsiTreeUtil.getStubOrPsiParentOfType(fieldReference, Method.class);
            if(methodScope != null) {
                PhpClass phpClass = methodScope.getContainingClass();
                if(phpClass != null) {
                    for (String setupMethod : new String[]{"setUp", "setUpBeforeTest"}) {
                        Method method = phpClass.findOwnMethodByName(setupMethod);

                        // "setUp" is our "constructor" for test classes
                        if(method != null) {
                            for (AssignmentExpression assignmentExpression : PsiTreeUtil.collectElementsOfType(method, AssignmentExpression.class)) {
                                PhpPsiElement variable = assignmentExpression.getVariable();

                                // remember or field name and attach is for a later resolve
                                if(variable instanceof FieldReference && name.equals(variable.getName())) {
                                    return assignmentExpression.getValue();
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    private static Function getNonClosureFunction(@NotNull PsiElement psiElement) {
        PsiElement parent = PsiTreeUtil.getStubOrPsiParent(psiElement);
        while (parent != null) {
            if (parent instanceof Function && !((Function) parent).isClosure()) {
                return (Function) parent;
            }

            if (parent instanceof PsiFile) {
                return null;
            }

            parent = PsiTreeUtil.getStubOrPsiParent(parent);
        }

        return null;
    }

    public interface ChainProcessorInterface {
        boolean process(@NotNull MethodReference methodReference);
    }
}
