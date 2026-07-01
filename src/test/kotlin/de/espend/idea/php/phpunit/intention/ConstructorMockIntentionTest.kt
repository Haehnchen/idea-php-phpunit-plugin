package de.espend.idea.php.phpunit.intention

import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.PhpFileType
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 *
 * @see ConstructorMockIntention
 */
class ConstructorMockIntentionTest : PhpUnitLightCodeInsightFixtureTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("ConstructorMockIntention.php")
    }

    override fun getTestDataPath(): String {
        return "src/test/java/de/espend/idea/php/phpunit/intention/fixtures"
    }

    fun testThatMockIsCreatedForEmptyConstructor() {
        myFixture.configureByText(
            PhpFileType.INSTANCE,
            "<?php\n" +
                "new \\Foo\\<caret>Bar();"
        )

        val text = invokeAndGetText()

        assertTrue(text.contains("use Bar\\Car;\n"))
        assertTrue(text.contains("use Bar\\Foo;\n"))
        assertTrue(text.contains("new \\Foo\\Bar(\$this->createMock(Foo::class), \$this->createMock(Car::class));"))
    }

    fun testThatMockIsCreatedForEmptyConstructorWithParentConstructor() {
        myFixture.configureByText(
            PhpFileType.INSTANCE,
            "<?php\n" +
                "new \\Foo\\FooExte<caret>nds();"
        )

        val text = invokeAndGetText()

        assertTrue(text.contains("use Bar\\Car;\n"))
        assertTrue(text.contains("use Bar\\Foo;\n"))
        assertTrue(text.contains("new \\Foo\\FooExtends(\$this->createMock(Foo::class), \$this->createMock(Car::class));"))
    }

    fun testThatMockIsCreatedForEmptyConstructorWithoutParameterList() {
        myFixture.configureByText(
            PhpFileType.INSTANCE,
            "<?php\n" +
                "new \\Foo\\FooExte<caret>nds;"
        )

        val text = invokeAndGetText()

        assertTrue(text.contains("use Bar\\Car;\n"))
        assertTrue(text.contains("use Bar\\Foo;\n"))
        assertTrue(text.contains("new \\Foo\\FooExtends(\$this->createMock(Foo::class), \$this->createMock(Car::class));"))
    }

    fun testThatMockIsCreatedForEmptyConstructorWithParameter() {
        myFixture.configureByText(
            PhpFileType.INSTANCE,
            "<?php\n" +
                "new \\Foo\\<caret>BarNext(\$this->createMock(Foo::class));"
        )

        val text = invokeAndGetText()

        assertTrue(text.contains("use Bar\\Car;\n"))
        assertTrue(text.contains("new \\Foo\\BarNext(\$this->createMock(Foo::class), \$this->createMock(Car::class), \$this->createMock(Car::class), \$this->createMock(Car::class));"))
    }

    fun testThatMockIsCreatedForEmptyConstructorWithPrimitiveTypes() {
        myFixture.configureByText(
            PhpFileType.INSTANCE,
            "<?php\n" +
                "new \\Foo\\<caret>BarPrimitives();"
        )

        val text = invokeAndGetText()

        assertTrue(text.contains("use Bar\\Car;"))

        assertTrue(
            text.contains("new \\Foo\\BarPrimitives('?', -1, true, \$this->createMock(Car::class), 0.0, []);")
        )
    }

    fun testThatMockIsCreatedForEmptyConstructorWithParameterAsVariableDeclaration() {
        myFixture.configureByText(
            PhpFileType.INSTANCE,
            "<?php\n" +
                "\$f<caret>oo = new \\Foo\\BarNext(\$this->createMock(Foo::class));"
        )

        val text = invokeAndGetText()

        assertTrue(text.contains("use Bar\\Car;"))

        assertTrue(
            text.contains("\$foo = new \\Foo\\BarNext(\$this->createMock(Foo::class), \$this->createMock(Car::class), \$this->createMock(Car::class), \$this->createMock(Car::class));")
        )
    }

    fun testThatIntentionIsAvailableForConstructorContext() {
        assertIntentionIsAvailable(
            PhpFileType.INSTANCE,
            "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            \$foo = new Fo<caret>obar()\n" +
                "        }\n" +
                "    }",
            "PHPUnit: Add constructor mocks"
        )

        assertIntentionIsAvailable(
            PhpFileType.INSTANCE,
            "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            \$fo<caret>o = new Foobar()\n" +
                "        }\n" +
                "    }",
            "PHPUnit: Add constructor mocks"
        )

        assertIntentionIsAvailable(
            PhpFileType.INSTANCE,
            "<?php\n" +
                "class FooTest extends \\PHPUnit_Framework_TestCase\n" +
                "    {\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            \$foo = new Fo<caret>obar()\n" +
                "        }\n" +
                "    }",
            "PHPUnit: Add constructor mocks"
        )
    }

    fun testThatRegisteredIntentionCreatesConstructorMocksInTestCase() {
        myFixture.configureByText(
            PhpFileType.INSTANCE,
            "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "{\n" +
                "    public function testFoobar()\n" +
                "    {\n" +
                "        new \\Foo\\<caret>Bar();\n" +
                "    }\n" +
                "}"
        )

        myFixture.launchAction(myFixture.findSingleIntention("PHPUnit: Add constructor mocks"))

        val text = myFixture.file.text
        assertTrue(text.contains("use Bar\\Car;\n"))
        assertTrue(text.contains("use Bar\\Foo;\n"))
        assertTrue(text.contains("new \\Foo\\Bar(\$this->createMock(Foo::class), \$this->createMock(Car::class));"))
    }

    private fun invokeAndGetText(): String {
        val psiElement: PsiElement = myFixture.file.findElementAt(myFixture.caretOffset)!!

        ConstructorMockIntention().invoke(project, editor, psiElement)

        return psiElement.containingFile.text
    }
}
