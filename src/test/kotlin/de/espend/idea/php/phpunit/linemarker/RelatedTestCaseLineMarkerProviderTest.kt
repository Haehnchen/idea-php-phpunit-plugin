package de.espend.idea.php.phpunit.linemarker

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.daemon.LineMarkerProviders
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementVisitor
import com.jetbrains.php.lang.psi.PhpPsiElementFactory
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase.LineMarker.ToolTipEqualsAssert

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see RelatedTestCaseLineMarkerProvider
 */
class RelatedTestCaseLineMarkerProviderTest : PhpUnitLightCodeInsightFixtureTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("RelatedTestCaseLineMarkerProvider.php")
    }

    override fun getTestDataPath(): String {
        return "src/test/java/de/espend/idea/php/phpunit/linemarker/fixtures"
    }

    fun testThatClassNameProvidesALineMarkerToItsTestCase() {
        val psiFileFromText = PhpPsiElementFactory.createPsiFileFromText(
            project,
            "<?php\n" +
                "namespace Foo\\Bar\\Car;\n" +
                "class Foobar {}\n"
        )

        assertLineMarker(psiFileFromText, ToolTipEqualsAssert("Navigate to Test Class"))
    }

    fun testThatDirectSiblingTestClassProvidesALineMarker() {
        val psiFileFromText = PhpPsiElementFactory.createPsiFileFromText(
            project,
            "<?php\n" +
                "namespace Foo\\Bar\\Car;\n" +
                "class Direct {}\n"
        )

        assertLineMarker(psiFileFromText, ToolTipEqualsAssert("Navigate to Test Class"))
    }

    fun testThatTestsNamespaceProvidesALineMarker() {
        val psiFileFromText = PhpPsiElementFactory.createPsiFileFromText(
            project,
            "<?php\n" +
                "namespace Foo\\Bar\\Car;\n" +
                "class TestsNamespace {}\n"
        )

        assertLineMarker(psiFileFromText, ToolTipEqualsAssert("Navigate to Test Class"))
    }

    fun testThatTestClassDoesNotProvideALineMarker() {
        val psiFileFromText = PhpPsiElementFactory.createPsiFileFromText(
            project,
            "<?php\n" +
                "namespace Foo\\Bar\\Car;\n" +
                "class DirectTest extends \\PHPUnit\\Framework\\TestCase {}\n"
        )

        assertNoLineMarker(psiFileFromText)
    }

    fun testThatNonPhpUnitTestClassCandidateDoesNotProvideALineMarker() {
        val psiFileFromText = PhpPsiElementFactory.createPsiFileFromText(
            project,
            "<?php\n" +
                "namespace Foo\\Bar\\Car;\n" +
                "class Fake {}\n"
        )

        assertNoLineMarker(psiFileFromText)
    }

    fun testThatNonPhpUnitFakeTestClassDoesNotProvideALineMarker() {
        val psiFileFromText = PhpPsiElementFactory.createPsiFileFromText(
            project,
            "<?php\n" +
                "namespace Foo\\Bar\\Car;\n" +
                "class FakeTest {}\n"
        )

        assertNoLineMarker(psiFileFromText)
    }

    private fun assertNoLineMarker(psiElement: PsiElement) {
        val elements = collectPsiElementsRecursive(psiElement)

        for (lineMarkerProvider: LineMarkerProvider in LineMarkerProviders.getInstance().allForLanguage(psiElement.language)) {
            val lineMarkerInfos: MutableCollection<LineMarkerInfo<*>> = ArrayList()
            lineMarkerProvider.collectSlowLineMarkers(elements, lineMarkerInfos)

            for (lineMarkerInfo in lineMarkerInfos) {
                if ("Navigate to Test Class" == lineMarkerInfo.lineMarkerTooltip) {
                    fail("Test class should not provide related test case line marker.")
                }
            }
        }
    }

    private fun collectPsiElementsRecursive(psiElement: PsiElement): List<PsiElement> {
        val elements: MutableList<PsiElement> = ArrayList()
        elements.add(psiElement.containingFile)

        psiElement.acceptChildren(object : PsiRecursiveElementVisitor() {
            override fun visitElement(element: PsiElement) {
                elements.add(element)
                super.visitElement(element)
            }
        })

        return elements
    }
}
