package de.espend.idea.php.phpunit.utils.mockstring

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.PhpPsiElement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.phpunit.PhpUnitUtil

object AvailabilityHelper {
    fun checkFile(psiFile: PsiFile): Boolean {
        return PhpUnitUtil.isPhpUnitTestFile(psiFile)
    }

    fun checkScope(psiElement: PsiElement?): Boolean {
        if (psiElement !is StringLiteralExpression) {
            return false
        }

        // $this->method('cursor');
        val stringLiteralExpressionContext = psiElement.context
        if (stringLiteralExpressionContext is ParameterList) {
            return true
        }

        // $this->method(array('cursor'));
        if (stringLiteralExpressionContext is PhpPsiElement && stringLiteralExpressionContext.toString() == "Array value") {
            val arrayValueContext = stringLiteralExpressionContext.context
            if (arrayValueContext is ArrayCreationExpression && arrayValueContext.context is ParameterList) {
                return true
            }
        }

        return false
    }
}
