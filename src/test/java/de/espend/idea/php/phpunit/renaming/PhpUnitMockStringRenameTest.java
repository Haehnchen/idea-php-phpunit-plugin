package de.espend.idea.php.phpunit.renaming;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;
import de.espend.idea.php.phpunit.reference.PhpUnitMockStringReference;
import de.espend.idea.php.phpunit.reference.PhpUnitMockStringReferenceProvider;

/**
 * @see de.espend.idea.php.phpunit.reference.PhpUnitMockStringReference
 */
public class PhpUnitMockStringRenameTest extends PhpUnitLightCodeInsightFixtureTestCase {
    public void testRenameAtMethodStringUpdatesOnlyThatStringLiteral() {
        myFixture.configureByText("PhpUnitMockStringRenameTest.php", PHPUNIT_FIXTURE +
                "class PhpUnitMockStringRenameTest extends \\PHPUnit_Framework_TestCase\n" +
                "{\n" +
                "    public function testReference()\n" +
                "    {\n" +
                "        $mock = $this->getMock(PhpUnitMockStringRenameSubject::class);\n" +
                "        $mock->method('target<caret>Method');\n" +
                "        $sameTextOutsideReference = 'targetMethod';\n" +
                "    }\n" +
                "}\n"
        );

        WriteCommandAction.runWriteCommandAction(getProject(), (Runnable) () -> findPhpUnitMockStringReferenceAtCaret().handleElementRename("newName"));

        myFixture.checkResult(PHPUNIT_FIXTURE +
                "class PhpUnitMockStringRenameTest extends \\PHPUnit_Framework_TestCase\n" +
                "{\n" +
                "    public function testReference()\n" +
                "    {\n" +
                "        $mock = $this->getMock(PhpUnitMockStringRenameSubject::class);\n" +
                "        $mock->method('newName');\n" +
                "        $sameTextOutsideReference = 'targetMethod';\n" +
                "    }\n" +
                "}\n"
        );
    }

    private PhpUnitMockStringReference findPhpUnitMockStringReferenceAtCaret() {
        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        StringLiteralExpression stringLiteralExpression = PsiTreeUtil.getParentOfType(psiElement, StringLiteralExpression.class);
        if (stringLiteralExpression == null) {
            fail("Element is not a string literal.");
        }

        PsiReference[] references = new PhpUnitMockStringReferenceProvider().getReferencesByElement(stringLiteralExpression, new ProcessingContext());
        for (PsiReference reference : references) {
            if (reference instanceof PhpUnitMockStringReference) {
                return (PhpUnitMockStringReference) reference;
            }
        }

        fail("Element does not have a mock string reference.");
        return null;
    }

    private static final String PHPUNIT_FIXTURE = "<?php\n" +
            "class PhpUnitMockStringRenameSubject\n" +
            "{\n" +
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
            "}\n";
}
