package de.espend.idea.php.phpunit.utils;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;
import de.espend.idea.php.phpunit.utils.processor.CreateMockMethodReferenceProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see de.espend.idea.php.phpunit.utils.ChainVisitorUtil
 */
public class ChainVisitorUtilTest extends PhpUnitLightCodeInsightFixtureTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("ChainVisitorUtil.php");
    }

    @Override
    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit/utils/fixtures";
    }

    public void testThatChainingVariableIsResolved() {
        assertEquals("Foo", findCreateMockParameter("<?php\n" +
            "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
            "{\n" +
            "   public function foobar()\n" +
            "   {\n" +
            "       $foo = $this->createMock('Foo');\n" +
            "       $foo->method('<caret>')\n" +
            "   }\n" +
            "}"
        ));
    }

    public void testThatChainingMockObjectIsResolved() {
        assertEquals("Foo", findCreateMockParameter("<?php\n" +
            "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
            "{\n" +
            "   public function foobar()\n" +
            "   {\n" +
            "       $foo = $this->createMock('Foo');\n" +
            "       $foo->expects()->method('<caret>')\n" +
            "   }\n" +
            "}"
        ));
    }

    public void testThatChainingFieldClassIsResolved() {
        assertEquals("Foo", findCreateMockParameter("<?php\n" +
            "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
            "{\n" +
            "" +
            "   public function setUp()\n" +
            "   {\n" +
            "       $this->foo = $this->createMock('Foo');\n" +
            "   }\n" +
            "" +
            "   public function foobar()\n" +
            "   {\n" +
            "       $this->foo->method('<caret>');\n" +
            "   }\n" +
            "}"
        ));
    }

    public void testThatChainingFieldClassIsResolved2() {
        assertEquals("Foo", findCreateMockParameter("<?php\n" +
            "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
            "{\n" +
            "" +
            "   public function setUp()\n" +
            "   {\n" +
            "       $this->foo = $this->createMock('Foo');\n" +
            "   }\n" +
            "" +
            "   public function foobar()\n" +
            "   {\n" +
            "       $this->foo->method('<caret>');\n" +
            "   }\n" +
            "}"
        ));
    }

    public void testThatClassConstantIsResolved() {
        assertEquals("Foo", findCreateMockParameter("<?php\n" +
            "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
            "{\n" +
            "   public function foobar()\n" +
            "   {\n" +
            "       $foo = $this->createMock(Foo::class);\n" +
            "       $foo->expects()->method('<caret>')\n" +
            "   }\n" +
            "}"
        ));
    }

    public void testThatFieldConstantIsResolved() {
        assertEquals("Foo", findCreateMockParameter("<?php\n" +
            "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
            "{\n" +
            "   var $foo = Foo::class;\n" +
            "" +
            "   public function foobar()\n" +
            "   {\n" +
            "       $foo = $this->createMock($this->foo);\n" +
            "       $foo->expects()->method('<caret>')\n" +
            "   }\n" +
            "}"
        ));
    }

    @Nullable
    private String findCreateMockParameter(@NotNull String content) {
        myFixture.configureByText(PhpFileType.INSTANCE, content);

        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        MethodReference methodReference = PsiTreeUtil.getParentOfType(psiElement, MethodReference.class);
        assertNotNull(methodReference);

        CreateMockMethodReferenceProcessor processor = new CreateMockMethodReferenceProcessor();
        ChainVisitorUtil.visit(methodReference, processor);

        return processor.getParameter();
    }
}
