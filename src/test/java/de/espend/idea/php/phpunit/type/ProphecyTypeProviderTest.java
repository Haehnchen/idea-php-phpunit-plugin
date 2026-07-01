package de.espend.idea.php.phpunit.type;

import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see de.espend.idea.php.phpunit.type.ProphecyTypeProvider
 */
public class ProphecyTypeProviderTest extends PhpUnitLightCodeInsightFixtureTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("ProphecyTypeProvider.php");
    }

    @Override
    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit/type/fixtures";
    }

    public void testThatProphesizeForVariableIsResolved() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
            "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
            "    {\n" +
            "        public function testFoobar()\n" +
            "        {\n" +
            "            $foo = $this->prophesize(Foo::class);\n" +
            "            $foo->getBar()->will<caret>Return();\n" +
            "        }\n" +
            "    }",
            PlatformPatterns.psiElement(Method.class).withName("willReturn")
        );
    }

    public void testThatProphesizeForVariableIsResolvedForClosure() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            $foo = $this->prophesize(Foo::class);\n" +
                "            $closure = function() use ($class) {\n" +
                "               $foo->getBar()->will<caret>Return();\n" +
                "            };" +
                "        }\n" +
                "    }",
            PlatformPatterns.psiElement(Method.class).withName("willReturn")
        );
    }

    public void testThatProphesizeForVariableInPropertyIsResolved() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function setUp()\n" +
                "        {\n" +
                "            $this->foo = $this->prophesize(Foo::class);\n" +
                "        }\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            $this->foo->getBar()->will<caret>Return();\n" +
                "        }\n" +
                "    }",
            PlatformPatterns.psiElement(Method.class).withName("willReturn")
        );
    }

    public void testThatProphesizeForVariableWithStringClassIsResolved() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            $foo = $this->prophesize('Foo');\n" +
                "            $foo->getBar()->will<caret>Return();\n" +
                "        }\n" +
                "    }",
            PlatformPatterns.psiElement(Method.class).withName("willReturn")
        );
    }

    public void testThatProphesizeForVariableIsNotResolvedForUnknownMethods() {
        assertPhpReferenceNotResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            $foo = $this->prophesize(Foo::class);\n" +
                "            $foo->unknown()->will<caret>Return();\n" +
                "        }\n" +
                "    }",
            PlatformPatterns.psiElement()
        );
    }

    public void testThatProphesizeForMethodReferenceIsResolved() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            $foo = $this->prophesize(Foo::class)->getBar()->will<caret>Return();\n" +
                "        }\n" +
                "    }",
            PlatformPatterns.psiElement(Method.class).withName("willReturn")
        );
    }
}
