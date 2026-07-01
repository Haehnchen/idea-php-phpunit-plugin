package de.espend.idea.php.phpunit.reference

import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.jetbrains.php.lang.parser.PhpElementTypes
import com.jetbrains.php.lang.patterns.PhpPatterns

/**
 * Resolves PHPUnit mock method/property strings to class members.
 *
 * <pre>
 * $mock->method('methodName');
 * PHPUnit_Helper::getProtectedPropertyValue(Foo::class, 'fieldName');
 * </pre>
 */
class PhpUnitMockStringReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(psiReferenceRegistrar: PsiReferenceRegistrar) {
        psiReferenceRegistrar.registerReferenceProvider(
            PhpPatterns.psiElement().withElementType(PhpElementTypes.STRING),
            PhpUnitMockStringReferenceProvider()
        )
    }
}
