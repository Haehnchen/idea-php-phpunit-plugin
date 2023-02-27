package de.espend.idea.php.phpunit.references;

import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see de.espend.idea.php.phpunit.reference.PhpUnitCreatePartialMock
 */
public class PhpUnitCreatePartialMockTest extends PhpUnitLightCodeInsightFixtureTestCase {
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("PhpUnitCreatePartialMock.php");
    }

    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit/references/fixtures";
    }

    public void testThatReferencesForClassMethodAreProvided() {
        assertReferencesMatch(PhpFileType.INSTANCE, "<?php\n" +
                "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
                "{\n" +
                "   public function foobar()\n" +
                "   {\n" +
                "       $this->createPartialMock(\\Foo\\Bar::class, ['getFoo<caret>bar']);\n" +
                "   }\n" +
                "}",
            PlatformPatterns.psiElement(Method.class).withName("getFoobar")
        );

        assertReferencesMatch(PhpFileType.INSTANCE, "<?php\n" +
            "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
            "{\n" +
            "   public function foobar()\n" +
            "   {\n" +
            "       $this->createPartialMock('Foo\\Bar', ['getFoo<caret>bar']);\n" +
            "   }\n" +
            "}",
            PlatformPatterns.psiElement(Method.class).withName("getFoobar")
        );
    }

    public void testThatChainingCreateMockProvidesMethodCompletion() {
        assertCompletionContains(PhpFileType.INSTANCE, "<?php\n" +
                "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
                "{\n" +
                "   public function foobar()\n" +
                "   {\n" +
                "       $this->createPartialMock(\\Foo\\Bar::class, ['<caret>']);\n" +
                "   }\n" +
                "}",
            "getFoobar"
        );

        assertCompletionContains(PhpFileType.INSTANCE, "<?php\n" +
                "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
                "{\n" +
                "   public function foobar()\n" +
                "   {\n" +
                "       $this->createPartialMock('Foo\\Bar', ['<caret>']);\n" +
                "   }\n" +
                "}",
            "getFoobar"
        );
    }
}