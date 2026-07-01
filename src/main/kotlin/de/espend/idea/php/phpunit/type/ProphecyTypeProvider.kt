package de.espend.idea.php.phpunit.type

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4
import de.espend.idea.php.phpunit.type.utils.PhpTypeProviderUtil
import de.espend.idea.php.phpunit.type.utils.ProphecyTypeUtil
import de.espend.idea.php.phpunit.utils.PhpUnitPluginUtil

/**
 * $this->prophesize(Foobar::class)->find()->will<caret>Return;
 *
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
class ProphecyTypeProvider : PhpTypeProvider4 {
    override fun getKey(): Char {
        return TYPE_KEY
    }

    override fun getType(element: PsiElement): PhpType? {
        if (element is MethodReference) {
            val method = PsiTreeUtil.getStubOrPsiParentOfType(element, Method::class.java)
            if (method != null) {
                val containingClass = method.containingClass

                // filter phpunit test methods
                if (containingClass != null && PhpUnitPluginUtil.isTestClassWithoutIndexAccess(containingClass)) {
                    val prophesize = ProphecyTypeUtil.getLocalProphesizeType(element)
                    if (prophesize != null) {
                        return PhpType().add("#" + key + prophesize + TRIM_KEY + element.name)
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
        // SIGNATURE.METHOD_NAME
        val split = expression.split(TRIM_KEY.toString().toRegex())
        if (split.size != 2) {
            return null
        }

        val phpIndex = PhpIndex.getInstance(project)

        val resolvedParameter = PhpTypeProviderUtil.getResolvedParameter(phpIndex, split[0]) ?: return null

        if (phpIndex.getAnyByFQN(resolvedParameter).stream().noneMatch { phpClass ->
                phpClass.findMethodByName(split[1]) != null
            }
        ) {
            return null
        }

        return phpIndex.getAnyByFQN("\\Prophecy\\Prophecy\\MethodProphecy")
    }

    companion object {
        const val TRIM_KEY: Char = '\u1536'
        const val TYPE_KEY: Char = '\u1530'
    }
}
