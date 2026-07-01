package de.espend.idea.php.phpunit.type

import com.jetbrains.php.lang.PhpFileType
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see ProphecyArgumentTypeProvider
 */
class ProphecyArgumentTypeProviderTest : PhpUnitLightCodeInsightFixtureTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("ProphecyArgumentTypeProvider.php")
    }

    override fun getTestDataPath(): String {
        return "src/test/kotlin/de/espend/idea/php/phpunit/type/fixtures"
    }

    fun testThatProphecyArgumentsProvideTypesForPrimitives() {
        assertMethodContainsTypes(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            \$foo = \$this->prophesize(Foo::class);\n" +
                "            \$foo->getBar(\\Prophecy\\Argument::a<caret>ny());\n" +
                "        }\n" +
                "    }",
            "\\array"
        )

        assertMethodContainsTypes(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function setUp()\n" +
                "        {\n" +
                "            \$this->foo = \$this->prophesize(Foo::class);\n" +
                "        }\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            \$this->foo->getBar(\\Prophecy\\Argument::a<caret>ny());\n" +
                "        }\n" +
                "    }",
            "\\array"
        )
    }

    fun testThatProphecyArgumentsProvideTypesForClasses() {
        assertMethodContainsTypes(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            \$foo = \$this->prophesize(Foo::class);\n" +
                "            \$foo->getBar(\\Prophecy\\Argument::any(), \\Prophecy\\Argument::a<caret>ny());\n" +
                "        }\n" +
                "    }",
            "\\Foo"
        )
    }

    fun testThatProphecyCeteraProvidesTypesForPrimitives() {
        assertMethodContainsTypes(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            \$foo = \$this->prophesize(Foo::class);\n" +
                "            \$foo->getBar(\\Prophecy\\Argument::ce<caret>tera());\n" +
                "        }\n" +
                "    }",
            "\\array"
        )
    }

    fun testThatProphecyCeteraProvidesTypesForClasses() {
        assertMethodContainsTypes(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            \$foo = \$this->prophesize(Foo::class);\n" +
                "            \$foo->getBar(\\Prophecy\\Argument::any(), \\Prophecy\\Argument::ce<caret>tera());\n" +
                "        }\n" +
                "    }",
            "\\Foo"
        )
    }
}
