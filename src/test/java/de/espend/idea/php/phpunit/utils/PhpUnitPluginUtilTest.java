package de.espend.idea.php.phpunit.utils;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.Function;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpUnitPluginUtilTest extends PhpUnitLightCodeInsightFixtureTestCase {

    public void testIsTestClassWithoutIndexAccess() {
        assertTrue(PhpUnitPluginUtil.isTestClassWithoutIndexAccess(
            PhpPsiElementFactory.createFromText(getProject(), PhpClass.class, "<?php class FooTest {}")
        ));

        assertTrue(PhpUnitPluginUtil.isTestClassWithoutIndexAccess(
            PhpPsiElementFactory.createFromText(getProject(), PhpClass.class, "<?php class FooTest extends \\PHPUnit\\Framework\\TestCase {}")
        ));

        assertTrue(PhpUnitPluginUtil.isTestClassWithoutIndexAccess(
            PhpPsiElementFactory.createFromText(getProject(), PhpClass.class, "<?php class FooTest extends PHPUnit_Framework_TestCase {}")
        ));

        assertTrue(PhpUnitPluginUtil.isTestClassWithoutIndexAccess(
            PhpPsiElementFactory.createFromText(getProject(), PhpClass.class, "<?php class FooTest extends \\Symfony\\Bundle\\FrameworkBundle\\Test\\WebTestCase {}")
        ));
    }

    public void testInsertExpectedException() {
        PsiFile psiFile = myFixture.configureByText("test.php", "<?php\n" +
            "function test()\n" +
            "{\n" +
            "  (new Foo())->foo();" +
            "  <caret>\n" +
            "}\n"
        );

        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        Document document = PsiDocumentManager.getInstance(getProject()).getDocument(psiFile);
        Function function = PsiTreeUtil.findChildOfType(psiFile, Function.class);

        WriteCommandAction.runWriteCommandAction(getProject(), () -> PhpUnitPluginUtil.insertExpectedException(document, function, psiElement, "Foobar\\Foobar"));

        String text = psiFile.getText();

        assertTrue(text.contains("use Foobar\\Foobar;"));
        assertTrue(text.contains("$this->expectException(Foobar::class);"));
    }
}
