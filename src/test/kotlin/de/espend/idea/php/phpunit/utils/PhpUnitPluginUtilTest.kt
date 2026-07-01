package de.espend.idea.php.phpunit.utils

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.PhpPsiElementFactory
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.PhpClass
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
class PhpUnitPluginUtilTest : PhpUnitLightCodeInsightFixtureTestCase() {

    fun testIsTestClassWithoutIndexAccess() {
        assertTrue(PhpUnitPluginUtil.isTestClassWithoutIndexAccess(
            PhpPsiElementFactory.createFromText(project, PhpClass::class.java, "<?php class FooTest {}")!!
        ))

        assertTrue(PhpUnitPluginUtil.isTestClassWithoutIndexAccess(
            PhpPsiElementFactory.createFromText(project, PhpClass::class.java, "<?php class FooTest extends \\PHPUnit\\Framework\\TestCase {}")!!
        ))

        assertTrue(PhpUnitPluginUtil.isTestClassWithoutIndexAccess(
            PhpPsiElementFactory.createFromText(project, PhpClass::class.java, "<?php class FooTest extends PHPUnit_Framework_TestCase {}")!!
        ))

        assertTrue(PhpUnitPluginUtil.isTestClassWithoutIndexAccess(
            PhpPsiElementFactory.createFromText(project, PhpClass::class.java, "<?php class FooTest extends \\Symfony\\Bundle\\FrameworkBundle\\Test\\WebTestCase {}")!!
        ))
    }

    fun testInsertExpectedException() {
        val psiFile = myFixture.configureByText("test.php", "<?php\n" +
            "function test()\n" +
            "{\n" +
            "  (new Foo())->foo();" +
            "  <caret>\n" +
            "}\n"
        )

        var psiElement = myFixture.file.findElementAt(myFixture.caretOffset)

        val function = PsiTreeUtil.findChildOfType(psiFile, Function::class.java)

        WriteCommandAction.runWriteCommandAction(project, { PhpUnitPluginUtil.insertExpectedException(function!!, psiElement!!, "Foobar\\Foobar") })

        val text = psiFile.text

        assertTrue(text.contains("use Foobar\\Foobar;"))
        assertTrue(text.contains("\$this->expectException(Foobar::class);"))
    }
}
