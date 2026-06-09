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

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class GetMockTypeProvider implements PhpTypeProvider4 {
    @Override
    public char getKey() {
        return '\u1534';
    }

    @Nullable
    @Override
    public PhpType getType(PsiElement psiElement) {
        if(psiElement instanceof MethodReference && "getMock".equals(((MethodReference) psiElement).getName())) {
            String clazz = IndexLessMethodParameterChainProcessor.createParameter((MethodReference) psiElement, "getMockBuilder");
            if(clazz != null) {
                return new PhpType().add("#" + this.getKey() + Base64.getEncoder().encodeToString(clazz.getBytes(StandardCharsets.UTF_8)));
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

        String resolvedParameter = PhpTypeProviderUtil.getResolvedParameter(phpIndex, new String(Base64.getDecoder().decode(expression), StandardCharsets.UTF_8));
        if(resolvedParameter == null) {
            return null;
        }

        return phpIndex.getAnyByFQN(resolvedParameter);
    }
}
