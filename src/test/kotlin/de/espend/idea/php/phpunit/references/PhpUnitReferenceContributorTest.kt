package de.espend.idea.php.phpunit.references

import com.intellij.patterns.PlatformPatterns
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.elements.Method
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see de.espend.idea.php.phpunit.reference.PhpUnitReferenceContributor
 */
class PhpUnitReferenceContributorTest : PhpUnitLightCodeInsightFixtureTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("PhpUnitReferenceContributor.php")
    }

    override fun getTestDataPath(): String {
        return "src/test/java/de/espend/idea/php/phpunit/references/fixtures"
    }

    fun testThatReferencesForClassMethodAreProvided() {
        assertReferencesMatch(PhpFileType.INSTANCE, "<?php\n" +
            "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
            "{\n" +
            "   public function setUp()\n" +
            "   {\n" +
            "       \$this->foo = \$this->createMock('Foo\\Bar');\n" +
            "   }\n" +
            "   public function foobar()\n" +
            "   {\n" +
            "       \$this->foo->method('getFoo<caret>bar');\n" +
            "   }\n" +
            "}",
            PlatformPatterns.psiElement(Method::class.java).withName("getFoobar")
        )
    }

    fun testThatLocalCreateMockVariableProvidesMethodReferences() {
        assertReferencesMatch(PhpFileType.INSTANCE, "<?php\n" +
            "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
            "{\n" +
            "   public function foobar()\n" +
            "   {\n" +
            "       \$foo = \$this->createMock(\\Foo\\Bar::class);\n" +
            "       \$foo->method('getAlternativeFoo<caret>bar');\n" +
            "   }\n" +
            "}",
            PlatformPatterns.psiElement(Method::class.java).withName("getAlternativeFoobar")
        )
    }

    fun testThatCreateMockInvocationMockerChainProvidesMethodReferences() {
        assertReferencesMatch(PhpFileType.INSTANCE, "<?php\n" +
            "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
            "{\n" +
            "   public function foobar()\n" +
            "   {\n" +
            "       \$foo = \$this->createMock(\\Foo\\Bar::class);\n" +
            "       \$foo->expects()->method('getAlternativeFoo<caret>bar');\n" +
            "   }\n" +
            "}",
            PlatformPatterns.psiElement(Method::class.java).withName("getAlternativeFoobar")
        )
    }

    fun testThatCreatePartialMockVariableProvidesMethodReferences() {
        assertReferencesMatch(PhpFileType.INSTANCE, "<?php\n" +
            "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
            "{\n" +
            "   public function foobar()\n" +
            "   {\n" +
            "       \$foo = \$this->createPartialMock(\\Foo\\Bar::class, ['getFoobar']);\n" +
            "       \$foo->method('getAlternativeFoo<caret>bar');\n" +
            "   }\n" +
            "}",
            PlatformPatterns.psiElement(Method::class.java).withName("getAlternativeFoobar")
        )
    }

    fun testThatLegacyCreateMockVariableProvidesMethodReferences() {
        assertReferencesMatch(PhpFileType.INSTANCE, "<?php\n" +
            "class Foo extends \\PHPUnit_Framework_TestCase\n" +
            "{\n" +
            "   public function foobar()\n" +
            "   {\n" +
            "       \$foo = \$this->createMock('Foo\\Bar');\n" +
            "       \$foo->method('getAlternativeFoo<caret>bar');\n" +
            "   }\n" +
            "}",
            PlatformPatterns.psiElement(Method::class.java).withName("getAlternativeFoobar")
        )
    }
}
