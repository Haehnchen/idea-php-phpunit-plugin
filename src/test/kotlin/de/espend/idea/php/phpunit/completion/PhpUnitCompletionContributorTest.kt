package de.espend.idea.php.phpunit.completion

import com.jetbrains.php.lang.PhpFileType
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see de.espend.idea.php.phpunit.completion.PhpUnitCompletionContributor
 */
class PhpUnitCompletionContributorTest : PhpUnitLightCodeInsightFixtureTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("PhpUnitCompletionContributor.php")
    }

    override fun getTestDataPath(): String {
        return "src/test/java/de/espend/idea/php/phpunit/completion/fixtures"
    }

    fun testThatChainingCreateMockProvidesMethodCompletion() {
        assertCompletionContains(PhpFileType.INSTANCE, "<?php\n" +
                "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
                "{\n" +
                "   public function foobar()\n" +
                "   {\n" +
                "       \$foo = \$this->createMock(\\Foo\\Bar::class);\n" +
                "       \$foo->method('<caret>')\n" +
                "   }\n" +
                "}",
            "getFoobar"
        )

        assertCompletionContains(PhpFileType.INSTANCE, "<?php\n" +
                "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
                "{\n" +
                "   public function setUp()\n" +
                "   {\n" +
                "       \$this->foo = \$this->createMock('Foo\\Bar');\n" +
                "   }\n" +
                "   public function foobar()\n" +
                "   {\n" +
                "       \$this->foo->method('<caret>');\n" +
                "   }\n" +
                "}",
            "getFoobar"
        )

        assertCompletionContains(PhpFileType.INSTANCE, "<?php\n" +
                "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
                "{\n" +
                "   public function setUp()\n" +
                "   {\n" +
                "       \$this->foo = \$this->createMock('Foo\\Bar');\n" +
                "   }\n" +
                "   public function foobar()\n" +
                "   {\n" +
                "       \$this->foo->method(null)->method('<caret>');\n" +
                "   }\n" +
                "}",
            "getFoobar"
        )
    }

    fun testThatLocalCreateMockVariableProvidesMethodCompletion() {
        assertCompletionContains(PhpFileType.INSTANCE, "<?php\n" +
                "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
                "{\n" +
                "   public function foobar()\n" +
                "   {\n" +
                "       \$foo = \$this->createMock(\\Foo\\Bar::class);\n" +
                "       \$foo->method('<caret>');\n" +
                "   }\n" +
                "}",
            "getFoobar",
            "getAlternativeFoobar"
        )
    }

    fun testThatCreateMockInvocationMockerChainProvidesMethodCompletion() {
        assertCompletionContains(PhpFileType.INSTANCE, "<?php\n" +
                "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
                "{\n" +
                "   public function foobar()\n" +
                "   {\n" +
                "       \$foo = \$this->createMock(\\Foo\\Bar::class);\n" +
                "       \$foo->expects()->method('<caret>');\n" +
                "   }\n" +
                "}",
            "getFoobar",
            "getAlternativeFoobar"
        )
    }

    fun testThatCreatePartialMockVariableProvidesMethodCompletion() {
        assertCompletionContains(PhpFileType.INSTANCE, "<?php\n" +
                "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
                "{\n" +
                "   public function foobar()\n" +
                "   {\n" +
                "       \$foo = \$this->createPartialMock(\\Foo\\Bar::class, ['getFoobar']);\n" +
                "       \$foo->method('<caret>');\n" +
                "   }\n" +
                "}",
            "getFoobar",
            "getAlternativeFoobar"
        )
    }

    fun testThatLegacyCreateMockVariableProvidesMethodCompletion() {
        assertCompletionContains(PhpFileType.INSTANCE, "<?php\n" +
                "class Foo extends \\PHPUnit_Framework_TestCase\n" +
                "{\n" +
                "   public function foobar()\n" +
                "   {\n" +
                "       \$foo = \$this->createMock('Foo\\Bar');\n" +
                "       \$foo->method('<caret>');\n" +
                "   }\n" +
                "}",
            "getFoobar",
            "getAlternativeFoobar"
        )
    }
}
