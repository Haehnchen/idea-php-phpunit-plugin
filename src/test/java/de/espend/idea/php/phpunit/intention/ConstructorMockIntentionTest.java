package de.espend.idea.php.phpunit.intention;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.PhpFileType;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 *
 * @see de.espend.idea.php.phpunit.intention.ConstructorMockIntention
 */
public class ConstructorMockIntentionTest extends PhpUnitLightCodeInsightFixtureTestCase {
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("ConstructorMockIntention.php");
    }

    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit/intention/fixtures";
    }

    public void testThatMockIsCreatedForEmptyConstructor() {
        myFixture.configureByText(PhpFileType.INSTANCE, "<?php\n" +
            "new \\Foo\\<caret>Bar();"
        );

        String text = invokeAndGetText();

        assertTrue(text.contains("use Bar\\Car;\n"));
        assertTrue(text.contains("use Bar\\Foo;\n"));
        assertTrue(text.contains("new \\Foo\\Bar($this->createMock(Foo::class), $this->createMock(Car::class));"));
    }

    public void testThatMockIsCreatedForEmptyConstructorWithParentConstructor() {
        myFixture.configureByText(PhpFileType.INSTANCE, "<?php\n" +
            "new \\Foo\\FooExte<caret>nds();"
        );

        String text = invokeAndGetText();

        assertTrue(text.contains("use Bar\\Car;\n"));
        assertTrue(text.contains("use Bar\\Foo;\n"));
        assertTrue(text.contains("new \\Foo\\FooExtends($this->createMock(Foo::class), $this->createMock(Car::class));"));
    }

    public void testThatMockIsCreatedForEmptyConstructorWithoutParameterList() {
        myFixture.configureByText(PhpFileType.INSTANCE, "<?php\n" +
            "new \\Foo\\FooExte<caret>nds;"
        );

        String text = invokeAndGetText();

        assertTrue(text.contains("use Bar\\Car;\n"));
        assertTrue(text.contains("use Bar\\Foo;\n"));
        assertTrue(text.contains("new \\Foo\\FooExtends($this->createMock(Foo::class), $this->createMock(Car::class));"));
    }

    public void testThatMockIsCreatedForEmptyConstructorWithParameter() {
        myFixture.configureByText(PhpFileType.INSTANCE, "<?php\n" +
            "new \\Foo\\<caret>BarNext($this->createMock(Foo::class));"
        );

        String text = invokeAndGetText();

        assertTrue(text.contains("use Bar\\Car;\n"));
        assertTrue(text.contains("new \\Foo\\BarNext($this->createMock(Foo::class), $this->createMock(Car::class), $this->createMock(Car::class), $this->createMock(Car::class));"));
    }

    public void testThatMockIsCreatedForEmptyConstructorWithPrimitiveTypes() {
        myFixture.configureByText(PhpFileType.INSTANCE, "<?php\n" +
            "new \\Foo\\<caret>BarPrimitives();"
        );

        String text = invokeAndGetText();

        assertTrue(text.contains("use Bar\\Car;"));

        assertTrue(
            text.contains("new \\Foo\\BarPrimitives('?', -1, true, $this->createMock(Car::class), 0.0, []);")
        );
    }

    public void testThatMockIsCreatedForEmptyConstructorWithParameterAsVariableDeclaration() {
        myFixture.configureByText(PhpFileType.INSTANCE, "<?php\n" +
            "$f<caret>oo = new \\Foo\\BarNext($this->createMock(Foo::class));"
        );

        String text = invokeAndGetText();

        assertTrue(text.contains("use Bar\\Car;"));

        assertTrue(
            text.contains("$foo = new \\Foo\\BarNext($this->createMock(Foo::class), $this->createMock(Car::class), $this->createMock(Car::class), $this->createMock(Car::class));")
        );
    }

    public void testThatIntentionIsAvailableForConstructorContext() {
        assertIntentionIsAvailable(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            $foo = new Fo<caret>obar()\n" +
                "        }\n" +
                "    }",
            "PHPUnit: Add constructor mocks"
        );

        assertIntentionIsAvailable(PhpFileType.INSTANCE, "<?php\n" +
                "class FooTest extends \\PHPUnit\\Framework\\TestCase\n" +
                "    {\n" +
                "        public function testFoobar()\n" +
                "        {\n" +
                "            $fo<caret>o = new Foobar()\n" +
                "        }\n" +
                "    }",
            "PHPUnit: Add constructor mocks"
        );
    }

    private String invokeAndGetText() {
        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        new ConstructorMockIntention().invoke(getProject(), getEditor(), psiElement);

        return psiElement.getContainingFile().getText();
    }
}
