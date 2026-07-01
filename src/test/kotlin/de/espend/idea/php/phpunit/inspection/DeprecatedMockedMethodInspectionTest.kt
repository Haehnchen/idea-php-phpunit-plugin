package de.espend.idea.php.phpunit.inspection

import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see DeprecatedMockedMethodInspection
 */
class DeprecatedMockedMethodInspectionTest : PhpUnitLightCodeInsightFixtureTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("DeprecatedMockedMethodInspection.php")
    }

    override fun getTestDataPath(): String {
        return "src/test/kotlin/de/espend/idea/php/phpunit/inspection/fixtures"
    }

    fun testDeprecationForMockedMethodViaCreatePartialMock() {
        assertLocalInspectionContains(
            "test.php",
            "<?php\n" +
                "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
                "{\n" +
                "   public function foobar()\n" +
                "   {\n" +
                "       \$this->createPartialMock(\\Foo\\Bar::class, ['getFoobarD<caret>eprecated']);\n" +
                "   }\n" +
                "}",
            "Method 'getFoobarDeprecated' is deprecated"
        )
    }

    fun testDeprecationForMockedMethodViaCreatePartialMock2() {
        assertLocalInspectionContains(
            "test.php",
            "<?php\n" +
                "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
                "{\n" +
                "   public function foobar()\n" +
                "   {\n" +
                "       \$foo = \$this->createPartialMock(\\Foo\\Bar::class, ['getFoobar']);\n" +
                "       \$foo->method('getFoobarD<caret>eprecated');\n" +
                "   }\n" +
                "}",
            "Method 'getFoobarDeprecated' is deprecated"
        )
    }

    fun testDeprecationForMockedMethodViaCreateMock() {
        assertLocalInspectionContains(
            "test2.php",
            "<?php\n" +
                "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
                "{\n" +
                "   public function foobar()\n" +
                "   {\n" +
                "       \$foo = \$this->createMock(\\Foo\\Bar::class);\n" +
                "       \$foo->method('getFoobarD<caret>eprecated');\n" +
                "   }\n" +
                "}",
            "Method 'getFoobarDeprecated' is deprecated"
        )
    }

    fun testDeprecationForMockedMethodViaCreateMockStringClassName() {
        assertLocalInspectionContains(
            "test2.php",
            "<?php\n" +
                "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
                "{\n" +
                "   public function foobar()\n" +
                "   {\n" +
                "       \$foo = \$this->createMock('Foo\\Bar');\n" +
                "       \$foo->method('getFoobarD<caret>eprecated');\n" +
                "   }\n" +
                "}",
            "Method 'getFoobarDeprecated' is deprecated"
        )
    }

    fun testDeprecationForMockedMethodViaDirectCreateMockChain() {
        assertLocalInspectionContains(
            "test2.php",
            "<?php\n" +
                "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
                "{\n" +
                "   public function foobar()\n" +
                "   {\n" +
                "       \$this->createMock(\\Foo\\Bar::class)->method('getFoobarD<caret>eprecated');\n" +
                "   }\n" +
                "}",
            "Method 'getFoobarDeprecated' is deprecated"
        )
    }

    fun testNonDeprecatedMockedMethodDoesNotWarn() {
        assertDeprecatedMockedMethodInspectionIsClean(
            "<?php\n" +
                "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
                "{\n" +
                "   public function foobar()\n" +
                "   {\n" +
                "       \$foo = \$this->createMock(\\Foo\\Bar::class);\n" +
                "       \$foo->method('getFoobar');\n" +
                "   }\n" +
                "}"
        )
    }

    fun testUnknownMockedMethodDoesNotWarn() {
        assertDeprecatedMockedMethodInspectionIsClean(
            "<?php\n" +
                "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
                "{\n" +
                "   public function foobar()\n" +
                "   {\n" +
                "       \$foo = \$this->createMock(\\Foo\\Bar::class);\n" +
                "       \$foo->method('missingMethod');\n" +
                "   }\n" +
                "}"
        )
    }

    fun testStringClassNameMockedMethodDoesNotWarnForNonDeprecatedMethod() {
        assertDeprecatedMockedMethodInspectionIsClean(
            "<?php\n" +
                "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
                "{\n" +
                "   public function foobar()\n" +
                "   {\n" +
                "       \$foo = \$this->createMock('Foo\\Bar');\n" +
                "       \$foo->method('getFoobar');\n" +
                "   }\n" +
                "}"
        )
    }

    private fun assertDeprecatedMockedMethodInspectionIsClean(content: String) {
        myFixture.configureByText("test.php", content)
        myFixture.enableInspections(DeprecatedMockedMethodInspection())
        myFixture.checkHighlighting(true, false, false, false)
    }
}
