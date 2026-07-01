package de.espend.idea.php.phpunit.utils.mockstring

import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpModifier

abstract class Filter {
    private var methodsAllowed = false
    private var fieldsAllowed = false

    private val allowedMethods: MutableList<String> = ArrayList()
    private val allowedModifiers: MutableList<String> = ArrayList()
    private val disallowedMethods: MutableList<String> = ArrayList()
    private val describedMethods: MutableList<String> = ArrayList()

    var phpClass: PhpClass? = null

    fun allowMethod(methodName: String) {
        allowMethods()
        disallowedMethods.remove(methodName)
        allowedMethods.add(methodName)
    }

    fun disallowMethod(methodName: String) {
        allowMethods()
        allowedMethods.remove(methodName)
        disallowedMethods.add(methodName)
    }

    fun describeMethod(methodName: String) {
        describedMethods.add(methodName)
    }

    fun allowModifier(modifierName: String) {
        allowedModifiers.add(modifierName)
    }

    fun allowModifier(modifier: PhpModifier) {
        allowModifier(modifier.toString())
    }

    fun allowMethods() {
        methodsAllowed = true
    }

    fun allowMethods(methodNames: List<String>) {
        for (methodName in methodNames) {
            allowMethod(methodName)
        }
    }

    fun describeMethods(methodNames: List<String>) {
        for (methodName in methodNames) {
            describeMethod(methodName)
        }
    }

    fun allowFields() {
        fieldsAllowed = true
    }

    protected fun isMethodAllowed(methodName: String): Boolean {
        return methodsAllowed && !disallowedMethods.contains(methodName) && (allowedMethods.isEmpty() || allowedMethods.contains(methodName))
    }

    fun isMethodAllowed(method: Method): Boolean {
        return isMethodAllowed(method.name) && isModifierAllowed(method.modifier)
    }

    fun isMethodDescribed(methodName: String): Boolean {
        return describedMethods.contains(methodName)
    }

    fun isMethodDescribed(method: Method): Boolean {
        return isMethodDescribed(method.name)
    }

    protected fun isFieldAllowed(fieldName: String): Boolean {
        return fieldsAllowed
    }

    fun isFieldAllowed(field: Field): Boolean {
        return field !is PhpDocProperty && isFieldAllowed(field.name) && isModifierAllowed(field.modifier)
    }

    protected fun isModifierAllowed(modifierName: String): Boolean {
        return allowedModifiers.isEmpty() || allowedModifiers.contains(modifierName)
    }

    protected fun isModifierAllowed(modifier: PhpModifier): Boolean {
        return isModifierAllowed(modifier.toString())
    }

    data class Context(
        val filterConfigItem: FilterConfig.Item,
        val methodReference: MethodReference
    )
}
