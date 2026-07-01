package de.espend.idea.php.phpunit.completion

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase
import de.espend.idea.php.phpunit.utils.mockstring.Filter
import de.espend.idea.php.phpunit.utils.mockstring.FilterFactory

/**
 * @see de.espend.idea.php.phpunit.completion.PhpUnitMockStringCompletionContributor
 * @see de.espend.idea.php.phpunit.completion.PhpUnitMockStringCompletionProvider
 */
class PhpUnitMockStringCompletionContributorTest : PhpUnitLightCodeInsightFixtureTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("common/fixture/PhpUnitMockStringClasses.php")
    }

    override fun getTestDataPath(): String {
        return "src/test/kotlin/de/espend/idea/php/phpunit"
    }

    fun testGetMockBuilderSetMethodsProvidesMethodCompletion() {
        assertCompletionContains(
                PhpFileType.INSTANCE,
                createTestCase(
                        "        \$this->getMockBuilder(PhpUnitMockStringTarget::class)->setMethods(['<caret>']);\n"
                ),
                "publicMethod"
        )
    }

    fun testGetMockBuilderSetMethodsProvidesMethodCompletionInArraySyntax() {
        assertCompletionContains(
                PhpFileType.INSTANCE,
                createTestCase(
                        "        \$this->getMockBuilder(PhpUnitMockStringTarget::class)->setMethods(array('<caret>'));\n"
                ),
                "publicMethod"
        )
    }

    fun testGetMockBuilderSetMethodsSkipsConstructAndDestruct() {
        val lookupStrings = getProviderLookupStrings(createTestCase(
                "        \$this->getMockBuilder(PhpUnitMockStringTarget::class)->setMethods(['<caret>']);\n"
        ))

        assertLookupContains(lookupStrings, "publicMethod", "protectedMethod")
        assertLookupDoesNotContain(lookupStrings, "__construct", "__destruct")
    }

    fun testLegacyGetMockSecondParameterProvidesMethodCompletion() {
        val lookupStrings = getProviderLookupStrings(createTestCase(
                "        \$this->getMock('PhpUnitMockStringTarget', ['publicMethod', '<caret>']);\n"
        ))

        assertLookupContains(lookupStrings, "protectedMethod")
        assertLookupDoesNotContain(lookupStrings, "publicMethod")
    }

    fun testGetMockClassSecondParameterProvidesMethodCompletion() {
        assertCompletionContains(
                PhpFileType.INSTANCE,
                createTestCase(
                        "        \$this->getMockClass(PhpUnitMockStringTarget::class, ['<caret>']);\n"
                ),
                "publicMethod"
        )
    }

    fun testGetMockForAbstractClassSeventhParameterProvidesMethodCompletion() {
        assertCompletionContains(
                PhpFileType.INSTANCE,
                createTestCase(
                        "        \$this->getMockForAbstractClass(PhpUnitMockAbstractTarget::class, [], '', true, true, true, ['<caret>']);\n"
                ),
                "publicMethod"
        )
    }

    fun testGetMockForTraitSeventhParameterProvidesTraitMethodCompletion() {
        assertCompletionContains(
                PhpFileType.INSTANCE,
                createTestCase(
                        "        \$this->getMockForTrait(PhpUnitMockTraitTarget::class, [], '', true, true, true, ['<caret>']);\n"
                ),
                "traitMethod"
        )
    }

    fun testInvocationMockerMethodProvidesConfiguredMethodCompletion() {
        val lookupStrings = getProviderLookupStrings(createTestCase(
                "        \$mock = \$this->getMockBuilder(PhpUnitMockStringTarget::class)\n" +
                        "            ->setMethods(['publicMethod'])\n" +
                        "            ->getMock();\n" +
                        "        \$mock->expects()->method('<caret>');\n"
        ))

        assertLookupContains(lookupStrings, "publicMethod")
        assertLookupDoesNotContain(lookupStrings, "protectedMethod")
    }

    fun testCreateMockAssignmentProvidesNamespacedInvocationCompletion() {
        assertCompletionContains(
                PhpFileType.INSTANCE,
                "<?php\n" +
                        "class PhpUnitMockStringCreateMockCompletionTest extends \\PHPUnit\\Framework\\TestCase\n" +
                        "{\n" +
                        "    public function testCompletion()\n" +
                        "    {\n" +
                        "        \$mock = \$this->createMock(PhpUnitMockStringTarget::class);\n" +
                        "        \$mock->method('<caret>');\n" +
                        "    }\n" +
                        "}\n",
                "publicMethod"
        )
    }

    fun testPhpUnitHelperPropertyCompletionUsesRegisteredContributor() {
        assertCompletionContains(
                PhpFileType.INSTANCE,
                createTestCase(
                        "        PHPUnit_Helper::setProtectedPropertyValue(PhpUnitMockStringTarget::class, '<caret>');\n"
                ),
                "protectedProperty"
        )
    }

    fun testPhpUnitHelperPropertyCompletionSkipsMethods() {
        val lookupStrings = getProviderLookupStrings(createTestCase(
                "        PHPUnit_Helper::setProtectedPropertyValue(PhpUnitMockStringTarget::class, '<caret>');\n"
        ))

        assertLookupContains(lookupStrings, "protectedProperty")
        assertLookupDoesNotContain(lookupStrings, "publicMethod", "protectedMethod")
    }

    fun testPhpUnitHelperCallProtectedMethodCompletionSkipsProperties() {
        val lookupStrings = getProviderLookupStrings(createTestCase(
                "        PHPUnit_Helper::callProtectedMethod(PhpUnitMockStringTarget::class, '<caret>');\n"
        ))

        assertLookupContains(lookupStrings, "publicMethod", "protectedMethod")
        assertLookupDoesNotContain(lookupStrings, "protectedProperty")
    }

    private fun getProviderLookupStrings(configureByText: String): List<String> {
        myFixture.configureByText(PhpFileType.INSTANCE, configureByText)

        var stringLiteralExpression = findStringLiteralAtCaret()
        val filter = FilterFactory.getFilter(stringLiteralExpression)
        assertNotNull("Expected mock string completion filter", filter)

        return ExposedCompletionProvider()
                .lookupElements(filter!!)
                .map { it.lookupString }
    }

    private fun findStringLiteralAtCaret(): StringLiteralExpression {
        var psiElement = myFixture.file.findElementAt(myFixture.caretOffset)
        var stringLiteralExpression = PsiTreeUtil.getParentOfType(psiElement, StringLiteralExpression::class.java, false)
        if (stringLiteralExpression == null && myFixture.caretOffset > 0) {
            psiElement = myFixture.file.findElementAt(myFixture.caretOffset - 1)
            stringLiteralExpression = PsiTreeUtil.getParentOfType(psiElement, StringLiteralExpression::class.java, false)
        }

        assertNotNull("Expected caret inside string literal", stringLiteralExpression)
        return stringLiteralExpression!!
    }

    private fun createTestCase(body: String): String {
        return "<?php\n" +
                "class PhpUnitMockStringCompletionPhpTest extends \\PHPUnit_Framework_TestCase\n" +
                "{\n" +
                "    public function testCompletion()\n" +
                "    {\n" +
                body +
                "    }\n" +
                "}\n"
    }

    private fun assertLookupContains(lookupStrings: List<String>, vararg expectedLookupStrings: String) {
        for (lookupString in expectedLookupStrings) {
            assertTrue(
                    "Expected completion to contain '%s' in %s".format(lookupString, lookupStrings),
                    lookupStrings.contains(lookupString)
            )
        }
    }

    private fun assertLookupDoesNotContain(lookupStrings: List<String>, vararg unexpectedLookupStrings: String) {
        for (lookupString in unexpectedLookupStrings) {
            assertFalse(
                    "Expected completion not to contain '%s' in %s".format(lookupString, lookupStrings),
                    lookupStrings.contains(lookupString)
            )
        }
    }

    private class ExposedCompletionProvider : PhpUnitMockStringCompletionProvider() {
        fun lookupElements(filter: Filter): List<LookupElement> {
            return super.getLookupElements(filter)
        }
    }
}
