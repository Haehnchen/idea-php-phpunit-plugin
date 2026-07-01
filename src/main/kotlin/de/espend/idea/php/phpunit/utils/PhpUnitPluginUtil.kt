package de.espend.idea.php.phpunit.utils

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.completion.PhpLookupElement
import com.jetbrains.php.lang.psi.PhpPsiElementFactory
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.GroupStatement
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.phpunit.PhpUnitRuntimeConfigurationProducer
import de.espend.idea.php.phpunit.utils.processor.CreateMockMethodReferenceProcessor
import org.apache.commons.lang3.StringUtils
import java.util.Locale

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
class PhpUnitPluginUtil {
    companion object {
        private val EXTENDS_TEST_CLASSES = arrayOf(
            "\\PHPUnit\\Framework\\TestCase",
            "\\PHPUnit_Framework_TestCase",
            "\\Symfony\\Bundle\\FrameworkBundle\\Test\\WebTestCase",
            "\\Behat\\Behat\\Context\\BehatContext"
        )

        /**
         * Run tests for given element
         *
         * @param psiElement Elements are PhpClass or Method possible context
         */
        fun executeDebugRunner(psiElement: PsiElement) {
            val context = RunConfigurationProducer.getInstance(PhpUnitRuntimeConfigurationProducer::class.java)
                .createConfigurationFromContext(ConfigurationContext(psiElement))

            if (context != null) {
                ProgramRunnerUtil.executeConfiguration(
                    context.configurationSettings,
                    DefaultDebugExecutor.getDebugExecutorInstance()
                )
            }
        }

        /**
         * Check if class is possibly a Test class, we just try to find it in local file scope
         * no index access invoked
         *
         * FooTest or on extends eg PHPUnit\Framework\TestCase
         */
        fun isTestClassWithoutIndexAccess(phpClass: PhpClass): Boolean {
            val name = phpClass.name
            if (name.endsWith("Test") || name.endsWith("Context")) {
                return true
            }

            // find "extends" classes
            val superFQN = "\\" + StringUtils.stripStart(phpClass.superFQN, "\\")

            for (extendsTestClass in EXTENDS_TEST_CLASSES) {
                if (extendsTestClass.equals(superFQN, ignoreCase = true)) {
                    return true
                }
            }

            for (interfaceName in phpClass.interfaceNames) {
                val interfaceNameNormalized = "\\" + StringUtils.stripStart(interfaceName, "\\")
                if (interfaceNameNormalized.equals("\\Behat\\Behat\\Context\\Context", ignoreCase = true)) {
                    return true
                }
            }

            // find somehow inside a project test folder
            val containingFile = phpClass.containingFile
            if (containingFile == null) {
                return false
            }

            val virtualFile = containingFile.virtualFile
            if (virtualFile == null) {
                return false
            }

            for (contentRoot in ProjectRootManager.getInstance(phpClass.project).contentRoots) {
                val relativePath = VfsUtil.getRelativePath(virtualFile, contentRoot, '/')

                if (relativePath == null) {
                    continue
                }

                val relativePathLowerCase = relativePath.lowercase(Locale.getDefault())

                // PhpUnit and Behat folder structure
                if (relativePathLowerCase.contains("/test/") ||
                    relativePathLowerCase.contains("/tests/") ||
                    relativePathLowerCase.contains("/feature/") ||
                    relativePathLowerCase.contains("/features/")
                ) {
                    return true
                }
            }

            return false
        }

        /**
         * $foo = $this->createMock('Foobar')
         * $foo->method('<caret>')
         */
        fun findCreateMockParameterOnParameterScope(psiElement: StringLiteralExpression): String? {
            val parameterList = psiElement.parent
            if (parameterList is ParameterList) {
                val methodReference = parameterList.parent
                if (methodReference is MethodReference && (
                    PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "PHPUnit_Framework_MockObject_MockObject", "method") ||
                        PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "PHPUnit_Framework_MockObject_Builder_InvocationMocker", "method") ||
                        PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "PHPUnit\\Framework\\MockObject\\MockObject", "method") ||
                        PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "PHPUnit\\Framework\\MockObject\\Builder\\InvocationMocker", "method") ||
                        PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "PHPUnit\\Framework\\MockObject\\Stub", "method")
                    )
                ) {
                    return CreateMockMethodReferenceProcessor.createParameter(methodReference)
                }
            }

            return null
        }

        /**
         * Insert "expectException" for given scope (eg method)
         */
        fun insertExpectedException(function: Function, psiElement: PsiElement, exceptionClass: String) {
            val fqn = "\\" + StringUtils.stripStart(exceptionClass, "\\")

            // add scope
            var addScope: PsiElement? = PsiTreeUtil.getPrevSiblingOfType(psiElement, Statement::class.java)
            if (addScope == null) {
                addScope = PsiTreeUtil.getNextSiblingOfType(psiElement, Statement::class.java)
            }

            if (addScope == null) {
                addScope = PsiTreeUtil.getParentOfType(psiElement, Statement::class.java, true, GroupStatement::class.java)
            }

            if (addScope == null) {
                return
            }

            val s = PhpElementsUtil.insertUseIfNecessary(function, fqn)
            val statement = PhpPsiElementFactory.createStatement(function.project, "\$this->expectException(" + (s ?: fqn) + "::class);")

            addScope.parent.addAfter(statement, addScope)
        }

        fun getMockableMethods(project: Project, parameter: String): Collection<LookupElement> {
            val elements: MutableCollection<LookupElement> = ArrayList()

            for (phpClass in PhpIndex.getInstance(project).getAnyByFQN(parameter)) {
                elements.addAll(
                    phpClass.methods
                        .filter { method: Method -> !method.access.isPublic || !method.name.startsWith("__") }
                        .map { method: Method -> PhpLookupElement(method) }
                        .toSet()
                )
            }

            return elements
        }

        fun isCreatePartialMockMethod(parentOfType: MethodReference): Boolean {
            return PhpElementsUtil.isMethodReferenceInstanceOf(parentOfType, "\\PHPUnit\\Framework\\TestCase", "createPartialMock")
                || PhpElementsUtil.isMethodReferenceInstanceOf(parentOfType, "PHPUnit_Framework_TestCase", "createPartialMock")
        }
    }
}
