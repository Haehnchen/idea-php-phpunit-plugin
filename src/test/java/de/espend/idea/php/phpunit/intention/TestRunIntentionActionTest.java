package de.espend.idea.php.phpunit.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.IntentionManager;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.PhpFileType;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see de.espend.idea.php.phpunit.intention.TestRunIntentionAction
 */
public class TestRunIntentionActionTest extends PhpUnitLightCodeInsightFixtureTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("classes.php");
    }

    @Override
    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit/intention/fixtures";
    }

    public void testThatIntentionIsAvailableForClass() {
        assertIntentionIsAvailable(
            PhpFileType.INSTANCE,
            "<?php\n class Foo extends \\PHPUnit\\Framework\\TestCase {<caret>}",
            "PHPUnit: Run Test"
        );
    }

    public void testThatIntentionIsAvailableForMethod() {
        assertIntentionIsAvailable(
            PhpFileType.INSTANCE,
            "<?php\n class Foo extends \\PHPUnit\\Framework\\TestCase { function testFoo() { <caret>} }",
            "PHPUnit: Run Test"
        );
    }

    public void testThatIntentionIsNotAvailableForNonTestClass() {
        assertIntentionIsNotAvailable(
            "<?php\n class Foo { function testFoo() { <caret>} }",
            "PHPUnit: Run Test"
        );
    }

    private void assertIntentionIsNotAvailable(String configureByText, String intentionText) {
        myFixture.configureByText(PhpFileType.INSTANCE, configureByText);
        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        for (IntentionAction intentionAction : IntentionManager.getInstance().getIntentionActions()) {
            if (!intentionAction.isAvailable(getProject(), getEditor(), psiElement.getContainingFile())) {
                continue;
            }

            if (intentionText.equals(intentionAction.getText())) {
                fail(String.format("Intention action '%s' should not be available in element '%s'", intentionText, psiElement.getText()));
            }
        }
    }
}
