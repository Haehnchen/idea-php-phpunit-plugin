package de.espend.idea.php.phpunit.intention

import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.elements.MethodReference
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see AddMockMethodIntention
 */
class AddMockMethodIntentionTest : PhpUnitLightCodeInsightFixtureTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("classes.php")
    }

    override fun getTestDataPath(): String {
        return "src/test/java/de/espend/idea/php/phpunit/intention/fixtures"
    }

    fun testThatIntentionForChainingIsAvailableWithTopMostParent() {
        assertIntentionIsAvailable(
            PhpFileType.INSTANCE,
            "<?php\n" +
                "/** @var \$x \\PHPUnit\\Framework\\TestCase */\n" +
                "\$x->createMock(\\Foo\\Bar::class)->exp<caret>ects();",
            "PHPUnit: Add mock method"
        )

        assertIntentionIsAvailable(
            PhpFileType.INSTANCE,
            "<?php\n" +
                "/** @var \$x \\PHPUnit\\Framework\\TestCase */\n" +
                "\$x->creat<caret>eMock(\\Foo\\Bar::class);",
            "PHPUnit: Add mock method"
        )

        assertIntentionIsAvailable(
            PhpFileType.INSTANCE,
            "<?php\n" +
                "function testFoo()" +
                "{\n" +
                "  /** @var \$t \\PHPUnit\\Framework\\TestCase */\n" +
                "  \$x = \$t->getMoc<caret>kBuilder(\\Foo\\Bar::class)->getMock()" +
                "}",
            "PHPUnit: Add mock method"
        )

        assertIntentionIsAvailable(
            PhpFileType.INSTANCE,
            "<?php\n" +
                "function testFoo()" +
                "{\n" +
                "  /** @var \$t \\PHPUnit\\Framework\\TestCase */\n" +
                "  \$<caret>x = \$t->getMockBuilder(\\Foo\\Bar::class)->getMock()" +
                "}",
            "PHPUnit: Add mock method"
        )
    }

    fun testThatInspectionIsInvokedForCreateMockWithInlined() {
        myFixture.configureByText(
            PhpFileType.INSTANCE,
            "<?php\n" +
                "/** @var \$x \\PHPUnit\\Framework\\TestCase */\n" +
                "\$x->createMock(\\Foo\\Bar::class)->expec<caret>ts();"
        )

        val text = invokeAndGetText()
        assertTrue(text.contains("\$x->method('getFooBar')->willReturn();"))

        val target = myFixture.file.findElementAt(myFixture.caretOffset)
        assertEquals("willReturn", (target!!.parent as MethodReference).name)
    }

    fun testThatInspectionIsInvokedForCreateMockWithPropertyAccess() {
        myFixture.configureByText(
            PhpFileType.INSTANCE,
            "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function setUp()\n" +
                "        {\n" +
                "            \$this->foo = \$this->createMock(\\Foo\\Bar::class);\n" +
                "        }\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            \$this->foo->fo<caret>ar();\n" +
                "        }\n" +
                "    }"
        )

        val text = invokeAndGetText()
        assertTrue(text.contains("\$this->foo->method('getFooBar')->willReturn();"))

        val target = myFixture.file.findElementAt(myFixture.caretOffset)
        assertEquals("willReturn", (target!!.parent as MethodReference).name)
    }

    fun testThatIntentionForChainingIsAvailableForCreateMock() {
        assertIntentionIsAvailable(
            PhpFileType.INSTANCE,
            "<?php\n" +
                "/** @var \$x \\PHPUnit\\Framework\\TestCase */\n" +
                "\$x->createMock(\\Foo\\Bar::class)->expec<caret>ts();",
            "PHPUnit: Add mock method"
        )

        assertIntentionIsAvailable(
            PhpFileType.INSTANCE,
            "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function setUp()\n" +
                "        {\n" +
                "            \$this->foo = \$this->createMock(\\Foo\\Bar::class);\n" +
                "        }\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            \$this->foo->fo<caret>ar();\n" +
                "        }\n" +
                "    }",
            "PHPUnit: Add mock method"
        )
    }

    fun testThatIntentionForChainingIsAvailableForMockBuilder() {
        assertIntentionIsAvailable(
            PhpFileType.INSTANCE,
            "<?php\n" +
                "function testFoo()" +
                "{\n" +
                "  /** @var \$t \\PHPUnit\\Framework\\TestCase */\n" +
                "  \$x = \$t->getMockBuilder(\\Foo\\Bar::class)->getMock()" +
                "  \$x->bar(<caret>);\n" +
                "}",
            "PHPUnit: Add mock method"
        )

        assertIntentionIsAvailable(
            PhpFileType.INSTANCE,
            "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function setUp()\n" +
                "        {\n" +
                "            \$this->foo = \$this->getMockBuilder(\\Foo\\Bar::class);\n" +
                "        }\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            \$this->foo->getMock()->b<caret>ar();\n" +
                "        }\n" +
                "    }",
            "PHPUnit: Add mock method"
        )
    }

    fun testThatInspectionIsInvokedForMockBuilderWithPropertyAccess() {
        myFixture.configureByText(
            PhpFileType.INSTANCE,
            "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function setUp()\n" +
                "        {\n" +
                "            \$this->foo = \$this->getMockBuilder(\\Foo\\Bar::class)->getMock();\n" +
                "        }\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            \$this->foo->b<caret>ar();\n" +
                "        }\n" +
                "    }"
        )

        val text = invokeAndGetText()
        assertTrue(text.contains("\$this->foo->method('getFooBar')->willReturn();"))

        val target = myFixture.file.findElementAt(myFixture.caretOffset)
        assertEquals("willReturn", (target!!.parent as MethodReference).name)
    }

    fun testThatInspectionIsInvokedForMockBuilderInlined() {
        myFixture.configureByText(
            PhpFileType.INSTANCE,
            "<?php\n" +
                "function testFoo()" +
                "{\n" +
                "  /** @var \$t \\PHPUnit\\Framework\\TestCase */\n" +
                "  \$x = \$t->getMockBuilder(\\Foo\\Bar::class)->getMock()" +
                "  \$x->bar(<caret>);\n" +
                "}"
        )

        val text = invokeAndGetText()

        assertTrue(text.contains("\$x->method('getFooBar')->willReturn();"))

        val target = myFixture.file.findElementAt(myFixture.caretOffset)
        assertEquals("willReturn", (target!!.parent as MethodReference).name)
    }

    private fun invokeAndGetText(): String {
        val psiElement: PsiElement = myFixture.file.findElementAt(myFixture.caretOffset)!!

        AddMockMethodIntention().invoke(project, editor, psiElement)

        return psiElement.containingFile.text
    }
}
