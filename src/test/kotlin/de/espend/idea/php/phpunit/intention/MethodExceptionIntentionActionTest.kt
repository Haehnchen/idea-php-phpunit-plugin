package de.espend.idea.php.phpunit.intention

import com.jetbrains.php.lang.PhpFileType
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 *
 * @see MethodExceptionIntentionAction
 */
class MethodExceptionIntentionActionTest : PhpUnitLightCodeInsightFixtureTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("MethodExceptionIntentionAction.php")
    }

    override fun getTestDataPath(): String {
        return "src/test/java/de/espend/idea/php/phpunit/intention/fixtures"
    }

    fun testThatIntentionIsAvailableForConstructorContext() {
        assertIntentionIsAvailable(
            PhpFileType.INSTANCE,
            "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            <caret>\n" +
                "        }\n" +
                "    }",
            "PHPUnit: Expected exception"
        )
    }

    fun testThatIntentionAddsExpectedExceptionForThrownMethodCall() {
        myFixture.configureByText(
            PhpFileType.INSTANCE,
            "<?php\n" +
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
        )

        myFixture.launchAction(myFixture.findSingleIntention("PHPUnit: Expected exception"))

        val text = myFixture.file.text
        assertTrue(text, text.contains("use Foo\\ExpectedException;\n"))
        assertTrue(text, text.contains("\$this->expectException(ExpectedException::class);"))
    }
}
