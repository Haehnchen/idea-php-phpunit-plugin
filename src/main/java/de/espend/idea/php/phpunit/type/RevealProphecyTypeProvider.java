package de.espend.idea.php.phpunit.type;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.PhpPsiUtil;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4;
import de.espend.idea.php.phpunit.type.utils.PhpTypeProviderUtil;
import de.espend.idea.php.phpunit.type.utils.ProphecyTypeUtil;
import de.espend.idea.php.phpunit.utils.PhpUnitPluginUtil;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Set;

/**
 * $foobar = $this->prophesize(Foobar::class);
 * $foobar->reveal();
 *
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class RevealProphecyTypeProvider implements PhpTypeProvider4 {
    @Override
    public char getKey() {
        return '\u1537';
    }

    @Nullable
    @Override
    public PhpType getType(PsiElement element) {
        if(element instanceof MethodReference && "reveal".equals(((MethodReference) element).getName())) {
            Method method = PhpPsiUtil.getParentByCondition(element, Method.INSTANCEOF);
            if(method != null) {
                PhpClass containingClass = method.getContainingClass();

                // filter phpunit test methods
                if (containingClass != null && PhpUnitPluginUtil.isTestClassWithoutIndexAccess(containingClass)) {
                    String prophesize = ProphecyTypeUtil.getLocalProphesizeType((MethodReference) element);
                    if (prophesize != null) {
                        return new PhpType().add("#" + this.getKey() + Base64.getEncoder().encodeToString(prophesize.getBytes(StandardCharsets.UTF_8)));
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
        PhpIndex phpIndex = PhpIndex.getInstance(project);

        String resolvedParameter = PhpTypeProviderUtil.getResolvedParameter(phpIndex, new String(Base64.getDecoder().decode(expression), StandardCharsets.UTF_8));
        if(resolvedParameter == null) {
            return null;
        }

        return phpIndex.getAnyByFQN(resolvedParameter);
    }
}
