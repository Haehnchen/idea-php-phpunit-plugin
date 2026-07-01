package de.espend.idea.php.phpunit.reference

import com.intellij.openapi.util.TextRange
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

open class PhpClassMethodReferenceForPartialMock(
    psiElement: StringLiteralExpression,
    method: String,
    clazz: String
) : PhpClassMethodReference(psiElement, method, clazz) {
    init {
        val elemText = psiElement.text
        val methodStartIndex = elemText.indexOf(method)
        val methodRange = TextRange(methodStartIndex, methodStartIndex + method.length)

        setRangeInElement(methodRange)
    }
}
