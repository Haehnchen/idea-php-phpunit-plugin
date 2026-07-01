package de.espend.idea.php.phpunit.linemarker;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.codeInsight.daemon.LineMarkerProviders;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase.LineMarker.ToolTipEqualsAssert;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see de.espend.idea.php.phpunit.linemarker.RelatedTestCaseLineMarkerProvider
 */
public class RelatedTestCaseLineMarkerProviderTest extends PhpUnitLightCodeInsightFixtureTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("RelatedTestCaseLineMarkerProvider.php");
    }

    @Override
    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit/linemarker/fixtures";
    }

    public void testThatClassNameProvidesALineMarkerToItsTestCase() {
        PsiFile psiFileFromText = PhpPsiElementFactory.createPsiFileFromText(getProject(), "<?php\n" +
            "namespace Foo\\Bar\\Car;\n" +
            "class Foobar {}\n"
        );

        assertLineMarker(psiFileFromText, new ToolTipEqualsAssert("Navigate to Test Class"));
    }

    public void testThatDirectSiblingTestClassProvidesALineMarker() {
        PsiFile psiFileFromText = PhpPsiElementFactory.createPsiFileFromText(getProject(), "<?php\n" +
            "namespace Foo\\Bar\\Car;\n" +
            "class Direct {}\n"
        );

        assertLineMarker(psiFileFromText, new ToolTipEqualsAssert("Navigate to Test Class"));
    }

    public void testThatTestsNamespaceProvidesALineMarker() {
        PsiFile psiFileFromText = PhpPsiElementFactory.createPsiFileFromText(getProject(), "<?php\n" +
            "namespace Foo\\Bar\\Car;\n" +
            "class TestsNamespace {}\n"
        );

        assertLineMarker(psiFileFromText, new ToolTipEqualsAssert("Navigate to Test Class"));
    }

    public void testThatTestClassDoesNotProvideALineMarker() {
        PsiFile psiFileFromText = PhpPsiElementFactory.createPsiFileFromText(getProject(), "<?php\n" +
            "namespace Foo\\Bar\\Car;\n" +
            "class DirectTest extends \\PHPUnit\\Framework\\TestCase {}\n"
        );

        assertNoLineMarker(psiFileFromText);
    }

    public void testThatNonPhpUnitTestClassCandidateDoesNotProvideALineMarker() {
        PsiFile psiFileFromText = PhpPsiElementFactory.createPsiFileFromText(getProject(), "<?php\n" +
            "namespace Foo\\Bar\\Car;\n" +
            "class Fake {}\n"
        );

        assertNoLineMarker(psiFileFromText);
    }

    private void assertNoLineMarker(PsiElement psiElement) {
        List<PsiElement> elements = collectPsiElementsRecursive(psiElement);

        for (LineMarkerProvider lineMarkerProvider : LineMarkerProviders.getInstance().allForLanguage(psiElement.getLanguage())) {
            Collection<LineMarkerInfo<?>> lineMarkerInfos = new ArrayList<>();
            lineMarkerProvider.collectSlowLineMarkers(elements, lineMarkerInfos);

            for (LineMarkerInfo<?> lineMarkerInfo : lineMarkerInfos) {
                if ("Navigate to Test Class".equals(lineMarkerInfo.getLineMarkerTooltip())) {
                    fail("Test class should not provide related test case line marker.");
                }
            }
        }
    }

    private List<PsiElement> collectPsiElementsRecursive(PsiElement psiElement) {
        List<PsiElement> elements = new ArrayList<>();
        elements.add(psiElement.getContainingFile());

        psiElement.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                elements.add(element);
                super.visitElement(element);
            }
        });

        return elements;
    }
}
