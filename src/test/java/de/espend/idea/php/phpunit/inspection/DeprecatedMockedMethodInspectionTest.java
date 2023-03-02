package de.espend.idea.php.phpunit.inspection;

import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see DeprecatedMockedMethodInspection
 */
public class DeprecatedMockedMethodInspectionTest extends PhpUnitLightCodeInsightFixtureTestCase {
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("DeprecatedMockedMethodInspection.php");
    }

    @Override
    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit/inspection/fixtures";
    }

    public void testDeprecationForMockedMethodViaCreatePartialMock() {
        assertLocalInspectionContains("test.php", "<?php\n" +
                "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
                "{\n" +
                "   public function foobar()\n" +
                "   {\n" +
                "       $this->createPartialMock(\\Foo\\Bar::class, ['getFoobarD<caret>eprecated']);\n" +
                "   }\n" +
                "}",
            "Method 'getFoobarDeprecated' is deprecated"
        );
    }

    public void testDeprecationForMockedMethodViaCreatePartialMock2() {
        assertLocalInspectionContains("test.php", "<?php\n" +
                "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
                "{\n" +
                "   public function foobar()\n" +
                "   {\n" +
                "       $foo = $this->createPartialMock(\\Foo\\Bar::class, ['getFoobar']);\n" +
                "       $foo->method('getFoobarD<caret>eprecated');\n" +
                "   }\n" +
                "}",
            "Method 'getFoobarDeprecated' is deprecated"
        );
    }

    public void testDeprecationForMockedMethodViaCreateMock() {
        assertLocalInspectionContains("test2.php", "<?php\n" +
                "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
                "{\n" +
                "   public function foobar()\n" +
                "   {\n" +
                "       $foo = $this->createMock(\\Foo\\Bar::class);\n" +
                "       $foo->method('getFoobarD<caret>eprecated');\n" +
                "   }\n" +
                "}",
            "Method 'getFoobarDeprecated' is deprecated"
        );
    }
}
