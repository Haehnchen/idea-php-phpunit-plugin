package de.espend.idea.php.phpunit.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import de.espend.idea.php.phpunit.utils.PatternUtil
import de.espend.idea.php.phpunit.utils.PhpUnitPluginUtil

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
class PhpUnitCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PatternUtil.getMethodReferenceWithParameterInsideTokenStringPattern(),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    completionParameters: CompletionParameters,
                    processingContext: ProcessingContext,
                    resultSet: CompletionResultSet
                ) {
                    val psiElement = completionParameters.position

                    val parent = psiElement.parent
                    if (parent is StringLiteralExpression) {
                        val parameter = PhpUnitPluginUtil.findCreateMockParameterOnParameterScope(parent)
                        if (parameter != null) {
                            resultSet.addAllElements(
                                PhpUnitPluginUtil.getMockableMethods(psiElement.project, parameter)
                            )
                        }
                    }
                }
            }
        )
    }
}
