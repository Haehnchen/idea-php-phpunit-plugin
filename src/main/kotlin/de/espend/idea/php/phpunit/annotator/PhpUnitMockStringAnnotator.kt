package de.espend.idea.php.phpunit.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import de.espend.idea.php.phpunit.utils.mockstring.AvailabilityHelper
import de.espend.idea.php.phpunit.utils.mockstring.FilterFactory

/**
 * Warns on invalid PHPUnit mock method strings.
 *
 * <pre>
 * $this->getMock(Foo::class, ['method']);
 * PHPUnit_Helper::callProtectedMethod(Foo::class, 'protectedMethod');
 * </pre>
 */
class PhpUnitMockStringAnnotator : Annotator {
    override fun annotate(psiElement: PsiElement, annotationHolder: AnnotationHolder) {
        if (AvailabilityHelper.checkFile(psiElement.containingFile) && AvailabilityHelper.checkScope(psiElement)) {
            val filter = FilterFactory.getFilter(psiElement)
            if (filter != null) {
                val phpClass = filter.phpClass
                if (phpClass != null) {
                    val name = StringUtil.unquoteString(psiElement.text)
                    val method = phpClass.findMethodByName(name)
                    val textRange = psiElement.textRange
                    val annotationTextRange = TextRange(textRange.startOffset + 1, textRange.endOffset - 1)
                    if (method == null) {
                        if (phpClass.findFieldByName(name, false) == null) {
                            annotationHolder.newAnnotation(HighlightSeverity.WARNING, "Method '$name' not found in class ${phpClass.name}")
                                .range(annotationTextRange).create()
                        }
                    } else {
                        if (!filter.isMethodAllowed(method)) {
                            annotationHolder.newAnnotation(HighlightSeverity.WARNING, "Method '$name' is not allowed to use here")
                                .range(annotationTextRange).create()
                        }
                    }
                }
            }
        }
    }
}
