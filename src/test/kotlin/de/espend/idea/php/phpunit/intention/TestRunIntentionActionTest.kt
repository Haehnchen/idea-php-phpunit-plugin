package de.espend.idea.php.phpunit.intention

import com.intellij.codeInsight.intention.IntentionManager
import com.jetbrains.php.lang.PhpFileType
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see TestRunIntentionAction
 */
class TestRunIntentionActionTest : PhpUnitLightCodeInsightFixtureTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("classes.php")
    }

    override fun getTestDataPath(): String {
        return "src/test/java/de/espend/idea/php/phpunit/intention/fixtures"
    }

    fun testThatIntentionIsAvailableForClass() {
        assertIntentionIsAvailable(
            PhpFileType.INSTANCE,
            "<?php\n class Foo extends \\PHPUnit\\Framework\\TestCase {<caret>}",
            "PHPUnit: Run Test"
        )
    }

    fun testThatIntentionIsAvailableForMethod() {
        assertIntentionIsAvailable(
            PhpFileType.INSTANCE,
            "<?php\n class Foo extends \\PHPUnit\\Framework\\TestCase { function testFoo() { <caret>} }",
            "PHPUnit: Run Test"
        )
    }

    fun testThatIntentionIsNotAvailableForNonTestClass() {
        assertIntentionIsNotAvailable(
            "<?php\n class Foo { function testFoo() { <caret>} }",
            "PHPUnit: Run Test"
        )
    }

    private fun assertIntentionIsNotAvailable(configureByText: String, intentionText: String) {
        myFixture.configureByText(PhpFileType.INSTANCE, configureByText)
        val psiElement = myFixture.file.findElementAt(myFixture.caretOffset)!!

        for (intentionAction in IntentionManager.getInstance().intentionActions) {
            if (!intentionAction.isAvailable(project, editor, psiElement.containingFile)) {
                continue
            }

            if (intentionText == intentionAction.text) {
                fail("Intention action '$intentionText' should not be available in element '${psiElement.text}'")
            }
        }
    }
}
