package de.espend.idea.php.phpunit.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.util.ProcessingContext
import com.jetbrains.php.completion.PhpLookupElement
import de.espend.idea.php.phpunit.utils.mockstring.Filter
import de.espend.idea.php.phpunit.utils.mockstring.FilterFactory

/**
 * Builds completion variants for supported PHPUnit mock strings.
 *
 * <pre>
 * $this->getMockClass(Foo::class, ['<caret>']);
 * PHPUnit_Helper::setProtectedPropertyValue(Foo::class, '<caret>');
 * </pre>
 */
open class PhpUnitMockStringCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        completionParameters: CompletionParameters,
        processingContext: ProcessingContext,
        completionResultSet: CompletionResultSet
    ) {
        val originalPosition = completionParameters.originalPosition
        if (originalPosition != null) {
            val filter = FilterFactory.getFilter(originalPosition.parent)
            if (filter != null) {
                completionResultSet.addAllElements(getLookupElements(filter))
            }
        }
    }

    protected open fun getLookupElements(filter: Filter): List<LookupElement> {
        val list = ArrayList<LookupElement>()
        val phpClass = filter.phpClass

        if (phpClass != null) {
            for (method in phpClass.methods) {
                if (filter.isMethodAllowed(method) && !filter.isMethodDescribed(method)) {
                    list.add(PhpLookupElement(method))
                }
            }
            for (field in phpClass.fields) {
                if (!field.isConstant && filter.isFieldAllowed(field)) {
                    list.add(PhpLookupElement(field))
                }
            }
        }

        return list
    }
}
