package de.espend.idea.php.phpunit.type

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4
import de.espend.idea.php.phpunit.type.utils.PhpTypeProviderUtil
import de.espend.idea.php.phpunit.utils.processor.IndexLessMethodParameterChainProcessor
import java.nio.charset.StandardCharsets
import java.util.Base64

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
class GetMockTypeProvider : PhpTypeProvider4 {
    override fun getKey(): Char {
        return '\u1534'
    }

    override fun getType(psiElement: PsiElement): PhpType? {
        if (psiElement is MethodReference && "getMock" == psiElement.name) {
            val clazz = IndexLessMethodParameterChainProcessor.createParameter(psiElement, "getMockBuilder")
            if (clazz != null) {
                return PhpType().add("#" + key + Base64.getEncoder().encodeToString(clazz.toByteArray(StandardCharsets.UTF_8)))
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
