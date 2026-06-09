package de.espend.idea.php.phpunit.type;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4;
import de.espend.idea.php.phpunit.type.utils.PhpTypeProviderUtil;
import de.espend.idea.php.phpunit.utils.processor.IndexLessMethodParameterChainProcessor;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Set;

public class MockeryMethodNameTypeProvider implements PhpTypeProvider4 {

    @Override
    public char getKey() {
        return '\u1643';
    }

    @Nullable
    @Override
    public PhpType getType(PsiElement psiElement) {
        if (PhpTypeProviderUtil.isMethodReferenceWithSpecificName(psiElement, "allows") ||
                PhpTypeProviderUtil.isMethodReferenceWithSpecificName(psiElement, "expects") ||
                PhpTypeProviderUtil.isMethodReferenceWithSpecificName(psiElement, "shouldReceive") ||
                PhpTypeProviderUtil.isMethodReferenceWithSpecificName(psiElement, "shouldNotReceive") ||
                PhpTypeProviderUtil.isMethodReferenceWithSpecificName(psiElement, "shouldHaveReceived")) {

            String clazz = IndexLessMethodParameterChainProcessor.createParameter((MethodReference) psiElement, "mock");
            if (clazz != null) {
                String type = "#" + this.getKey() + Base64.getEncoder().encodeToString(clazz.getBytes(StandardCharsets.UTF_8));

                if (((MethodReference) psiElement).getParameters().length == 0) {
                    // e.g. $mock->allows()
                    // Following used by MockeryExpectationTypeProvider to know to add the type Expectation to the
                    // method reference that follows, e.g $mock->allows()->foo()
                    type = type + MockeryExpectationTypeProvider.getTrimKey() + "ExpectingMockedMethod";
                } else {
                    // e.g. $mock->allows('foo')
                    // In this case getBySignature will not give type of Mocked object
                    type = type + MockeryExpectationTypeProvider.getTrimKey() + "NotExpectingMockedMethod";
                }

                return new PhpType().add(type);
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
        PhpIndex phpIndex = PhpIndex.getInstance(project);

        String[] split = expression.split(String.valueOf(MockeryExpectationTypeProvider.getTrimKey()));
        if (split.length != 2) {
            return null;
        }

        String resolvedParameter = PhpTypeProviderUtil.getResolvedParameter(phpIndex, new String(Base64.getDecoder().decode(split[0]), StandardCharsets.UTF_8));
        if (resolvedParameter == null) {
            return null;
        }

        if (split[1].equals("NotExpectingMockedMethod")) {
            return null;
        }

        return phpIndex.getAnyByFQN(resolvedParameter);
    }
}
