package de.espend.idea.php.phpunit.intention;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see de.espend.idea.php.phpunit.intention.AddMockMethodIntention
 */
public class AddMockMethodIntentionTest extends PhpUnitLightCodeInsightFixtureTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("classes.php");
    }

    @Override
    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit/intention/fixtures";
    }

    public void testThatIntentionForChainingIsAvailableWithTopMostParent() {
        assertIntentionIsAvailable(PhpFileType.INSTANCE, "<?php\n" +
                "/** @var $x \\PHPUnit\\Framework\\TestCase */\n" +
                "$x->createMock(\\Foo\\Bar::class)->exp<caret>ects();",
            "PHPUnit: Add mock method"
        );

        assertIntentionIsAvailable(PhpFileType.INSTANCE, "<?php\n" +
                "/** @var $x \\PHPUnit\\Framework\\TestCase */\n" +
                "$x->creat<caret>eMock(\\Foo\\Bar::class);",
            "PHPUnit: Add mock method"
        );

        assertIntentionIsAvailable(PhpFileType.INSTANCE, "<?php\n" +
                "function testFoo()" +
                "{\n" +
                "  /** @var $t \\PHPUnit\\Framework\\TestCase */\n" +
                "  $x = $t->getMoc<caret>kBuilder(\\Foo\\Bar::class)->getMock()" +
                "}",
            "PHPUnit: Add mock method"
        );

        assertIntentionIsAvailable(PhpFileType.INSTANCE, "<?php\n" +
                "function testFoo()" +
                "{\n" +
                "  /** @var $t \\PHPUnit\\Framework\\TestCase */\n" +
                "  $<caret>x = $t->getMockBuilder(\\Foo\\Bar::class)->getMock()" +
                "}",
            "PHPUnit: Add mock method"
        );
    }

    public void testThatInspectionIsInvokedForCreateMockWithInlined() {
        myFixture.configureByText(PhpFileType.INSTANCE, "<?php\n" +
            "/** @var $x \\PHPUnit\\Framework\\TestCase */\n" +
            "$x->createMock(\\Foo\\Bar::class)->expec<caret>ts();"
        );

        String text = invokeAndGetText();
        assertTrue(text.contains("$x->method('getFooBar')->willReturn();"));

        PsiElement target = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        assertEquals("willReturn", ((MethodReference) target.getParent()).getName());
    }

    public void testThatInspectionIsInvokedForCreateMockWithPropertyAccess() {
        myFixture.configureByText(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function setUp()\n" +
                "        {\n" +
                "            $this->foo = $this->createMock(\\Foo\\Bar::class);\n" +
                "        }\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            $this->foo->fo<caret>ar();\n" +
                "        }\n" +
                "    }"
        );

        String text = invokeAndGetText();
        assertTrue(text.contains("$this->foo->method('getFooBar')->willReturn();"));

        PsiElement target = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        assertEquals("willReturn", ((MethodReference) target.getParent()).getName());
    }

    public void testThatIntentionForChainingIsAvailableForCreateMock() {
        assertIntentionIsAvailable(PhpFileType.INSTANCE, "<?php\n" +
            "/** @var $x \\PHPUnit\\Framework\\TestCase */\n" +
            "$x->createMock(\\Foo\\Bar::class)->expec<caret>ts();",
            "PHPUnit: Add mock method"
        );

        assertIntentionIsAvailable(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function setUp()\n" +
                "        {\n" +
                "            $this->foo = $this->createMock(\\Foo\\Bar::class);\n" +
                "        }\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            $this->foo->fo<caret>ar();\n" +
                "        }\n" +
                "    }",
            "PHPUnit: Add mock method"
        );
    }

    public void testThatIntentionForChainingIsAvailableForMockBuilder() {
        assertIntentionIsAvailable(PhpFileType.INSTANCE, "<?php\n" +
                "function testFoo()" +
                "{\n" +
                "  /** @var $t \\PHPUnit\\Framework\\TestCase */\n" +
                "  $x = $t->getMockBuilder(\\Foo\\Bar::class)->getMock()" +
                "  $x->bar(<caret>);\n" +
                "}",
            "PHPUnit: Add mock method"
        );

        assertIntentionIsAvailable(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function setUp()\n" +
                "        {\n" +
                "            $this->foo = $this->getMockBuilder(\\Foo\\Bar::class);\n" +
                "        }\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            $this->foo->getMock()->b<caret>ar();\n" +
                "        }\n" +
                "    }",
            "PHPUnit: Add mock method"
        );
    }

    public void testThatInspectionIsInvokedForMockBuilderWithPropertyAccess() {
        myFixture.configureByText(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function setUp()\n" +
                "        {\n" +
                "            $this->foo = $this->getMockBuilder(\\Foo\\Bar::class)->getMock();\n" +
                "        }\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            $this->foo->b<caret>ar();\n" +
                "        }\n" +
                "    }"
        );

        String text = invokeAndGetText();
        assertTrue(text.contains("$this->foo->method('getFooBar')->willReturn();"));

        PsiElement target = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        assertEquals("willReturn", ((MethodReference) target.getParent()).getName());
    }

    public void testThatInspectionIsInvokedForMockBuilderInlined() {
        myFixture.configureByText(PhpFileType.INSTANCE, "<?php\n" +
                "function testFoo()" +
                "{\n" +
                "  /** @var $t \\PHPUnit\\Framework\\TestCase */\n" +
                "  $x = $t->getMockBuilder(\\Foo\\Bar::class)->getMock()" +
                "  $x->bar(<caret>);\n" +
                "}"
        );

        String text = invokeAndGetText();

        assertTrue(text.contains("$x->method('getFooBar')->willReturn();"));

        PsiElement target = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        assertEquals("willReturn", ((MethodReference) target.getParent()).getName());
    }

    private String invokeAndGetText() {
        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        new AddMockMethodIntention().invoke(getProject(), getEditor(), psiElement);

        return psiElement.getContainingFile().getText();
    }
}
