package de.espend.idea.php.phpunit.completion

import com.intellij.codeInsight.completion.CompletionConfidence
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.ThreeState
import de.espend.idea.php.phpunit.utils.mockstring.AvailabilityHelper

/**
 * Keeps auto-popup available in PHPUnit mock string positions.
 *
 * <pre>
 * $this->getMock(Foo::class, ['<caret>']);
 * $mock->method('<caret>');
 * </pre>
 */
class PhpUnitMockStringCompletionConfidence : CompletionConfidence() {
    override fun shouldSkipAutopopup(
        editor: Editor,
        contextElement: PsiElement,
        psiFile: PsiFile,
        offset: Int
    ): ThreeState {
        if (!AvailabilityHelper.checkFile(psiFile)) {
            return ThreeState.UNSURE
        }

        if (!AvailabilityHelper.checkScope(contextElement.context)) {
            return ThreeState.NO
        }

        return ThreeState.UNSURE
    }
}
