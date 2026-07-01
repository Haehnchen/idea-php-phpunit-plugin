package de.espend.idea.php.phpunit.type;

import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see de.espend.idea.php.phpunit.type.GetMockTypeProvider
 */
public class GetMockTypeProviderTest extends PhpUnitLightCodeInsightFixtureTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("GetMockBuilderTypeProvider.php");
    }

    @Override
    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit/type/fixtures";
    }

    public void testGetMockGeneratorProvidesNavigation() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                "/** @var $t \\PHPUnit\\Framework\\TestCase */\n" +
                "$x = $t->getMockBuilder('Foo')->getMock()" +
                "$x->b<caret>ar();\n",
            PlatformPatterns.psiElement(Method.class).withName("bar")
        );
    }

    public void testChainMockGeneratorProvidesNavigation() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
            "/** @var $t \\PHPUnit\\Framework\\TestCase */\n" +
            "$x = $t->getMockBuilder('Foo')" +
            "->disableOriginalConstructor()" +
            "->disableOriginalConstructor()" +
            "->getMock()" +
            "$x->b<caret>ar();\n",
            PlatformPatterns.psiElement(Method.class).withName("bar")
        );
    }

    public void testThatProphesizeForVariableWithStringClassIsResolved() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function setUp()\n" +
                "        {\n" +
                "            $this->foo = $this->getMockBuilder(\\Foo::class);\n" +
                "        }\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            $this->foo->getMock()->b<caret>ar();\n" +
                "        }\n" +
                "    }",
            PlatformPatterns.psiElement(Method.class).withName("bar")
        );
    }
}
