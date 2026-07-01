package de.espend.idea.php.phpunit.annotator

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase


class MockeryAnnotatorTest : LightJavaCodeInsightFixtureTestCase() {


    override fun getTestDataPath(): String {
        return "src/test/kotlin/de/espend/idea/php/phpunit"
    }

    fun testNoMethodAnnotation() {
        myFixture.configureByFiles("annotator/fixtures/MockeryAnnotatorNoMethod.php", "common/fixture/MockeryClasses.php")
        myFixture.checkHighlighting(true, false, false, false)
    }

    fun testNoMethodMultipleClassesAnnotation() {
        myFixture.configureByFiles("annotator/fixtures/MockeryAnnotatorNoMethodMultipleClasses.php", "common/fixture/MockeryClasses.php")
        myFixture.checkHighlighting(true, false, false, false)
    }

    fun testPrivateMethodAnnotation() {
        myFixture.configureByFiles("annotator/fixtures/MockeryAnnotatorPrivateMethod.php", "common/fixture/MockeryClasses.php")
        myFixture.checkHighlighting(true, false, false, false)
    }

    fun testProtectedMethodAnnotation() {
        myFixture.configureByFiles("annotator/fixtures/MockeryAnnotatorProtectedMethod.php", "common/fixture/MockeryClasses.php")
        myFixture.checkHighlighting(true, false, false, false)
    }

    fun testArrayScopeAnnotation() {
        myFixture.configureByFiles("annotator/fixtures/MockeryAnnotatorArrayScopes.php", "common/fixture/MockeryClasses.php")
        myFixture.checkHighlighting(true, false, false, false)
    }

    fun testLegacyMethodAnnotation() {
        myFixture.configureByFiles("annotator/fixtures/MockeryAnnotatorLegacyMethods.php", "common/fixture/MockeryClasses.php")
        myFixture.checkHighlighting(true, false, false, false)
    }
}
