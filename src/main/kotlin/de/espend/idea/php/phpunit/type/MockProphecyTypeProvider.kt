package de.espend.idea.php.phpunit.type

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4
import de.espend.idea.php.phpunit.type.utils.PhpTypeProviderUtil
import de.espend.idea.php.phpunit.utils.PhpElementsUtil
import java.util.Collections

class MockProphecyTypeProvider : PhpTypeProvider4 {
    override fun getKey(): Char {
        return CHAR
    }

    override fun getType(psiElement: PsiElement): PhpType? {
        if (psiElement !is MethodReference || !METHODS.containsKey(psiElement.name)) {
            return null
        }

        val signature = PhpTypeProviderUtil.getReferenceSignatureByFirstParameter(psiElement, TRIM_KEY)
        return if (signature == null) null else PhpType().add("#" + key + signature)
    }

    override fun complete(s: String, project: Project): PhpType? {
        return null
    }

    override fun getBySignature(
        expression: String,
        visited: Set<String>,
        depth: Int,
        project: Project,
    ): Collection<PhpNamedElement> {
        // get back our original call
        // since phpstorm 7.1.2 we need to validate this
        val endIndex = expression.lastIndexOf(TRIM_KEY)
        if (endIndex == -1) {
            return Collections.emptySet()
        }

        val originalSignature = expression.substring(0, endIndex)
        val parameter = expression.substring(endIndex + 1)

        val elements: MutableCollection<PhpNamedElement> = HashSet()

        // search for called method
        val phpIndex = PhpIndex.getInstance(project)
        for (method in PhpTypeProviderUtil.getTypeSignature(phpIndex, originalSignature)) {
            if (method !is Method) {
                continue
            }

            // find classes for this method
            val myClasses = METHODS[method.name] ?: emptySet()
            if (myClasses.isEmpty()) {
                continue
            }

            val containingClass = method.containingClass ?: continue
            val parameterResolved = PhpTypeProviderUtil.getResolvedParameter(phpIndex, parameter) ?: continue

            for (className in myClasses) {
                if (PhpElementsUtil.isInstanceOf(containingClass, className)) {
                    elements.addAll(PhpIndex.getInstance(project).getAnyByFQN(parameterResolved))
                }
            }
        }

        return elements
    }

    companion object {
        const val CHAR: Char = '\u5143'

        @JvmField
        var TRIM_KEY: Char = '\u0192'

        private val PHPUNIT_CLASSES: Set<String> = setOf(
            "\\PHPUnit\\Framework\\TestCase",
            "\\PHPUnit_Framework_TestCase",
        )

        private val PROPHESIZE_CLASSES: Set<String> = PHPUNIT_CLASSES + setOf(
            "\\Prophecy\\Prophet",
            "\\Prophecy\\PhpUnit\\ProphecyTrait",
        )

        private val METHODS: Map<String, Collection<String>> = mapOf(
            "getMock" to PHPUNIT_CLASSES,
            "getMockClass" to PHPUNIT_CLASSES,
            "getMockForAbstractClass" to PHPUNIT_CLASSES,
            "getMockForTrait" to PHPUNIT_CLASSES,
            "createMock" to PHPUNIT_CLASSES,
            "prophesize" to PROPHESIZE_CLASSES,
        )
    }
}
