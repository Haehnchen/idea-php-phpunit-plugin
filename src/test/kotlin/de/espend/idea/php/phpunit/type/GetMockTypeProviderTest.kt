package de.espend.idea.php.phpunit.type

import com.intellij.patterns.PlatformPatterns
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.elements.Method
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see de.espend.idea.php.phpunit.type.GetMockTypeProvider
 */
class GetMockTypeProviderTest : PhpUnitLightCodeInsightFixtureTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("GetMockBuilderTypeProvider.php")
    }

    override fun getTestDataPath(): String {
        return "src/test/java/de/espend/idea/php/phpunit/type/fixtures"
    }

    fun testGetMockGeneratorProvidesNavigation() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                "/** @var \$t \\PHPUnit\\Framework\\TestCase */\n" +
                "\$x = \$t->getMockBuilder('Foo')->getMock()" +
                "\$x->b<caret>ar();\n",
            PlatformPatterns.psiElement(Method::class.java).withName("bar")
        )
    }

    fun testChainMockGeneratorProvidesNavigation() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
            "/** @var \$t \\PHPUnit\\Framework\\TestCase */\n" +
            "\$x = \$t->getMockBuilder('Foo')" +
            "->disableOriginalConstructor()" +
            "->disableOriginalConstructor()" +
            "->getMock()" +
            "\$x->b<caret>ar();\n",
            PlatformPatterns.psiElement(Method::class.java).withName("bar")
        )
    }

    fun testThatProphesizeForVariableWithStringClassIsResolved() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function setUp()\n" +
                "        {\n" +
                "            \$this->foo = \$this->getMockBuilder(\\Foo::class);\n" +
                "        }\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            \$this->foo->getMock()->b<caret>ar();\n" +
                "        }\n" +
                "    }",
            PlatformPatterns.psiElement(Method::class.java).withName("bar")
        )
    }
}
