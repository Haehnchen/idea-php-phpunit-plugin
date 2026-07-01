package de.espend.idea.php.phpunit.references

import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PlatformPatterns
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiReference
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase
import de.espend.idea.php.phpunit.reference.PhpUnitMockStringReference
import de.espend.idea.php.phpunit.reference.PhpUnitMockStringReferenceProvider

/**
 * @see de.espend.idea.php.phpunit.reference.PhpUnitMockStringReferenceContributor
 */
class PhpUnitMockStringReferenceContributorTest : PhpUnitLightCodeInsightFixtureTestCase() {
    fun testResolvesMethodStringInPhpUnitMockMethod() {
        assertPhpUnitMockStringReferenceResolvesTo("PhpUnitMockStringMethodReferenceTest.php", PHPUNIT_FIXTURE +
                        "class PhpUnitMockStringMethodReferenceTest extends \\PHPUnit_Framework_TestCase\n" +
                        "{\n" +
                        "    public function testReference()\n" +
                        "    {\n" +
                        "        \$mock = \$this->getMock(PhpUnitMockStringReferenceSubject::class);\n" +
                        "        \$mock->method('target<caret>Method');\n" +
                        "    }\n" +
                        "}\n",
                PlatformPatterns.psiElement(Method::class.java).withName("targetMethod")
        )
    }

    fun testResolvesFieldStringViaPhpUnitHelperGetProtectedPropertyValue() {
        assertPhpUnitMockStringReferenceResolvesTo("PhpUnitMockStringFieldReferenceTest.php", PHPUNIT_FIXTURE +
                        "class PhpUnitMockStringFieldReferenceTest extends \\PHPUnit_Framework_TestCase\n" +
                        "{\n" +
                        "    public function testReference()\n" +
                        "    {\n" +
                        "        \\PHPUnit_Helper::getProtectedPropertyValue(PhpUnitMockStringReferenceSubject::class, 'secret<caret>Value');\n" +
                        "    }\n" +
                        "}\n",
                PlatformPatterns.psiElement(Field::class.java).withName("secretValue")
        )
    }

    fun testResolvesFieldStringViaPhpUnitHelperSetProtectedPropertyValue() {
        assertPhpUnitMockStringReferenceResolvesTo("PhpUnitMockStringSetFieldReferenceTest.php", PHPUNIT_FIXTURE +
                        "class PhpUnitMockStringSetFieldReferenceTest extends \\PHPUnit_Framework_TestCase\n" +
                        "{\n" +
                        "    public function testReference()\n" +
                        "    {\n" +
                        "        \\PHPUnit_Helper::setProtectedPropertyValue(PhpUnitMockStringReferenceSubject::class, 'secret<caret>Value');\n" +
                        "    }\n" +
                        "}\n",
                PlatformPatterns.psiElement(Field::class.java).withName("secretValue")
        )
    }

    fun testResolvesMethodStringViaCreateMockAssignment() {
        assertPhpUnitMockStringReferenceResolvesTo("PhpUnitMockStringCreateMockReferenceTest.php", PHPUNIT_FIXTURE +
                        "class PhpUnitMockStringCreateMockReferenceTest extends \\PHPUnit_Framework_TestCase\n" +
                        "{\n" +
                        "    public function testReference()\n" +
                        "    {\n" +
                        "        \$mock = \$this->createMock(PhpUnitMockStringReferenceSubject::class);\n" +
                        "        \$mock->method('target<caret>Method');\n" +
                        "    }\n" +
                        "}\n",
                PlatformPatterns.psiElement(Method::class.java).withName("targetMethod")
        )
    }

    fun testReferenceRangeCoversOnlyTheMethodNameInsideQuotes() {
        myFixture.configureByText("PhpUnitMockStringReferenceRangeTest.php", PHPUNIT_FIXTURE +
                "class PhpUnitMockStringReferenceRangeTest extends \\PHPUnit_Framework_TestCase\n" +
                "{\n" +
                "    public function testReference()\n" +
                "    {\n" +
                "        \$mock = \$this->createMock(PhpUnitMockStringReferenceSubject::class);\n" +
                "        \$mock->method('target<caret>Method');\n" +
                "    }\n" +
                "}\n"
        )

        val rangeInElement = findPhpUnitMockStringReferenceAtCaret().getRangeInElement()

        assertEquals(TextRange(1, "'targetMethod'".length - 1), rangeInElement)
    }

    fun testDoesNotProvideReferenceOutsidePhpUnitTestFile() {
        assertNoPhpUnitMockStringReference("PhpUnitMockStringReference.php", "<?php\n" +
                "class PhpUnitMockStringReferenceSubject\n" +
                "{\n" +
                "    public function targetMethod()\n" +
                "    {\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class PHPUnit_Framework_MockObject_MockObject\n" +
                "{\n" +
                "    public function method(\$constraint)\n" +
                "    {\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class PhpUnitMockStringNonTest\n" +
                "{\n" +
                "    public function run()\n" +
                "    {\n" +
                "        \$mock = new PHPUnit_Framework_MockObject_MockObject();\n" +
                "        \$mock->method('target<caret>Method');\n" +
                "    }\n" +
                "}\n"
        )
    }

    fun testDoesNotProvideReferenceForFreeStringInPhpUnitTestFile() {
        assertNoPhpUnitMockStringReference("PhpUnitMockStringFreeStringTest.php", PHPUNIT_FIXTURE +
                "class PhpUnitMockStringFreeStringTest extends \\PHPUnit_Framework_TestCase\n" +
                "{\n" +
                "    public function testReference()\n" +
                "    {\n" +
                "        \$value = 'target<caret>Method';\n" +
                "    }\n" +
                "}\n"
        )
    }

    private fun assertPhpUnitMockStringReferenceResolvesTo(fileName: String, configureByText: String, pattern: ElementPattern<*>) {
        myFixture.configureByText(fileName, configureByText)

        val resolve = findPhpUnitMockStringReferenceAtCaret().resolve()
        if (!pattern.accepts(resolve)) {
            fail(String.format("failed pattern matches element of '%s'", resolve?.toString() ?: "null"))
        }

        assertTrue(pattern.accepts(resolve))
    }

    private fun assertNoPhpUnitMockStringReference(fileName: String, configureByText: String) {
        myFixture.configureByText(fileName, configureByText)

        val stringLiteralExpression = findStringLiteralAtCaret()
        for (reference in getPhpUnitMockStringReferences(stringLiteralExpression)) {
            if (reference is PhpUnitMockStringReference) {
                fail("Element should not have a mock string reference.")
            }
        }
    }

    private fun findPhpUnitMockStringReferenceAtCaret(): PhpUnitMockStringReference {
        val stringLiteralExpression = findStringLiteralAtCaret()
        for (reference in getPhpUnitMockStringReferences(stringLiteralExpression)) {
            if (reference is PhpUnitMockStringReference) {
                return reference
            }
        }

        fail("Element does not have a mock string reference.")
        throw AssertionError("Element does not have a mock string reference.")
    }

    private fun getPhpUnitMockStringReferences(stringLiteralExpression: StringLiteralExpression): Array<PsiReference> {
        return PhpUnitMockStringReferenceProvider().getReferencesByElement(stringLiteralExpression, ProcessingContext())
    }

    private fun findStringLiteralAtCaret(): StringLiteralExpression {
        val psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset())
        return PsiTreeUtil.getParentOfType(psiElement, StringLiteralExpression::class.java) ?: run {
            fail("Element is not a string literal.")
            throw AssertionError("Element is not a string literal.")
        }
    }

    private val PHPUNIT_FIXTURE = "<?php\n" +
            "class PhpUnitMockStringReferenceSubject\n" +
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
            "\n" +
            "    /**\n" +
            "     * @return PHPUnit_Framework_MockObject_MockObject\n" +
            "     */\n" +
            "    protected function createMock(\$originalClassName)\n" +
            "    {\n" +
            "        return new PHPUnit_Framework_MockObject_MockObject();\n" +
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
            "}\n" +
            "\n" +
            "class PHPUnit_Helper\n" +
            "{\n" +
            "    public static function getProtectedPropertyValue(\$className, \$propertyName)\n" +
            "    {\n" +
            "    }\n" +
            "\n" +
            "    public static function setProtectedPropertyValue(\$className, \$propertyName, \$value = null)\n" +
            "    {\n" +
            "    }\n" +
            "}\n"
}
