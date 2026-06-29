package de.espend.idea.php.phpunit.references;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;
import de.espend.idea.php.phpunit.reference.PhpUnitMockStringReference;
import de.espend.idea.php.phpunit.reference.PhpUnitMockStringReferenceProvider;
import org.jetbrains.annotations.NotNull;

/**
 * @see de.espend.idea.php.phpunit.reference.PhpUnitMockStringReferenceContributor
 */
public class PhpUnitMockStringReferenceContributorTest extends PhpUnitLightCodeInsightFixtureTestCase {
    public void testResolvesMethodStringInPhpUnitMockMethod() {
        assertPhpUnitMockStringReferenceResolvesTo("PhpUnitMockStringMethodReferenceTest.php", PHPUNIT_FIXTURE +
                        "class PhpUnitMockStringMethodReferenceTest extends \\PHPUnit_Framework_TestCase\n" +
                        "{\n" +
                        "    public function testReference()\n" +
                        "    {\n" +
                        "        $mock = $this->getMock(PhpUnitMockStringReferenceSubject::class);\n" +
                        "        $mock->method('target<caret>Method');\n" +
                        "    }\n" +
                        "}\n",
                PlatformPatterns.psiElement(Method.class).withName("targetMethod")
        );
    }

    public void testResolvesFieldStringViaPhpUnitHelperGetProtectedPropertyValue() {
        assertPhpUnitMockStringReferenceResolvesTo("PhpUnitMockStringFieldReferenceTest.php", PHPUNIT_FIXTURE +
                        "class PhpUnitMockStringFieldReferenceTest extends \\PHPUnit_Framework_TestCase\n" +
                        "{\n" +
                        "    public function testReference()\n" +
                        "    {\n" +
                        "        \\PHPUnit_Helper::getProtectedPropertyValue(PhpUnitMockStringReferenceSubject::class, 'secret<caret>Value');\n" +
                        "    }\n" +
                        "}\n",
                PlatformPatterns.psiElement(Field.class).withName("secretValue")
        );
    }

    public void testResolvesFieldStringViaPhpUnitHelperSetProtectedPropertyValue() {
        assertPhpUnitMockStringReferenceResolvesTo("PhpUnitMockStringSetFieldReferenceTest.php", PHPUNIT_FIXTURE +
                        "class PhpUnitMockStringSetFieldReferenceTest extends \\PHPUnit_Framework_TestCase\n" +
                        "{\n" +
                        "    public function testReference()\n" +
                        "    {\n" +
                        "        \\PHPUnit_Helper::setProtectedPropertyValue(PhpUnitMockStringReferenceSubject::class, 'secret<caret>Value');\n" +
                        "    }\n" +
                        "}\n",
                PlatformPatterns.psiElement(Field.class).withName("secretValue")
        );
    }

    public void testResolvesMethodStringViaCreateMockAssignment() {
        assertPhpUnitMockStringReferenceResolvesTo("PhpUnitMockStringCreateMockReferenceTest.php", PHPUNIT_FIXTURE +
                        "class PhpUnitMockStringCreateMockReferenceTest extends \\PHPUnit_Framework_TestCase\n" +
                        "{\n" +
                        "    public function testReference()\n" +
                        "    {\n" +
                        "        $mock = $this->createMock(PhpUnitMockStringReferenceSubject::class);\n" +
                        "        $mock->method('target<caret>Method');\n" +
                        "    }\n" +
                        "}\n",
                PlatformPatterns.psiElement(Method.class).withName("targetMethod")
        );
    }

    public void testDoesNotProvideReferenceOutsidePhpUnitTestFile() {
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
                "    public function method($constraint)\n" +
                "    {\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class PhpUnitMockStringNonTest\n" +
                "{\n" +
                "    public function run()\n" +
                "    {\n" +
                "        $mock = new PHPUnit_Framework_MockObject_MockObject();\n" +
                "        $mock->method('target<caret>Method');\n" +
                "    }\n" +
                "}\n"
        );
    }

    public void testDoesNotProvideReferenceForFreeStringInPhpUnitTestFile() {
        assertNoPhpUnitMockStringReference("PhpUnitMockStringFreeStringTest.php", PHPUNIT_FIXTURE +
                "class PhpUnitMockStringFreeStringTest extends \\PHPUnit_Framework_TestCase\n" +
                "{\n" +
                "    public function testReference()\n" +
                "    {\n" +
                "        $value = 'target<caret>Method';\n" +
                "    }\n" +
                "}\n"
        );
    }

    private void assertPhpUnitMockStringReferenceResolvesTo(String fileName, String configureByText, ElementPattern<?> pattern) {
        myFixture.configureByText(fileName, configureByText);

        PsiElement resolve = findPhpUnitMockStringReferenceAtCaret().resolve();
        if (!pattern.accepts(resolve)) {
            fail(String.format("failed pattern matches element of '%s'", resolve == null ? "null" : resolve.toString()));
        }

        assertTrue(pattern.accepts(resolve));
    }

    private void assertNoPhpUnitMockStringReference(String fileName, String configureByText) {
        myFixture.configureByText(fileName, configureByText);

        StringLiteralExpression stringLiteralExpression = findStringLiteralAtCaret();
        for (PsiReference reference : getPhpUnitMockStringReferences(stringLiteralExpression)) {
            if (reference instanceof PhpUnitMockStringReference) {
                fail("Element should not have a mock string reference.");
            }
        }
    }

    @NotNull
    private PhpUnitMockStringReference findPhpUnitMockStringReferenceAtCaret() {
        StringLiteralExpression stringLiteralExpression = findStringLiteralAtCaret();
        for (PsiReference reference : getPhpUnitMockStringReferences(stringLiteralExpression)) {
            if (reference instanceof PhpUnitMockStringReference) {
                return (PhpUnitMockStringReference) reference;
            }
        }

        fail("Element does not have a mock string reference.");
        throw new AssertionError("Element does not have a mock string reference.");
    }

    private PsiReference @NotNull [] getPhpUnitMockStringReferences(StringLiteralExpression stringLiteralExpression) {
        return new PhpUnitMockStringReferenceProvider().getReferencesByElement(stringLiteralExpression, new ProcessingContext());
    }

    private StringLiteralExpression findStringLiteralAtCaret() {
        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        StringLiteralExpression stringLiteralExpression = PsiTreeUtil.getParentOfType(psiElement, StringLiteralExpression.class);
        if (stringLiteralExpression == null) {
            fail("Element is not a string literal.");
        }

        return stringLiteralExpression;
    }

    private static final String PHPUNIT_FIXTURE = "<?php\n" +
            "class PhpUnitMockStringReferenceSubject\n" +
            "{\n" +
            "    protected $secretValue;\n" +
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
            "    protected function getMock($originalClassName, array $methods = array())\n" +
            "    {\n" +
            "        return new PHPUnit_Framework_MockObject_MockObject();\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * @return PHPUnit_Framework_MockObject_MockObject\n" +
            "     */\n" +
            "    protected function createMock($originalClassName)\n" +
            "    {\n" +
            "        return new PHPUnit_Framework_MockObject_MockObject();\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "class PHPUnit_Framework_MockObject_MockObject\n" +
            "{\n" +
            "    public function method($constraint)\n" +
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
            "    public static function getProtectedPropertyValue($className, $propertyName)\n" +
            "    {\n" +
            "    }\n" +
            "\n" +
            "    public static function setProtectedPropertyValue($className, $propertyName, $value = null)\n" +
            "    {\n" +
            "    }\n" +
            "}\n";
}
