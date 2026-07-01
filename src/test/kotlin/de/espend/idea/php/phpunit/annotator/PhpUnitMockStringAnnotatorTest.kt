package de.espend.idea.php.phpunit.annotator

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase

class PhpUnitMockStringAnnotatorTest : LightJavaCodeInsightFixtureTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/kotlin/de/espend/idea/php/phpunit"
    }

    fun testPhpUnitMockStringWarnings() {
        myFixture.configureByFiles(
                "annotator/fixtures/PhpUnitMockStringAnnotatorTest.php",
                "common/fixture/PhpUnitMockStringClasses.php"
        )

        myFixture.checkHighlighting(true, false, false, false)
    }
}
