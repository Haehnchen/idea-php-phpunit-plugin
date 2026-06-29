package de.espend.idea.php.phpunit.completion;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;
import de.espend.idea.php.phpunit.utils.mockstring.Filter;
import de.espend.idea.php.phpunit.utils.mockstring.FilterFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @see de.espend.idea.php.phpunit.completion.PhpUnitMockStringCompletionContributor
 * @see de.espend.idea.php.phpunit.completion.PhpUnitMockStringCompletionProvider
 */
public class PhpUnitMockStringCompletionContributorTest extends PhpUnitLightCodeInsightFixtureTestCase {
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("common/fixture/PhpUnitMockStringClasses.php");
    }

    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit";
    }

    public void testGetMockBuilderSetMethodsProvidesMethodCompletion() {
        assertCompletionContains(
                PhpFileType.INSTANCE,
                createTestCase(
                        "        $this->getMockBuilder(PhpUnitMockStringTarget::class)->setMethods(['<caret>']);\n"
                ),
                "publicMethod"
        );
    }

    public void testGetMockBuilderSetMethodsSkipsConstructAndDestruct() {
        List<String> lookupStrings = getProviderLookupStrings(createTestCase(
                "        $this->getMockBuilder(PhpUnitMockStringTarget::class)->setMethods(['<caret>']);\n"
        ));

        assertLookupContains(lookupStrings, "publicMethod", "protectedMethod");
        assertLookupDoesNotContain(lookupStrings, "__construct", "__destruct");
    }

    public void testLegacyGetMockSecondParameterProvidesMethodCompletion() {
        List<String> lookupStrings = getProviderLookupStrings(createTestCase(
                "        $this->getMock('PhpUnitMockStringTarget', ['publicMethod', '<caret>']);\n"
        ));

        assertLookupContains(lookupStrings, "protectedMethod");
        assertLookupDoesNotContain(lookupStrings, "publicMethod");
    }

    public void testGetMockClassSecondParameterProvidesMethodCompletion() {
        assertCompletionContains(
                PhpFileType.INSTANCE,
                createTestCase(
                        "        $this->getMockClass(PhpUnitMockStringTarget::class, ['<caret>']);\n"
                ),
                "publicMethod"
        );
    }

    public void testInvocationMockerMethodProvidesConfiguredMethodCompletion() {
        List<String> lookupStrings = getProviderLookupStrings(createTestCase(
                "        $mock = $this->getMockBuilder(PhpUnitMockStringTarget::class)\n" +
                        "            ->setMethods(['publicMethod'])\n" +
                        "            ->getMock();\n" +
                        "        $mock->expects()->method('<caret>');\n"
        ));

        assertLookupContains(lookupStrings, "publicMethod");
        assertLookupDoesNotContain(lookupStrings, "protectedMethod");
    }

    public void testCreateMockAssignmentProvidesNamespacedInvocationCompletion() {
        assertCompletionContains(
                PhpFileType.INSTANCE,
                "<?php\n" +
                        "class PhpUnitMockStringCreateMockCompletionTest extends \\PHPUnit\\Framework\\TestCase\n" +
                        "{\n" +
                        "    public function testCompletion()\n" +
                        "    {\n" +
                        "        $mock = $this->createMock(PhpUnitMockStringTarget::class);\n" +
                        "        $mock->method('<caret>');\n" +
                        "    }\n" +
                        "}\n",
                "publicMethod"
        );
    }

    public void testPhpUnitHelperPropertyCompletionUsesRegisteredContributor() {
        assertCompletionContains(
                PhpFileType.INSTANCE,
                createTestCase(
                        "        PHPUnit_Helper::setProtectedPropertyValue(PhpUnitMockStringTarget::class, '<caret>');\n"
                ),
                "protectedProperty"
        );
    }

    @NotNull
    private List<String> getProviderLookupStrings(@NotNull String configureByText) {
        myFixture.configureByText(PhpFileType.INSTANCE, configureByText);

        StringLiteralExpression stringLiteralExpression = findStringLiteralAtCaret();
        Filter filter = FilterFactory.getFilter(stringLiteralExpression);
        assertNotNull("Expected mock string completion filter", filter);

        return new ExposedCompletionProvider()
                .lookupElements(filter)
                .stream()
                .map(LookupElement::getLookupString)
                .collect(Collectors.toList());
    }

    @NotNull
    private StringLiteralExpression findStringLiteralAtCaret() {
        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        StringLiteralExpression stringLiteralExpression = PsiTreeUtil.getParentOfType(psiElement, StringLiteralExpression.class, false);
        if (stringLiteralExpression == null && myFixture.getCaretOffset() > 0) {
            psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset() - 1);
            stringLiteralExpression = PsiTreeUtil.getParentOfType(psiElement, StringLiteralExpression.class, false);
        }

        assertNotNull("Expected caret inside string literal", stringLiteralExpression);
        return stringLiteralExpression;
    }

    @NotNull
    private String createTestCase(@NotNull String body) {
        return "<?php\n" +
                "class PhpUnitMockStringCompletionPhpTest extends \\PHPUnit_Framework_TestCase\n" +
                "{\n" +
                "    public function testCompletion()\n" +
                "    {\n" +
                body +
                "    }\n" +
                "}\n";
    }

    private void assertLookupContains(@NotNull List<String> lookupStrings, @NotNull String... expectedLookupStrings) {
        for (String lookupString : expectedLookupStrings) {
            assertTrue(
                    String.format("Expected completion to contain '%s' in %s", lookupString, lookupStrings),
                    lookupStrings.contains(lookupString)
            );
        }
    }

    private void assertLookupDoesNotContain(@NotNull List<String> lookupStrings, @NotNull String... unexpectedLookupStrings) {
        for (String lookupString : unexpectedLookupStrings) {
            assertFalse(
                    String.format("Expected completion not to contain '%s' in %s", lookupString, lookupStrings),
                    lookupStrings.contains(lookupString)
            );
        }
    }

    private static class ExposedCompletionProvider extends PhpUnitMockStringCompletionProvider {
        @NotNull
        private List<LookupElement> lookupElements(@NotNull Filter filter) {
            return super.getLookupElements(filter);
        }
    }
}
