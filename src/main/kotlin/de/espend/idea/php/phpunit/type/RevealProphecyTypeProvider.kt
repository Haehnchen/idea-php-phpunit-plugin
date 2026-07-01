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
import java.nio.charset.StandardCharsets
import java.util.Base64

/**
 * $foobar = $this->prophesize(Foobar::class);
 * $foobar->reveal();
 *
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
class RevealProphecyTypeProvider : PhpTypeProvider4 {
    override fun getKey(): Char {
        return '\u1537'
    }

    override fun getType(element: PsiElement): PhpType? {
        if (element is MethodReference && "reveal" == element.name) {
            val method = PsiTreeUtil.getStubOrPsiParentOfType(element, Method::class.java)
            if (method != null) {
                val containingClass = method.containingClass

                // filter phpunit test methods
                if (containingClass != null && PhpUnitPluginUtil.isTestClassWithoutIndexAccess(containingClass)) {
                    val prophesize = ProphecyTypeUtil.getLocalProphesizeType(element)
                    if (prophesize != null) {
                        return PhpType().add(
                            "#" + key + Base64.getEncoder()
                                .encodeToString(prophesize.toByteArray(StandardCharsets.UTF_8)),
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
        val phpIndex = PhpIndex.getInstance(project)

        val resolvedParameter = PhpTypeProviderUtil.getResolvedParameter(
            phpIndex,
            String(Base64.getDecoder().decode(expression), StandardCharsets.UTF_8),
        ) ?: return null

        return phpIndex.getAnyByFQN(resolvedParameter)
    }
}
