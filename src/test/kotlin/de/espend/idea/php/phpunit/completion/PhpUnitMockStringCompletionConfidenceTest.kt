package de.espend.idea.php.phpunit.completion

import com.intellij.psi.PsiElement
import com.intellij.util.ThreeState
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase

/**
 * @see de.espend.idea.php.phpunit.completion.PhpUnitMockStringCompletionConfidence
 */
class PhpUnitMockStringCompletionConfidenceTest : PhpUnitLightCodeInsightFixtureTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("common/fixture/PhpUnitMockStringClasses.php")
    }

    override fun getTestDataPath(): String {
        return "src/test/java/de/espend/idea/php/phpunit"
    }

    fun testShouldNotSkipAutopopupInsidePhpUnitStringParameter() {
        myFixture.configureByText(
                "PhpUnitMockStringCompletionConfidenceTest.php",
                createTestCase(
                        "        \$this->getMock('PhpUnitMockStringTarget', ['<caret>']);\n"
                )
        )

        assertSame(ThreeState.NO, shouldSkipAutopopupAtCaret())
    }

    fun testShouldNotSkipAutopopupInsidePhpUnitFileOutsideCompletionStringScope() {
        myFixture.configureByText(
                "PhpUnitMockStringCompletionConfidenceTest.php",
                createTestCase(
                        "        \$this<caret>;\n"
                )
        )

        assertSame(ThreeState.NO, PhpUnitMockStringCompletionConfidence().shouldSkipAutopopup(
                editor,
                myFixture.file,
                myFixture.file,
                myFixture.caretOffset
        ))
    }

    fun testShouldBeUnsureOutsidePhpUnitTestFile() {
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
        )

        assertSame(ThreeState.UNSURE, shouldSkipAutopopupAtCaret())
    }

    private fun shouldSkipAutopopupAtCaret(): ThreeState {
        var psiElement = findContextElementAtCaret()
        return PhpUnitMockStringCompletionConfidence().shouldSkipAutopopup(
                editor,
                psiElement,
                myFixture.file,
                myFixture.caretOffset
        )
    }

    private fun findContextElementAtCaret(): PsiElement {
        var psiElement = myFixture.file.findElementAt(myFixture.caretOffset)
        if (psiElement == null && myFixture.caretOffset > 0) {
            psiElement = myFixture.file.findElementAt(myFixture.caretOffset - 1)
        }

        assertNotNull("Expected PSI element at caret", psiElement)
        return psiElement!!
    }

    private fun createTestCase(body: String): String {
        return "<?php\n" +
                "class PhpUnitMockStringCompletionConfidencePhpTest extends \\PHPUnit_Framework_TestCase\n" +
                "{\n" +
                "    public function testCompletion()\n" +
                "    {\n" +
                body +
                "    }\n" +
                "}\n"
    }
}
