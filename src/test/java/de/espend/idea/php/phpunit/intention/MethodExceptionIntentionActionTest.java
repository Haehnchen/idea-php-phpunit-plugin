package de.espend.idea.php.phpunit.intention;

import com.jetbrains.php.lang.PhpFileType;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 *
 * @see de.espend.idea.php.phpunit.intention.MethodExceptionIntentionAction
 */
public class MethodExceptionIntentionActionTest extends PhpUnitLightCodeInsightFixtureTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("MethodExceptionIntentionAction.php");
    }

    @Override
    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit/intention/fixtures";
    }

    public void testThatIntentionIsAvailableForConstructorContext() {
        assertIntentionIsAvailable(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            <caret>\n" +
                "        }\n" +
                "    }",
            "PHPUnit: Expected exception"
        );
    }

    public void testThatIntentionAddsExpectedExceptionForThrownMethodCall() {
        myFixture.configureByText(PhpFileType.INSTANCE, "<?php\n" +
            "namespace Tests;\n" +
            "\n" +
            "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
            "{\n" +
            "    public function testFoobar()\n" +
            "    {\n" +
            "        <caret>\n" +
            "        (new \\Foo\\Service())->throwsExpectedException();\n" +
            "    }\n" +
            "}"
        );

        myFixture.launchAction(myFixture.findSingleIntention("PHPUnit: Expected exception"));

        String text = myFixture.getFile().getText();
        assertTrue(text, text.contains("use Foo\\ExpectedException;\n"));
        assertTrue(text, text.contains("$this->expectException(ExpectedException::class);"));
    }
}
