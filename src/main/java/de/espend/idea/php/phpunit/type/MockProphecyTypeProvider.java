package de.espend.idea.php.phpunit.type;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4;
import de.espend.idea.php.phpunit.type.utils.PhpTypeProviderUtil;
import de.espend.idea.php.phpunit.utils.PhpElementsUtil;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MockProphecyTypeProvider implements PhpTypeProvider4 {
    public static final char CHAR = '\u5143';
    public static char TRIM_KEY = '\u0192';

    private static final Collection<String> PHPUNIT_CLASSES = new HashSet<>() {{
        add("\\PHPUnit\\Framework\\TestCase");
        add("\\PHPUnit_Framework_TestCase");
    }};

    private static final Collection<String> PROPHESIZE_CLASSES = new HashSet<>(PHPUNIT_CLASSES) {{
        add("\\Prophecy\\Prophet");
        add("\\Prophecy\\PhpUnit\\ProphecyTrait");
    }};

    private static final Map<String, Collection<String>> METHODS = new HashMap<>() {{
        put("getMock", PHPUNIT_CLASSES);
        put("getMockClass", PHPUNIT_CLASSES);
        put("getMockForAbstractClass", PHPUNIT_CLASSES);
        put("getMockForTrait", PHPUNIT_CLASSES);
        put("createMock", PHPUNIT_CLASSES);
        put("prophesize", PROPHESIZE_CLASSES);
    }};

    @Override
    public char getKey() {
        return CHAR;
    }

    @Nullable
    @Override
    public PhpType getType(PsiElement psiElement) {
        if(!(psiElement instanceof MethodReference) || !METHODS.containsKey(((MethodReference) psiElement).getName())) {
            return null;
        }

        String signature = PhpTypeProviderUtil.getReferenceSignatureByFirstParameter((MethodReference) psiElement, TRIM_KEY);
        return signature == null ? null : new PhpType().add("#" + this.getKey() + signature);
    }

    @Nullable
    @Override
    public PhpType complete(String s, Project project) {
        return null;
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String expression, Set<String> visited, int depth, Project project) {
        // get back our original call
        // since phpstorm 7.1.2 we need to validate this
        int endIndex = expression.lastIndexOf(TRIM_KEY);
        if(endIndex == -1) {
            return Collections.emptySet();
        }

        String originalSignature = expression.substring(0, endIndex);
        String parameter = expression.substring(endIndex + 1);

        Collection<PhpNamedElement> elements = new HashSet<>();

        // search for called method
        PhpIndex phpIndex = PhpIndex.getInstance(project);
        for (PhpNamedElement method : PhpTypeProviderUtil.getTypeSignature(phpIndex, originalSignature)) {
            if(!(method instanceof Method)) {
                continue;
            }

            // find classes for this method
            Collection<String> myClasses = METHODS.getOrDefault(method.getName(), Collections.emptyList());
            if(myClasses.size() == 0) {
                continue;
            }

            PhpClass containingClass = ((Method) method).getContainingClass();
            if(containingClass == null) {
                continue;
            }

            String parameterResolved = PhpTypeProviderUtil.getResolvedParameter(phpIndex, parameter);
            if(parameterResolved == null) {
                continue;
            }

            for (String s : myClasses) {
                if(PhpElementsUtil.isInstanceOf(containingClass, s)) {
                    elements.addAll(PhpIndex.getInstance(project).getAnyByFQN(parameterResolved));
                }
            }
        }

        return elements;
    }
}
