package de.espend.idea.php.phpunit.type

import com.intellij.patterns.PlatformPatterns
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.elements.Method
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see de.espend.idea.php.phpunit.type.SetUpTypeProvider
 */
class SetUpTypeProviderTest : PhpUnitLightCodeInsightFixtureTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("classes.php")
        myFixture.copyFileToProject("ProphecyTypeProvider.php")
        myFixture.copyFileToProject("SetUpTypeProvider.php")
    }

    override fun getTestDataPath(): String {
        return "src/test/kotlin/de/espend/idea/php/phpunit/type/fixtures"
    }

    fun testThatSetUpTypesForFieldReferencesAreProvided() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php" +
            "    class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
            "    {\n" +
            "        public function setUp()\n" +
            "        {\n" +
            "            \$this->fake = \$this->prophesize(\\Bar::class);\n" +
            "        }\n" +
            "\n" +
            "        public function itShouldDoFoobar()\n" +
            "        {\n" +
            "            \$this->fake->getFo<caret>obar();\n" +
            "        }\n" +
            "    }",
            PlatformPatterns.psiElement(Method::class.java).withName("getFoobar")
        )
    }

    fun testThatSetUpTypesForFieldReferencesAreProvidedForCreateMock() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php" +
                "    class FooBarTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function setUp()\n" +
                "        {\n" +
                "            \$this->fake = \$this->createMock(\\Bar::class);\n" +
                "        }\n" +
                "\n" +
                "        public function itShouldDoFoobar()\n" +
                "        {\n" +
                "            \$this->fake->getFo<caret>obar();\n" +
                "        }\n" +
                "    }",
            PlatformPatterns.psiElement(Method::class.java).withName("getFoobar")
        )
    }

    fun testThatSetUpTypesForFieldReferencesWithMultipleAssignmentsAreProvided() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php" +
                "    class FooBarBarTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function setUp()\n" +
                "        {\n" +
                "            \$this->fake = \$this->prophesize(\\Bar::class);\n" +
                "        }\n" +
                "\n" +
                "        public function itShouldDoFoobar()\n" +
                "        {\n" +
                "            \$this->fake->getFo<caret>obar();\n" +
                "        }\n" +
                "    }",
            PlatformPatterns.psiElement(Method::class.java).withName("getFoobar")
        )
    }

    fun testThatSetUpTypesAreNotProvidedForNonTestClass() {
        assertPhpReferenceNotResolveTo(PhpFileType.INSTANCE, "<?php" +
                "    class FooBarService\n" +
                "    {\n" +
                "        public function setUp()\n" +
                "        {\n" +
                "            \$this->fake = \$this->createMock(\\Bar::class);\n" +
                "        }\n" +
                "\n" +
                "        public function itShouldDoFoobar()\n" +
                "        {\n" +
                "            \$this->fake->getFo<caret>obar();\n" +
                "        }\n" +
                "    }",
            PlatformPatterns.psiElement(Method::class.java).withName("getFoobar")
        )
    }
}
