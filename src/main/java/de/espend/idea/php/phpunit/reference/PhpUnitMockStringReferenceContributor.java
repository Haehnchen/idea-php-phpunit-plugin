package de.espend.idea.php.phpunit.reference;

import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.patterns.PhpPatterns;
import org.jetbrains.annotations.NotNull;

/**
 * Resolves PHPUnit mock method/property strings to class members.
 *
 * <pre>
 * $mock->method('methodName');
 * PHPUnit_Helper::getProtectedPropertyValue(Foo::class, 'fieldName');
 * </pre>
 */
public class PhpUnitMockStringReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar psiReferenceRegistrar) {
        psiReferenceRegistrar.registerReferenceProvider(
                PhpPatterns.psiElement().withElementType(PhpElementTypes.STRING),
                new PhpUnitMockStringReferenceProvider()
        );
    }
}
