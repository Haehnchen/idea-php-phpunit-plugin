package de.espend.idea.php.phpunit.type

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4
import de.espend.idea.php.phpunit.type.utils.PhpTypeProviderUtil
import de.espend.idea.php.phpunit.utils.PhpElementsUtil

/**
 * Attach the types from the wrapped prophesize class based on the method index
 *
 * $this->prophesize(Foobar::class)->find(Arguments::a<caret>ny());
 *
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
class ProphecyArgumentTypeProvider : PhpTypeProvider4 {
    override fun getKey(): Char {
        return '\u0162'
    }

    override fun getType(psiElement: PsiElement): PhpType? {
        if (psiElement !is MethodReference || !psiElement.isStatic || !METHODS.contains(psiElement.name)) {
            return null
        }

        val parameterList = psiElement.parent
        if (parameterList !is ParameterList) {
            return null
        }

        if (!PhpElementsUtil.isLocalResolveMethodReferenceInstanceOf(psiElement, PROPHECY_ARGUMENT_CLASS)) {
            return null
        }

        val methodReference = PsiTreeUtil.getParentOfType(psiElement, MethodReference::class.java)
        if (methodReference != null) {
            // first type wins from main "prophecy" call
            for (type in methodReference.type.types) {
                // pipe the parameter prophecy type provider without its "#" starting elements; also attach the parameter to resolve it later
                if (type.startsWith("#" + ProphecyTypeProvider.TYPE_KEY)) {
                    val parameterIndex = PhpElementsUtil.getParameterIndex(parameterList, psiElement)
                    if (parameterIndex != null) {
                        return PhpType().add("#" + key + type.substring(2) + TRIM_KEY + parameterIndex)
                    }
                }
            }
        }

        return null
    }

    override fun complete(s: String, project: Project): PhpType? {
        // strip our type prefix "#..." and extract our signature with the type and the parameter index
        val substring = s.substring(2)
        val signature = substring.split(TRIM_KEY.toString().toRegex())
        if (signature.size != 2) {
            return null
        }

        // split the foreign prophecy type resolve to get the main instance
        val foreignSignature = signature[0]
        val regex = foreignSignature.split(ProphecyTypeProvider.TRIM_KEY.toString().toRegex())
        if (regex.size != 2) {
            return null
        }

        val className = PhpTypeProviderUtil.getResolvedParameter(PhpIndex.getInstance(project), regex[0]) ?: return null

        // attach the mocked prophecy class instance with its used parameter
        var types: PhpType? = null
        for (phpClass: PhpClass in PhpIndex.getInstance(project).getAnyByFQN(className)) {
            val methodByName: Method = phpClass.findMethodByName(regex[1]) ?: continue

            val methodParameter = signature[1].toInt()
            val parameter = methodByName.getParameter(methodParameter)
            if (parameter != null) {
                // init PhpType; allow collection all types for all classes
                if (types == null) {
                    types = PhpType()
                }

                types.add(parameter.type)
            }
        }

        return types
    }

    override fun getBySignature(
        s: String,
        set: Set<String>,
        i: Int,
        project: Project,
    ): Collection<PhpNamedElement>? {
        return null
    }

    companion object {
        private val METHODS: Collection<String> = listOf(
            "any",
            "cetera", // fill arguments, we can not fix magic arguments here, but the type
        )

        private const val PROPHECY_ARGUMENT_CLASS = "\\Prophecy\\Argument"
        private const val TRIM_KEY: Char = '\u1539'
    }
}
