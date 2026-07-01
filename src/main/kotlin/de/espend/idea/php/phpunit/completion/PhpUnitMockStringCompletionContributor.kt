package de.espend.idea.php.phpunit.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.jetbrains.php.lang.patterns.PhpPatterns

/**
 * Completes PHPUnit mock method strings.
 *
 * <pre>
 * $this->getMockBuilder(Foo::class)->setMethods(['<caret>']);
 * $mock->expects($this->any())->method('<caret>');
 * </pre>
 */
class PhpUnitMockStringCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PhpPatterns.psiElement(),
            PhpUnitMockStringCompletionProvider()
        )
    }
}
