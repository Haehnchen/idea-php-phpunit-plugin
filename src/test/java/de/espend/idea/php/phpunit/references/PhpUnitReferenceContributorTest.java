package de.espend.idea.php.phpunit.references;

import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see de.espend.idea.php.phpunit.reference.PhpUnitReferenceContributor
 */
public class PhpUnitReferenceContributorTest extends PhpUnitLightCodeInsightFixtureTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("PhpUnitReferenceContributor.php");
    }

    @Override
    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit/references/fixtures";
    }

    public void testThatReferencesForClassMethodAreProvided() {
        assertReferencesMatch(PhpFileType.INSTANCE, "<?php\n" +
            "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
            "{\n" +
            "   public function setUp()\n" +
            "   {\n" +
            "       $this->foo = $this->createMock('Foo\\Bar');\n" +
            "   }\n" +
            "   public function foobar()\n" +
            "   {\n" +
            "       $this->foo->method('getFoo<caret>bar');\n" +
            "   }\n" +
            "}",
            PlatformPatterns.psiElement(Method.class).withName("getFoobar")
        );
    }

    public void testThatLocalCreateMockVariableProvidesMethodReferences() {
        assertReferencesMatch(PhpFileType.INSTANCE, "<?php\n" +
            "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
            "{\n" +
            "   public function foobar()\n" +
            "   {\n" +
            "       $foo = $this->createMock(\\Foo\\Bar::class);\n" +
            "       $foo->method('getAlternativeFoo<caret>bar');\n" +
            "   }\n" +
            "}",
            PlatformPatterns.psiElement(Method.class).withName("getAlternativeFoobar")
        );
    }

    public void testThatCreateMockInvocationMockerChainProvidesMethodReferences() {
        assertReferencesMatch(PhpFileType.INSTANCE, "<?php\n" +
            "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
            "{\n" +
            "   public function foobar()\n" +
            "   {\n" +
            "       $foo = $this->createMock(\\Foo\\Bar::class);\n" +
            "       $foo->expects()->method('getAlternativeFoo<caret>bar');\n" +
            "   }\n" +
            "}",
            PlatformPatterns.psiElement(Method.class).withName("getAlternativeFoobar")
        );
    }

    public void testThatCreatePartialMockVariableProvidesMethodReferences() {
        assertReferencesMatch(PhpFileType.INSTANCE, "<?php\n" +
            "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
            "{\n" +
            "   public function foobar()\n" +
            "   {\n" +
            "       $foo = $this->createPartialMock(\\Foo\\Bar::class, ['getFoobar']);\n" +
            "       $foo->method('getAlternativeFoo<caret>bar');\n" +
            "   }\n" +
            "}",
            PlatformPatterns.psiElement(Method.class).withName("getAlternativeFoobar")
        );
    }

    public void testThatLegacyCreateMockVariableProvidesMethodReferences() {
        assertReferencesMatch(PhpFileType.INSTANCE, "<?php\n" +
            "class Foo extends \\PHPUnit_Framework_TestCase\n" +
            "{\n" +
            "   public function foobar()\n" +
            "   {\n" +
            "       $foo = $this->createMock('Foo\\Bar');\n" +
            "       $foo->method('getAlternativeFoo<caret>bar');\n" +
            "   }\n" +
            "}",
            PlatformPatterns.psiElement(Method.class).withName("getAlternativeFoobar")
        );
    }
}
