package de.espend.idea.php.phpunit.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpClassMethodReference extends PsiPolyVariantReferenceBase<PsiElement> {
    @NotNull
    private final String method;

    @NotNull
    private final String clazz;

    public PhpClassMethodReference(@NotNull StringLiteralExpression psiElement, @NotNull String method, @NotNull String clazz) {
        super(psiElement);
        this.method = method;
        this.clazz = clazz;
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean b) {
        Collection<ResolveResult> resolveResults = new ArrayList<>();

        for (PhpClass phpClass : PhpIndex.getInstance(getElement().getProject()).getAnyByFQN(this.clazz)) {
            Method method = phpClass.findMethodByName(this.method);
            if(method != null) {
                resolveResults.add(new PsiElementResolveResult(method));
            }
        }

        return resolveResults.toArray(new ResolveResult[0]);
    }

    @Override
    public Object @NotNull [] getVariants() {
        return new Object[0];
    }
}
