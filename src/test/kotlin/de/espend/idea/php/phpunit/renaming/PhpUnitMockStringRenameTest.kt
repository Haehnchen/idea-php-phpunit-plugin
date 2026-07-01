package de.espend.idea.php.phpunit.renaming

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase
import de.espend.idea.php.phpunit.reference.PhpUnitMockStringReference
import de.espend.idea.php.phpunit.reference.PhpUnitMockStringReferenceProvider

/**
 * @see de.espend.idea.php.phpunit.reference.PhpUnitMockStringReference
 */
class PhpUnitMockStringRenameTest : PhpUnitLightCodeInsightFixtureTestCase() {
    fun testRenameAtMethodStringUpdatesOnlyThatStringLiteral() {
        myFixture.configureByText("PhpUnitMockStringRenameTest.php", PHPUNIT_FIXTURE +
                "class PhpUnitMockStringRenameTest extends \\PHPUnit_Framework_TestCase\n" +
                "{\n" +
                "    public function testReference()\n" +
                "    {\n" +
                "        \$mock = \$this->getMock(PhpUnitMockStringRenameSubject::class);\n" +
                "        \$mock->method('target<caret>Method');\n" +
                "        \$sameTextOutsideReference = 'targetMethod';\n" +
                "    }\n" +
                "}\n"
        )

        WriteCommandAction.runWriteCommandAction(getProject(), Runnable { findPhpUnitMockStringReferenceAtCaret().handleElementRename("newName") })

        myFixture.checkResult(PHPUNIT_FIXTURE +
                "class PhpUnitMockStringRenameTest extends \\PHPUnit_Framework_TestCase\n" +
                "{\n" +
                "    public function testReference()\n" +
                "    {\n" +
                "        \$mock = \$this->getMock(PhpUnitMockStringRenameSubject::class);\n" +
                "        \$mock->method('newName');\n" +
                "        \$sameTextOutsideReference = 'targetMethod';\n" +
                "    }\n" +
                "}\n"
        )
    }

    fun testRenameAtProtectedPropertyStringUpdatesOnlyThatStringLiteral() {
        myFixture.configureByText("PhpUnitMockStringPropertyRenameTest.php", PHPUNIT_FIXTURE +
                "class PhpUnitMockStringPropertyRenameTest extends \\PHPUnit_Framework_TestCase\n" +
                "{\n" +
                "    public function testReference()\n" +
                "    {\n" +
                "        PHPUnit_Helper::setProtectedPropertyValue(PhpUnitMockStringRenameSubject::class, 'secret<caret>Value');\n" +
                "        \$sameTextOutsideReference = 'secretValue';\n" +
                "    }\n" +
                "}\n"
        )

        WriteCommandAction.runWriteCommandAction(getProject(), Runnable { findPhpUnitMockStringReferenceAtCaret().handleElementRename("newName") })

        myFixture.checkResult(PHPUNIT_FIXTURE +
                "class PhpUnitMockStringPropertyRenameTest extends \\PHPUnit_Framework_TestCase\n" +
                "{\n" +
                "    public function testReference()\n" +
                "    {\n" +
                "        PHPUnit_Helper::setProtectedPropertyValue(PhpUnitMockStringRenameSubject::class, 'newName');\n" +
                "        \$sameTextOutsideReference = 'secretValue';\n" +
                "    }\n" +
                "}\n"
        )
    }

    private fun findPhpUnitMockStringReferenceAtCaret(): PhpUnitMockStringReference {
        val psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset())
        val stringLiteralExpression = PsiTreeUtil.getParentOfType(psiElement, StringLiteralExpression::class.java) ?: run {
            fail("Element is not a string literal.")
            throw AssertionError("Element is not a string literal.")
        }

        val references = PhpUnitMockStringReferenceProvider().getReferencesByElement(stringLiteralExpression, ProcessingContext())
        for (reference in references) {
            if (reference is PhpUnitMockStringReference) {
                return reference
            }
        }

        fail("Element does not have a mock string reference.")
        throw AssertionError("Element does not have a mock string reference.")
    }

    private val PHPUNIT_FIXTURE = "<?php\n" +
            "class PhpUnitMockStringRenameSubject\n" +
            "{\n" +
            "    protected \$secretValue;\n" +
            "\n" +
            "    public function targetMethod()\n" +
            "    {\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "abstract class PHPUnit_Framework_TestCase\n" +
            "{\n" +
            "    /**\n" +
            "     * @return PHPUnit_Framework_MockObject_MockObject\n" +
            "     */\n" +
            "    protected function getMock(\$originalClassName, array \$methods = array())\n" +
            "    {\n" +
            "        return new PHPUnit_Framework_MockObject_MockObject();\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "class PHPUnit_Helper\n" +
            "{\n" +
            "    public static function setProtectedPropertyValue(\$className, \$propertyName, \$value = null)\n" +
            "    {\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "class PHPUnit_Framework_MockObject_MockObject\n" +
            "{\n" +
            "    public function method(\$constraint)\n" +
            "    {\n" +
            "        return new PHPUnit_Framework_MockObject_Builder_InvocationMocker();\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "class PHPUnit_Framework_MockObject_Builder_InvocationMocker\n" +
            "{\n" +
            "}\n"
}
