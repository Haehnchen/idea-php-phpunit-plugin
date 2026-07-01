package de.espend.idea.php.phpunit.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import de.espend.idea.php.phpunit.utils.mockstring.AvailabilityHelper

/**
 * Attaches references only inside supported PHPUnit mock string scopes.
 *
 * <pre>
 * $this->getMock(Foo::class, ['methodName']);
 * PHPUnit_Helper::getProtectedPropertyValue(Foo::class, 'fieldName');
 * </pre>
 */
class PhpUnitMockStringReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(
        psiElement: PsiElement,
        processingContext: ProcessingContext
    ): Array<PsiReference> {
        if (AvailabilityHelper.checkFile(psiElement.containingFile) && AvailabilityHelper.checkScope(psiElement)) {
            val reference = PhpUnitMockStringReference(psiElement)
            return arrayOf(reference)
        }

        return PsiReference.EMPTY_ARRAY
    }
}
