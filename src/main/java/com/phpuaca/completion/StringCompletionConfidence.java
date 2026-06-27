package com.phpuaca.completion;

import com.intellij.codeInsight.completion.CompletionConfidence;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ThreeState;
import com.phpuaca.helper.AvailabilityHelper;
import org.jetbrains.annotations.NotNull;

public class StringCompletionConfidence extends CompletionConfidence {

    @NotNull
    @Override
    public ThreeState shouldSkipAutopopup(@NotNull Editor editor, @NotNull PsiElement contextElement, @NotNull PsiFile psiFile, int offset) {
        AvailabilityHelper availabilityHelper = new AvailabilityHelper();
        if (!availabilityHelper.checkFile(psiFile)) {
            return ThreeState.UNSURE;
        }

        if (!availabilityHelper.checkScope(contextElement.getContext())) {
            return ThreeState.NO;
        }

        return ThreeState.UNSURE;
    }
}
