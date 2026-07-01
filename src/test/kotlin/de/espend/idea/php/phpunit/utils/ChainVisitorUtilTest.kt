package de.espend.idea.php.phpunit.utils

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.elements.MethodReference
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase
import de.espend.idea.php.phpunit.utils.processor.CreateMockMethodReferenceProcessor

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see de.espend.idea.php.phpunit.utils.ChainVisitorUtil
 */
class ChainVisitorUtilTest : PhpUnitLightCodeInsightFixtureTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("ChainVisitorUtil.php")
    }

    override fun getTestDataPath(): String {
        return "src/test/kotlin/de/espend/idea/php/phpunit/utils/fixtures"
    }

    fun testThatChainingVariableIsResolved() {
        assertEquals("Foo", findCreateMockParameter("<?php\n" +
            "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
            "{\n" +
            "   public function foobar()\n" +
            "   {\n" +
            "       \$foo = \$this->createMock('Foo');\n" +
            "       \$foo->method('<caret>')\n" +
            "   }\n" +
            "}"
        ))
    }

    fun testThatChainingMockObjectIsResolved() {
        assertEquals("Foo", findCreateMockParameter("<?php\n" +
            "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
            "{\n" +
            "   public function foobar()\n" +
            "   {\n" +
            "       \$foo = \$this->createMock('Foo');\n" +
            "       \$foo->expects()->method('<caret>')\n" +
            "   }\n" +
            "}"
        ))
    }

    fun testThatChainingFieldClassIsResolved() {
        assertEquals("Foo", findCreateMockParameter("<?php\n" +
            "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
            "{\n" +
            "" +
            "   public function setUp()\n" +
            "   {\n" +
            "       \$this->foo = \$this->createMock('Foo');\n" +
            "   }\n" +
            "" +
            "   public function foobar()\n" +
            "   {\n" +
            "       \$this->foo->method('<caret>');\n" +
            "   }\n" +
            "}"
        ))
    }

    fun testThatChainingFieldClassIsResolved2() {
        assertEquals("Foo", findCreateMockParameter("<?php\n" +
            "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
            "{\n" +
            "" +
            "   public function setUp()\n" +
            "   {\n" +
            "       \$this->foo = \$this->createMock('Foo');\n" +
            "   }\n" +
            "" +
            "   public function foobar()\n" +
            "   {\n" +
            "       \$this->foo->method('<caret>');\n" +
            "   }\n" +
            "}"
        ))
    }

    fun testThatClassConstantIsResolved() {
        assertEquals("Foo", findCreateMockParameter("<?php\n" +
            "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
            "{\n" +
            "   public function foobar()\n" +
            "   {\n" +
            "       \$foo = \$this->createMock(Foo::class);\n" +
            "       \$foo->expects()->method('<caret>')\n" +
            "   }\n" +
            "}"
        ))
    }

    fun testThatFieldConstantIsResolved() {
        assertEquals("Foo", findCreateMockParameter("<?php\n" +
            "class Foo extends \\PHPUnit\\Framework\\TestCase\n" +
            "{\n" +
            "   var \$foo = Foo::class;\n" +
            "" +
            "   public function foobar()\n" +
            "   {\n" +
            "       \$foo = \$this->createMock(\$this->foo);\n" +
            "       \$foo->expects()->method('<caret>')\n" +
            "   }\n" +
            "}"
        ))
    }

    private fun findCreateMockParameter(content: String): String? {
        myFixture.configureByText(PhpFileType.INSTANCE, content)

        var psiElement = myFixture.file.findElementAt(myFixture.caretOffset)
        val methodReference = PsiTreeUtil.getParentOfType(psiElement, MethodReference::class.java)
        assertNotNull(methodReference)

        val processor = CreateMockMethodReferenceProcessor()
        ChainVisitorUtil.visit(methodReference!!, processor)

        return processor.parameter
    }
}
