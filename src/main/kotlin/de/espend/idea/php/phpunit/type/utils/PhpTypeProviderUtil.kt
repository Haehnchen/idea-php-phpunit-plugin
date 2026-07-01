package de.espend.idea.php.phpunit.type.utils

import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.ClassConstantReference
import com.jetbrains.php.lang.psi.elements.FieldReference
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.elements.PhpReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import de.espend.idea.php.phpunit.utils.PhpElementsUtil
import org.apache.commons.lang3.StringUtils

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
object PhpTypeProviderUtil {
    /**
     * Creates a signature for PhpType implementation which must be resolved inside 'getBySignature'
     *
     * eg. foo(MyClass::class) => "#F\foo|#K#C\Foo.class"
     *
     * foo($this->foo), foo('foobar')
     */
    fun getReferenceSignatureByFirstParameter(functionReference: FunctionReference): String? {
        val parameters = functionReference.parameters
        if (parameters.isEmpty()) {
            return null
        }

        val parameter = parameters[0]

        // we already have a string value
        if (parameter is StringLiteralExpression) {
            val param = parameter.contents
            if (StringUtil.isNotEmpty(param)) {
                return param
            }

            return null
        }

        // whitelist here; we can also provide some more but think of performance
        // Service::NAME, $this->name and Entity::CLASS;
        if (parameter is PhpReference && (parameter is ClassConstantReference || parameter is FieldReference)) {
            val signature = parameter.signature
            if (StringUtil.isNotEmpty(signature)) {
                return signature
            }
        }

        return null
    }

    /**
     * Creates a signature for PhpType implementation which must be resolved inside 'getBySignature'
     *
     * eg. foo(MyClass::class) => "#F\foo|#K#C\Foo.class"
     *
     * foo($this->foo), foo('foobar')
     */
    fun getReferenceSignatureByFirstParameter(functionReference: FunctionReference, trimKey: Char): String? {
        val refSignature = functionReference.signature
        if (StringUtil.isEmpty(refSignature)) {
            return null
        }

        val parameters = functionReference.parameters
        if (parameters.isEmpty()) {
            return null
        }

        val parameter = parameters[0]

        // we already have a string value
        if (parameter is StringLiteralExpression) {
            val param = parameter.contents
            if (StringUtil.isNotEmpty(param)) {
                return refSignature + trimKey + param
            }

            return null
        }

        // whitelist here; we can also provide some more but think of performance
        // Service::NAME, $this->name and Entity::CLASS;
        if (parameter is PhpReference && (parameter is ClassConstantReference || parameter is FieldReference)) {
            val signature = parameter.signature
            if (StringUtil.isNotEmpty(signature)) {
                return refSignature + trimKey + signature
            }
        }

        return null
    }

    /**
     * we can also pipe php references signatures and resolve them here
     * overwrite parameter to get string value
     */
    fun getResolvedParameter(phpIndex: PhpIndex, parameter: String): String? {
        return getResolvedParameter(phpIndex, parameter, null, 0)
    }

    /**
     * we can also pipe php references signatures and resolve them here
     * overwrite parameter to get string value
     */
    fun getResolvedParameter(phpIndex: PhpIndex, parameter: String, visited: Set<String>?, depth: Int): String? {
        var resolvedParameter = parameter

        // PHP 5.5 class constant: "Class\Foo::class"
        if (resolvedParameter.startsWith("#K#C")) {
            // PhpStorm9: #K#C\Class\Foo.class
            if (resolvedParameter.endsWith(".class")) {
                return StringUtils.stripStart(resolvedParameter.substring(4, resolvedParameter.length - 6), "\\")
            }
        }

        // #K#C\Class\Foo.property
        // #K#C\Class\Foo.CONST
        if (resolvedParameter.startsWith("#")) {
            // get psi element from signature
            val signTypes = phpIndex.getBySignature(resolvedParameter, visited, depth)
            if (signTypes.size == 0) {
                return null
            }

            // get string value
            resolvedParameter = PhpElementsUtil.getStringValue(signTypes.iterator().next()) ?: return null
        }

        return resolvedParameter
    }

    /**
     * We can have multiple types inside a TypeProvider; split them on "|" so that we dont get empty types
     *
     * #M#x#M#C\FooBar.get?doctrine.odm.mongodb.document_manager.getRepository|
     * #M#x#M#C\FooBar.get?doctrine.odm.mongodb.document_manager.getRepository
     */
    fun getTypeSignature(phpIndex: PhpIndex, signature: String): Collection<PhpNamedElement> {
        if (!signature.contains("|")) {
            return phpIndex.getBySignature(signature, null, 0)
        }

        val elements: MutableCollection<PhpNamedElement> = ArrayList()
        for (s in signature.split("\\|".toRegex())) {
            elements.addAll(phpIndex.getBySignature(s, null, 0))
        }

        return elements
    }

    fun isMethodReferenceWithSpecificName(psiElement: PsiElement, methodName: String): Boolean {
        return psiElement is MethodReference && methodName == psiElement.name
    }
}
