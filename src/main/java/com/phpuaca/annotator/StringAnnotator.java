package com.phpuaca.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.phpuaca.filter.Filter;
import com.phpuaca.filter.FilterFactory;
import com.phpuaca.helper.AvailabilityHelper;
import org.jetbrains.annotations.NotNull;

public class StringAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        AvailabilityHelper availabilityHelper = new AvailabilityHelper();
        if (availabilityHelper.checkFile(psiElement.getContainingFile()) && availabilityHelper.checkScope(psiElement)) {
            Filter filter = FilterFactory.getInstance().getFilter(psiElement);
            if (filter != null) {
                PhpClass phpClass = filter.getPhpClass();
                if (phpClass != null) {
                    String name = StringUtil.unquoteString(psiElement.getText());
                    Method method = phpClass.findMethodByName(name);
                    TextRange textRange = psiElement.getTextRange();
                    TextRange annotationTextRange = new TextRange(textRange.getStartOffset() + 1, textRange.getEndOffset() - 1);
                    if (method == null) {
                        if (phpClass.findFieldByName(name, false) == null) {
                            annotationHolder.newAnnotation(HighlightSeverity.WARNING, "Method '" + name + "' not found in class " + phpClass.getName())
                                .range(annotationTextRange).create();
                        }
                    } else {
                        if (!filter.isMethodAllowed(method)) {
                            annotationHolder.newAnnotation(HighlightSeverity.WARNING, "Method '" + name + "' is not allowed to use here")
                                .range(annotationTextRange).create();
                        }
                    }
                }
            }
        }
    }
}
