package de.espend.idea.php.phpunit.type

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4
import de.espend.idea.php.phpunit.type.utils.PhpTypeProviderUtil
import de.espend.idea.php.phpunit.utils.PhpUnitPluginUtil

class MockeryExpectationTypeProvider : PhpTypeProvider4 {
    override fun getKey(): Char {
        return TYPE_KEY
    }

    override fun getType(element: PsiElement): PhpType? {
        if (element is MethodReference) {
            val method = PsiTreeUtil.getStubOrPsiParentOfType(element, Method::class.java)

            if (method == null) {
                return null
            }

            val containingClass: PhpClass? = method.containingClass

            // filter phpunit test methods
            if (containingClass != null && PhpUnitPluginUtil.isTestClassWithoutIndexAccess(containingClass)) {
                // Need to check if child element type is expecting a mocked method now
                val childElem = element.firstChild!!
                val childTypes = (childElem as PhpTypedElement).type.types.toTypedArray()

                for (type in childTypes) {
                    // This is added by MockeryMethodNameTypeProvider to a new syntax method reference
                    // e.g. $mock->allows()
                    if (type.endsWith(TRIM_KEY.toString() + "ExpectingMockedMethod")) {
                        return PhpType().add(
                            "#" + key + "#K#C\\Mockery\\Expectation.class" + TRIM_KEY + element.name,
                        )
                    }
                }
            }
        }

        return null
    }

    override fun complete(s: String, project: Project): PhpType? {
        return null
    }

    override fun getBySignature(
        expression: String,
        visited: Set<String>,
        depth: Int,
        project: Project,
    ): Collection<PhpNamedElement>? {
        val split = expression.split(TRIM_KEY.toString().toRegex())
        if (split.size != 2) {
            return null
        }

        val phpIndex = PhpIndex.getInstance(project)
        val resolvedParameter = PhpTypeProviderUtil.getResolvedParameter(phpIndex, split[0])

        if (resolvedParameter != null && resolvedParameter == "Mockery\\Expectation") {
            return phpIndex.getAnyByFQN("\\Mockery\\Expectation")
        }

        return null
    }

    companion object {
        const val TRIM_KEY: Char = '\u1644'
        const val TYPE_KEY: Char = '\u1645'

        @JvmStatic
        fun getTrimKey(): Char {
            return TRIM_KEY
        }
    }
}
