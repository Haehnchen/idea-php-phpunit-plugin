package de.espend.idea.php.phpunit.completion;

import com.intellij.psi.PsiElement;
import com.intellij.util.ThreeState;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

/**
 * @see de.espend.idea.php.phpunit.completion.PhpUnitMockStringCompletionConfidence
 */
public class PhpUnitMockStringCompletionConfidenceTest extends PhpUnitLightCodeInsightFixtureTestCase {
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("common/fixture/PhpUnitMockStringClasses.php");
    }

    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit";
    }

    public void testShouldNotSkipAutopopupInsidePhpUnitStringParameter() {
        myFixture.configureByText(
                "PhpUnitMockStringCompletionConfidenceTest.php",
                createTestCase(
                        "        $this->getMock('PhpUnitMockStringTarget', ['<caret>']);\n"
                )
        );

        assertSame(ThreeState.NO, shouldSkipAutopopupAtCaret());
    }

    public void testShouldNotSkipAutopopupInsidePhpUnitFileOutsideCompletionStringScope() {
        myFixture.configureByText(
                "PhpUnitMockStringCompletionConfidenceTest.php",
                createTestCase(
                        "        $this<caret>;\n"
                )
        );

        assertSame(ThreeState.NO, new PhpUnitMockStringCompletionConfidence().shouldSkipAutopopup(
                getEditor(),
                myFixture.getFile(),
                myFixture.getFile(),
                myFixture.getCaretOffset()
        ));
    }

    public void testShouldBeUnsureOutsidePhpUnitTestFile() {
        myFixture.configureByText(
                "PhpUnitMockStringCompletionPlain.php",
                "<?php\n" +
                        "class PhpUnitMockStringCompletionPlain\n" +
                        "{\n" +
                        "    public function run()\n" +
                        "    {\n" +
                        "        foo('<caret>');\n" +
                        "    }\n" +
                        "}\n"
        );

        assertSame(ThreeState.UNSURE, shouldSkipAutopopupAtCaret());
    }

    @NotNull
    private ThreeState shouldSkipAutopopupAtCaret() {
        PsiElement psiElement = findContextElementAtCaret();
        return new PhpUnitMockStringCompletionConfidence().shouldSkipAutopopup(
                getEditor(),
                psiElement,
                myFixture.getFile(),
                myFixture.getCaretOffset()
        );
    }

    @NotNull
    private PsiElement findContextElementAtCaret() {
        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        if (psiElement == null && myFixture.getCaretOffset() > 0) {
            psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset() - 1);
        }

        assertNotNull("Expected PSI element at caret", psiElement);
        return psiElement;
    }

    @NotNull
    private String createTestCase(@NotNull String body) {
        return "<?php\n" +
                "class PhpUnitMockStringCompletionConfidencePhpTest extends \\PHPUnit_Framework_TestCase\n" +
                "{\n" +
                "    public function testCompletion()\n" +
                "    {\n" +
                body +
                "    }\n" +
                "}\n";
    }
}
