package de.espend.idea.php.phpunit.completion;

import com.intellij.codeInsight.completion.CompletionConfidence;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ThreeState;
import de.espend.idea.php.phpunit.utils.mockstring.AvailabilityHelper;
import org.jetbrains.annotations.NotNull;

/**
 * Keeps auto-popup available in PHPUnit mock string positions.
 *
 * <pre>
 * $this->getMock(Foo::class, ['<caret>']);
 * $mock->method('<caret>');
 * </pre>
 */
public class PhpUnitMockStringCompletionConfidence extends CompletionConfidence {

    @NotNull
    @Override
    public ThreeState shouldSkipAutopopup(@NotNull Editor editor, @NotNull PsiElement contextElement, @NotNull PsiFile psiFile, int offset) {
        if (!AvailabilityHelper.checkFile(psiFile)) {
            return ThreeState.UNSURE;
        }

        if (!AvailabilityHelper.checkScope(contextElement.getContext())) {
            return ThreeState.NO;
        }

        return ThreeState.UNSURE;
    }
}
