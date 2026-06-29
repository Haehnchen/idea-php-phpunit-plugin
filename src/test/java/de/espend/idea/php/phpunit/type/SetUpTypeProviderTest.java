package de.espend.idea.php.phpunit.type;

import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see de.espend.idea.php.phpunit.type.SetUpTypeProvider
 */
public class SetUpTypeProviderTest extends PhpUnitLightCodeInsightFixtureTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("classes.php");
        myFixture.copyFileToProject("ProphecyTypeProvider.php");
        myFixture.copyFileToProject("SetUpTypeProvider.php");
    }

    @Override
    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit/type/fixtures";
    }

    public void testThatSetUpTypesForFieldReferencesAreProvided() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php" +
            "    class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
            "    {\n" +
            "        public function setUp()\n" +
            "        {\n" +
            "            $this->fake = $this->prophesize(\\Bar::class);\n" +
            "        }\n" +
            "\n" +
            "        public function itShouldDoFoobar()\n" +
            "        {\n" +
            "            $this->fake->getFo<caret>obar();\n" +
            "        }\n" +
            "    }",
            PlatformPatterns.psiElement(Method.class).withName("getFoobar")
        );
    }

    public void testThatSetUpTypesForFieldReferencesAreProvidedForCreateMock() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php" +
                "    class FooBarTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function setUp()\n" +
                "        {\n" +
                "            $this->fake = $this->createMock(\\Bar::class);\n" +
                "        }\n" +
                "\n" +
                "        public function itShouldDoFoobar()\n" +
                "        {\n" +
                "            $this->fake->getFo<caret>obar();\n" +
                "        }\n" +
                "    }",
            PlatformPatterns.psiElement(Method.class).withName("getFoobar")
        );
    }

    public void testThatSetUpTypesForFieldReferencesWithMultipleAssignmentsAreProvided() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php" +
                "    class FooBarBarTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function setUp()\n" +
                "        {\n" +
                "            $this->fake = $this->prophesize(\\Bar::class);\n" +
                "        }\n" +
                "\n" +
                "        public function itShouldDoFoobar()\n" +
                "        {\n" +
                "            $this->fake->getFo<caret>obar();\n" +
                "        }\n" +
                "    }",
            PlatformPatterns.psiElement(Method.class).withName("getFoobar")
        );
    }
}
