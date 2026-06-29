package de.espend.idea.php.phpunit.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import de.espend.idea.php.phpunit.utils.mockstring.AvailabilityHelper;
import org.jetbrains.annotations.NotNull;

/**
 * Attaches references only inside supported PHPUnit mock string scopes.
 *
 * <pre>
 * $this->getMock(Foo::class, ['methodName']);
 * PHPUnit_Helper::getProtectedPropertyValue(Foo::class, 'fieldName');
 * </pre>
 */
public class PhpUnitMockStringReferenceProvider extends PsiReferenceProvider {

    @NotNull
    @Override
    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        if (AvailabilityHelper.checkFile(psiElement.getContainingFile()) && AvailabilityHelper.checkScope(psiElement)) {
            PhpUnitMockStringReference reference = new PhpUnitMockStringReference(psiElement);
            return new PsiReference[]{reference};
        }

        return PsiReference.EMPTY_ARRAY;
    }
}
