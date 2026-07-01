package de.espend.idea.php.phpunit.type

import com.intellij.patterns.PlatformPatterns
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.elements.Method
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see de.espend.idea.php.phpunit.type.RevealProphecyTypeProvider
 */
class RevealProphecyTypeProviderTest : PhpUnitLightCodeInsightFixtureTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("RevealProphecyTypeProvider.php")
    }

    override fun getTestDataPath(): String {
        return "src/test/kotlin/de/espend/idea/php/phpunit/type/fixtures"
    }

    fun testThatRevealIsResolved() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            \$foo = \$this->prophesize(Foo::class);\n" +
                "            \$foo->reveal()->getB<caret>ar();\n" +
                "        }\n" +
                "    }",
            PlatformPatterns.psiElement(Method::class.java).withName("getBar")
        )
    }

    fun testThatRevealIsResolvedForStringClass() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            \$foo = \$this->prophesize('Foo');\n" +
                "            \$foo->reveal()->getB<caret>ar();\n" +
                "        }\n" +
                "    }",
            PlatformPatterns.psiElement(Method::class.java).withName("getBar")
        )
    }

    fun testThatRevealForPropertyIsResolved() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function setUp()\n" +
                "        {\n" +
                "            \$this->foo = \$this->prophesize(Foo::class);\n" +
                "        }\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            \$this->foo->reveal()->getB<caret>ar();\n" +
                "        }\n" +
                "    }",
            PlatformPatterns.psiElement(Method::class.java).withName("getBar")
        )
    }
}
