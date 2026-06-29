package de.espend.idea.php.phpunit.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import de.espend.idea.php.phpunit.utils.mockstring.Filter;
import de.espend.idea.php.phpunit.utils.mockstring.FilterFactory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds completion variants for supported PHPUnit mock strings.
 *
 * <pre>
 * $this->getMockClass(Foo::class, ['<caret>']);
 * PHPUnit_Helper::setProtectedPropertyValue(Foo::class, '<caret>');
 * </pre>
 */
public class PhpUnitMockStringCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters completionParameters, @NotNull ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
        PsiElement originalPosition = completionParameters.getOriginalPosition();
        if (originalPosition != null) {
            Filter filter = FilterFactory.getFilter(originalPosition.getParent());
            if (filter != null) {
                completionResultSet.addAllElements(getLookupElements(filter));
            }
        }
    }

    @NotNull
    protected List<LookupElement> getLookupElements(@NotNull Filter filter) {
        List<LookupElement> list = new ArrayList<>();
        PhpClass phpClass = filter.getPhpClass();

        if (phpClass != null) {
            for (Method method : phpClass.getMethods()) {
                if (filter.isMethodAllowed(method) && !filter.isMethodDescribed(method)) {
                    list.add(new PhpLookupElement(method));
                }
            }
            for (Field field : phpClass.getFields()) {
                if (!field.isConstant() && filter.isFieldAllowed(field)) {
                    list.add(new PhpLookupElement(field));
                }
            }
        }

        return list;
    }
}
