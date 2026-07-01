package de.espend.idea.php.phpunit.intention;

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
}