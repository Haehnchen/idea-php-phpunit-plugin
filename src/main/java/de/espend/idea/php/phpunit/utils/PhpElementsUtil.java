package de.espend.idea.php.phpunit.utils;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.codeInsight.PhpCodeInsightUtil;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.refactoring.PhpAliasImporter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpElementsUtil {
    @Nullable
    public static String getStringValue(@Nullable PsiElement psiElement) {
        return getStringValue(psiElement, 0);
    }

    /**
     * <code>Foo::class</code> to its class fqn include namespace
     */
    public static String getClassConstantPhpFqn(@NotNull ClassConstantReference classConstant) {
        PhpExpression classReference = classConstant.getClassReference();
        if(!(classReference instanceof PhpReference)) {
            return null;
        }

        String typeName = ((PhpReference) classReference).getFQN();
        return StringUtils.isNotBlank(typeName) ? StringUtils.stripStart(typeName, "\\") : null;
    }

    /**
     * <code>new Foo</code> to its class fqn include namespace
     */
    public static String getNewExpressionPhpFqn(@NotNull NewExpression newExpression) {
        PhpReference classReference = newExpression.getClassReference();
        if(classReference == null) {
            return null;
        }

        String typeName = classReference.getFQN();
        return StringUtils.isNotBlank(typeName) ? StringUtils.stripStart(typeName, "\\") : null;
    }

    @Nullable
    private static String getStringValue(@Nullable PsiElement psiElement, int depth) {
        if(psiElement == null || ++depth > 5) {
            return null;
        }

        if(psiElement instanceof StringLiteralExpression) {
            String resolvedString = ((StringLiteralExpression) psiElement).getContents();
            if(StringUtils.isEmpty(resolvedString)) {
                return null;
            }

            return resolvedString;
        } else if(psiElement instanceof Field) {
            return getStringValue(((Field) psiElement).getDefaultValue(), depth);
        } else if(psiElement instanceof ClassConstantReference && "class".equals(((ClassConstantReference) psiElement).getName())) {
            // Foobar::class
            return getClassConstantPhpFqn((ClassConstantReference) psiElement);
        } else if(psiElement instanceof PhpReference) {
            PsiReference psiReference = psiElement.getReference();
            if(psiReference == null) {
                return null;
            }

            PsiElement ref = psiReference.resolve();
            if(ref instanceof PhpReference) {
                return getStringValue(psiElement, depth);
            }

            if(ref instanceof Field) {
                return getStringValue(((Field) ref).getDefaultValue());
            }
        } else if(psiElement instanceof NewExpression){
            return getNewExpressionPhpFqn((NewExpression) psiElement);
        } else if(psiElement instanceof  ConcatenationExpression){
            // Allows creation method like: Mockery::mock(Dependency::class . "[calledMethod]");

            ConcatenationExpression concatenationExpression = (ConcatenationExpression) psiElement;
            StringBuilder concatString = new StringBuilder();

            for(PsiElement e : concatenationExpression.getChildren()){
                concatString.append(getStringValue(e, depth));
            }
            return concatString.toString();
        }

        return null;
    }

    /**
     * @param subjectClass eg DateTime
     * @param expectedClass eg DateTimeInterface
     */
    public static boolean isInstanceOf(@NotNull PhpClass subjectClass, @NotNull String expectedClass) {
        return new PhpType().add(expectedClass).isConvertibleFrom(new PhpType().add(subjectClass), PhpIndex.getInstance(subjectClass.getProject()));
    }

    /**
     * Resolves MethodReference and compare containing class against implementations instances
     */
    public static boolean isMethodReferenceInstanceOf(@NotNull MethodReference methodReference, @NotNull String ...expectedClassNameAsOr) {
        for (ResolveResult resolveResult : methodReference.multiResolve(false)) {
            PsiElement resolve = resolveResult.getElement();

            if(!(resolve instanceof Method)) {
                continue;
            }

            PhpClass containingClass = ((Method) resolve).getContainingClass();
            if(containingClass == null) {
                continue;
            }

            for (String expectedClassName : expectedClassNameAsOr) {
                if (PhpElementsUtil.isInstanceOf(containingClass, expectedClassName)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Try to match the class instance of the method references without calling any phpindex to be usable in index process
     *
     * Note: only local file is taken so only direct instances are detected
     */
    public static boolean isLocalResolveMethodReferenceInstanceOf(@NotNull MethodReference methodReference, @NotNull String ...expectedClassNameAsOr) {
        PhpExpression classReference = methodReference.getClassReference();
        if (classReference != null) {
            for (String type : classReference.getType().getTypes()) {
                // check the class name based on the type; also normalize any slashes
                String typeFqn = "\\" + StringUtils.stripStart(type, "\\");
                for (String expected : expectedClassNameAsOr) {
                    String expectedFqn = "\\" + StringUtils.stripStart(expected, "\\");
                    if (expectedFqn.equalsIgnoreCase(typeFqn)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Get the position of the given element to its parent ParameterList
     */
    @Nullable
    public static Integer getParameterIndex(@NotNull ParameterList parameterList, @NotNull PsiElement parameter) {
        PsiElement[] parameters = parameterList.getParameters();

        int i;
        for(i = 0; i < parameters.length; i = i + 1) {
            if(parameters[i].equals(parameter)) {
                return i;
            }
        }

        return null;
    }

    /**
     * Resolves MethodReference and compare containing class against implementations instances
     */
    public static boolean isMethodReferenceInstanceOf(@NotNull MethodReference methodReference, @NotNull String expectedClassName, @NotNull String methodName) {
        if(!methodName.equals(methodReference.getName())) {
            return false;
        }

        return isMethodReferenceInstanceOf(methodReference, expectedClassName);
    }

    @Nullable
    public static String insertUseIfNecessary(@NotNull PsiElement scope, @NotNull String fqnClasName) {
        if(!fqnClasName.startsWith("\\")) {
            fqnClasName = "\\" + fqnClasName;
        }

        PhpPsiElement scopeForUseOperator = PhpCodeInsightUtil.findScopeForUseOperator(scope);
        if(scopeForUseOperator == null) {
            return null;
        }

        if(!PhpCodeInsightUtil.getAliasesInScope(scopeForUseOperator).containsValue(fqnClasName)) {
            PhpAliasImporter.insertUseStatement(fqnClasName, scopeForUseOperator);
        }

        for (Map.Entry<String, String> entry : PhpCodeInsightUtil.getAliasesInScope(scopeForUseOperator).entrySet()) {
            if(fqnClasName.equals(entry.getValue())) {
                return entry.getKey();
            }
        }

        return null;
    }

    /**
     * class "Foo" extends
     */
    static public PsiElementPattern.Capture<PsiElement> getClassNamePattern() {
        return PlatformPatterns
            .psiElement(PhpTokenTypes.IDENTIFIER)
            .afterLeafSkipping(
                PlatformPatterns.psiElement(PsiWhiteSpace.class),
                PlatformPatterns.psiElement(PhpTokenTypes.kwCLASS)
            )
            .withParent(PhpClass.class)
            .withLanguage(PhpLanguage.INSTANCE);
    }
}
