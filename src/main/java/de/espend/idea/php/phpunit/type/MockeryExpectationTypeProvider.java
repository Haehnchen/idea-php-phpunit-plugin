package de.espend.idea.php.phpunit.type;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4;
import de.espend.idea.php.phpunit.type.utils.PhpTypeProviderUtil;
import de.espend.idea.php.phpunit.utils.PhpUnitPluginUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

public class MockeryExpectationTypeProvider implements PhpTypeProvider4 {
    public static final char TRIM_KEY = '\u1644';
    public static final char TYPE_KEY = '\u1645';

    @Override
    public char getKey() {
        return TYPE_KEY;
    }

    public static char getTrimKey() {
        return TRIM_KEY;
    }

    @Nullable
    @Override
    public PhpType getType(PsiElement element) {
        if (element instanceof MethodReference) {
            Method method = PsiTreeUtil.getStubOrPsiParentOfType(element, Method.class);

            if (method == null) {
                return null;
            }

            PhpClass containingClass = method.getContainingClass();

            // filter phpunit test methods
            if (containingClass != null && PhpUnitPluginUtil.isTestClassWithoutIndexAccess(containingClass)) {

                // Need to check if child element type is expecting a mocked method now
                PsiElement childElem = element.getFirstChild();
                String[] childTypes = ((PhpTypedElement) childElem).getType().getTypes().toArray(new String[0]);

                for (String type : childTypes) {
                    // This is added by MockeryMethodNameTypeProvider to a new syntax method reference
                    // e.g. $mock->allows()
                    if (type.endsWith(TRIM_KEY + "ExpectingMockedMethod")) {
                        return new PhpType().add("#" + this.getKey() + "#K#C\\Mockery\\Expectation.class" + TRIM_KEY
                                + ((MethodReference) element).getName());
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    @Override
    public PhpType complete(String s, Project project) {
        return null;
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String expression, Set<String> visited, int depth, Project project) {
        String[] split = expression.split(String.valueOf(TRIM_KEY));
        if (split.length != 2) {
            return null;
        }

        PhpIndex phpIndex = PhpIndex.getInstance(project);
        String resolvedParameter = PhpTypeProviderUtil.getResolvedParameter(phpIndex, split[0]);

        if (resolvedParameter != null && resolvedParameter.equals("Mockery\\Expectation")) {
            return phpIndex.getAnyByFQN("\\Mockery\\Expectation");
        }

        return null;
    }
}
