package de.espend.idea.php.phpunit.type

import com.intellij.patterns.PlatformPatterns
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.elements.Method
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see de.espend.idea.php.phpunit.type.ProphecyTypeProvider
 */
class ProphecyTypeProviderTest : PhpUnitLightCodeInsightFixtureTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("ProphecyTypeProvider.php")
    }

    override fun getTestDataPath(): String {
        return "src/test/kotlin/de/espend/idea/php/phpunit/type/fixtures"
    }

    fun testThatProphesizeForVariableIsResolved() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
            "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
            "    {\n" +
            "        public function testFoobar()\n" +
            "        {\n" +
            "            \$foo = \$this->prophesize(Foo::class);\n" +
            "            \$foo->getBar()->will<caret>Return();\n" +
            "        }\n" +
            "    }",
            PlatformPatterns.psiElement(Method::class.java).withName("willReturn")
        )
    }

    fun testThatProphesizeForVariableIsResolvedForClosure() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            \$foo = \$this->prophesize(Foo::class);\n" +
                "            \$closure = function() use (\$class) {\n" +
                "               \$foo->getBar()->will<caret>Return();\n" +
                "            };" +
                "        }\n" +
                "    }",
            PlatformPatterns.psiElement(Method::class.java).withName("willReturn")
        )
    }

    fun testThatProphesizeForVariableInPropertyIsResolved() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function setUp()\n" +
                "        {\n" +
                "            \$this->foo = \$this->prophesize(Foo::class);\n" +
                "        }\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            \$this->foo->getBar()->will<caret>Return();\n" +
                "        }\n" +
                "    }",
            PlatformPatterns.psiElement(Method::class.java).withName("willReturn")
        )
    }

    fun testThatProphesizeForVariableWithStringClassIsResolved() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            \$foo = \$this->prophesize('Foo');\n" +
                "            \$foo->getBar()->will<caret>Return();\n" +
                "        }\n" +
                "    }",
            PlatformPatterns.psiElement(Method::class.java).withName("willReturn")
        )
    }

    fun testThatProphesizeForVariableIsNotResolvedForUnknownMethods() {
        assertPhpReferenceNotResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            \$foo = \$this->prophesize(Foo::class);\n" +
                "            \$foo->unknown()->will<caret>Return();\n" +
                "        }\n" +
                "    }",
            PlatformPatterns.psiElement()
        )
    }

    fun testThatProphesizeForMethodReferenceIsResolved() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            \$foo = \$this->prophesize(Foo::class)->getBar()->will<caret>Return();\n" +
                "        }\n" +
                "    }",
            PlatformPatterns.psiElement(Method::class.java).withName("willReturn")
        )
    }
}
