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

class MockeryMethodNameTypeProvider : PhpTypeProvider4 {
    override fun getKey(): Char {
        return '\u1643'
    }

    override fun getType(psiElement: PsiElement): PhpType? {
        if (PhpTypeProviderUtil.isMethodReferenceWithSpecificName(psiElement, "allows") ||
            PhpTypeProviderUtil.isMethodReferenceWithSpecificName(psiElement, "expects") ||
            PhpTypeProviderUtil.isMethodReferenceWithSpecificName(psiElement, "shouldReceive") ||
            PhpTypeProviderUtil.isMethodReferenceWithSpecificName(psiElement, "shouldNotReceive") ||
            PhpTypeProviderUtil.isMethodReferenceWithSpecificName(psiElement, "shouldHaveReceived")
        ) {
            val methodReference = psiElement as MethodReference
            val clazz = IndexLessMethodParameterChainProcessor.createParameter(methodReference, "mock")
            if (clazz != null) {
                var type = "#" + key + Base64.getEncoder().encodeToString(clazz.toByteArray(StandardCharsets.UTF_8))

                if (methodReference.parameters.isEmpty()) {
                    // e.g. $mock->allows()
                    // Following used by MockeryExpectationTypeProvider to know to add the type Expectation to the
                    // method reference that follows, e.g $mock->allows()->foo()
                    type += MockeryExpectationTypeProvider.getTrimKey().toString() + "ExpectingMockedMethod"
                } else {
                    // e.g. $mock->allows('foo')
                    // In this case getBySignature will not give type of Mocked object
                    type += MockeryExpectationTypeProvider.getTrimKey().toString() + "NotExpectingMockedMethod"
                }

                return PhpType().add(type)
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

        val split = expression.split(MockeryExpectationTypeProvider.getTrimKey().toString().toRegex())
        if (split.size != 2) {
            return null
        }

        val resolvedParameter = PhpTypeProviderUtil.getResolvedParameter(
            phpIndex,
            String(Base64.getDecoder().decode(split[0]), StandardCharsets.UTF_8),
        ) ?: return null

        if (split[1] == "NotExpectingMockedMethod") {
            return null
        }

        return phpIndex.getAnyByFQN(resolvedParameter)
    }
}
