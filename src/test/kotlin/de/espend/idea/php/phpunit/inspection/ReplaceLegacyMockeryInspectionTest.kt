package de.espend.idea.php.phpunit.inspection

import com.intellij.ide.util.PropertiesComponent
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase

class ReplaceLegacyMockeryInspectionTest : LightJavaCodeInsightFixtureTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("common/fixture/MockeryClasses.php")
    }

    override fun getTestDataPath(): String {
        return "src/test/java/de/espend/idea/php/phpunit"
    }

    protected fun doTest(testName: String, preferFunctionNotation: Boolean, preferMultipleStatements: Boolean) {
        val testFixturePrefix = "inspection/fixtures/"
        myFixture.configureByFile(testFixturePrefix + testName + ".php")

        val instance = PropertiesComponent.getInstance()
        val oldPreferFunctionNotation = instance.getBoolean("preferFunctionNotation", false)
        val oldPreferMultipleStatements = instance.getBoolean("preferMultipleStatements", false)
        instance.setValue("preferFunctionNotation", preferFunctionNotation)
        instance.setValue("preferMultipleStatements", preferMultipleStatements)

        myFixture.enableInspections(ReplaceLegacyMockeryInspection())
        val highlightInfos = myFixture.doHighlighting()
        assertNotNull(highlightInfos)
        val action = myFixture.findSingleIntention(ReplaceLegacyMockeryInspection.QUICK_FIX_NAME)
        assertNotNull(action)

        myFixture.launchAction(action)
        myFixture.checkResultByFile(testFixturePrefix + testName + ".after.php")

        instance.setValue("preferFunctionNotation", oldPreferFunctionNotation)
        instance.setValue("preferMultipleStatements", oldPreferMultipleStatements)
    }

    fun testShouldReceiveToAllows() {
        doTest("shouldReceiveToAllows", false, false)
        doTest("shouldReceiveToAllows", false, true)
        doTest("shouldReceiveToAllowsFunctional", true, false)
        doTest("shouldReceiveToAllowsFunctional", true, true)
    }

    fun testShouldReceiveAndReturnToAllows() {
        doTest("shouldReceiveAndReturnToAllows", false, false)
        doTest("shouldReceiveAndReturnToAllows", false, true)
        doTest("shouldReceiveAndReturnToAllowsFunctional", true, false)
        doTest("shouldReceiveAndReturnToAllowsFunctional", true, true)
    }

    fun testShouldReceiveAndReturnWithToAllows() {
        doTest("shouldReceiveAndReturnWithToAllows", false, false)
        doTest("shouldReceiveAndReturnWithToAllows", false, true)
        doTest("shouldReceiveAndReturnWithToAllowsFunctional", true, false)
        doTest("shouldReceiveAndReturnWithToAllowsFunctional", true, true)
    }

    fun testShouldReceiveWithAndReturnToAllows() {
        doTest("shouldReceiveWithAndReturnToAllows", false, false)
        doTest("shouldReceiveWithAndReturnToAllows", false, true)
        doTest("shouldReceiveWithAndReturnToAllowsFunctional", true, false)
        doTest("shouldReceiveWithAndReturnToAllowsFunctional", true, true)
    }

    fun testShouldReceiveMultipleToAllows() {
        doTest("shouldReceiveMultipleToAllows", false, false)
        doTest("shouldReceiveMultipleToAllowsMultipleStatements", false, true)
        doTest("shouldReceiveMultipleToAllowsFunctional", true, false)
        doTest("shouldReceiveMultipleToAllowsFunctional", true, true)
    }

    fun testShouldReceiveMultipleAndReturnToAllows() {
        doTest("shouldReceiveMultipleAndReturnToAllows", false, false)
        doTest("shouldReceiveMultipleAndReturnToAllowsMultipleStatements", false, true)
        doTest("shouldReceiveMultipleAndReturnToAllowsFunctional", true, false)
        doTest("shouldReceiveMultipleAndReturnToAllowsFunctional", true, true)
    }

    fun testShouldReceiveOnceToExpects() {
        doTest("shouldReceiveOnceToExpects", false, false)
        doTest("shouldReceiveOnceToExpects", false, true)
        doTest("shouldReceiveOnceToExpectsFunctional", true, false)
        doTest("shouldReceiveOnceToExpectsFunctional", true, true)
    }

    fun testShouldReceiveWithOnceToExpects() {
        doTest("shouldReceiveWithOnceToExpects", false, false)
        doTest("shouldReceiveWithOnceToExpects", false, true)
        doTest("shouldReceiveWithOnceToExpectsFunctional", true, false)
        doTest("shouldReceiveWithOnceToExpectsFunctional", true, true)
    }

    fun testShouldReceiveOnceWithToExpects() {
        doTest("shouldReceiveOnceWithToExpects", false, false)
        doTest("shouldReceiveOnceWithToExpects", false, true)
        doTest("shouldReceiveOnceWithToExpectsFunctional", true, false)
        doTest("shouldReceiveOnceWithToExpectsFunctional", true, true)
    }

    fun testShouldReceiveOnceAndReturnToExpects() {
        doTest("shouldReceiveOnceAndReturnToExpects", false, false)
        doTest("shouldReceiveOnceAndReturnToExpects", false, true)
        doTest("shouldReceiveOnceAndReturnToExpectsFunctional", true, false)
        doTest("shouldReceiveOnceAndReturnToExpectsFunctional", true, true)
    }

    fun testShouldReceiveAndReturnOnceToExpects() {
        doTest("shouldReceiveAndReturnOnceToExpects", false, false)
        doTest("shouldReceiveAndReturnOnceToExpects", false, true)
        doTest("shouldReceiveAndReturnOnceToExpectsFunctional", true, false)
        doTest("shouldReceiveAndReturnOnceToExpectsFunctional", true, true)
    }

    fun testShouldReceiveWithAndReturnOnceToExpects() {
        doTest("shouldReceiveWithAndReturnOnceToExpects", false, false)
        doTest("shouldReceiveWithAndReturnOnceToExpects", false, true)
        doTest("shouldReceiveWithAndReturnOnceToExpectsFunctional", true, false)
        doTest("shouldReceiveWithAndReturnOnceToExpectsFunctional", true, true)
    }

    fun testShouldReceiveOnceWithAndReturnToExpects() {
        doTest("shouldReceiveOnceWithAndReturnToExpects", false, false)
        doTest("shouldReceiveOnceWithAndReturnToExpects", false, true)
        doTest("shouldReceiveOnceWithAndReturnToExpectsFunctional", true, false)
        doTest("shouldReceiveOnceWithAndReturnToExpectsFunctional", true, true)
    }

    fun testShouldReceiveMultipleOnceToExpects() {
        doTest("shouldReceiveMultipleOnceToExpects", false, false)
        doTest("shouldReceiveMultipleOnceToExpectsMultipleStatements", false, true)
        doTest("shouldReceiveMultipleOnceToExpectsFunctional", true, false)
        doTest("shouldReceiveMultipleOnceToExpectsFunctional", true, true)
    }

    fun testShouldReceiveMultipleAndReturnOnceToExpects() {
        doTest("shouldReceiveMultipleAndReturnOnceToExpects", false, false)
        doTest("shouldReceiveMultipleAndReturnOnceToExpectsMultipleStatements", false, true)
        doTest("shouldReceiveMultipleAndReturnOnceToExpectsFunctional", true, false)
        doTest("shouldReceiveMultipleAndReturnOnceToExpectsFunctional", true, true)
    }

    fun testShouldReceiveTwiceToExpects() {
        doTest("shouldReceiveTwiceToExpects", false, false)
        doTest("shouldReceiveTwiceToExpects", false, true)
        doTest("shouldReceiveTwiceToExpectsFunctional", true, false)
        doTest("shouldReceiveTwiceToExpectsFunctional", true, true)
    }

    fun testShouldReceiveTimesToExpects() {
        doTest("shouldReceiveTimesToExpects", false, false)
        doTest("shouldReceiveTimesToExpects", false, true)
        doTest("shouldReceiveTimesToExpectsFunctional", true, false)
        doTest("shouldReceiveTimesToExpectsFunctional", true, true)
    }

    fun testShouldReceiveWithAndReturnTwiceToExpects() {
        doTest("shouldReceiveWithAndReturnTwiceToExpects", false, false)
        doTest("shouldReceiveWithAndReturnTwiceToExpects", false, true)
        doTest("shouldReceiveWithAndReturnTwiceToExpectsFunctional", true, false)
        doTest("shouldReceiveWithAndReturnTwiceToExpectsFunctional", true, true)
    }

    fun testShouldReceiveTwiceWithAndReturnToExpects() {
        doTest("shouldReceiveTwiceWithAndReturnToExpects", false, false)
        doTest("shouldReceiveTwiceWithAndReturnToExpects", false, true)
        doTest("shouldReceiveTwiceWithAndReturnToExpectsFunctional", true, false)
        doTest("shouldReceiveTwiceWithAndReturnToExpectsFunctional", true, true)
    }

    fun testShouldReceiveMultipleTwiceToExpects() {
        doTest("shouldReceiveMultipleTwiceToExpects", false, false)
        doTest("shouldReceiveMultipleTwiceToExpectsMultipleStatements", false, true)
        doTest("shouldReceiveMultipleTwiceToExpectsFunctional", true, false)
        doTest("shouldReceiveMultipleTwiceToExpectsFunctional", true, true)
    }

    fun testShouldReceiveMultipleAndReturnTwiceToExpects() {
        doTest("shouldReceiveMultipleAndReturnTwiceToExpects", false, false)
        doTest("shouldReceiveMultipleAndReturnTwiceToExpectsMultipleStatements", false, true)
        doTest("shouldReceiveMultipleAndReturnTwiceToExpectsFunctional", true, false)
        doTest("shouldReceiveMultipleAndReturnTwiceToExpectsFunctional", true, true)
    }

    fun testShouldReceiveMultipleTwiceAndReturnToExpects() {
        doTest("shouldReceiveMultipleTwiceAndReturnToExpects", false, false)
        doTest("shouldReceiveMultipleTwiceAndReturnToExpectsMultipleStatements", false, true)
        doTest("shouldReceiveMultipleTwiceAndReturnToExpectsFunctional", true, false)
        doTest("shouldReceiveMultipleTwiceAndReturnToExpectsFunctional", true, true)
    }

    fun testShouldNotReceiveToAllowsNever() {
        doTest("shouldNotReceiveToAllowsNever", false, false)
        doTest("shouldNotReceiveToAllowsNever", false, true)
        doTest("shouldNotReceiveToAllowsNeverFunctional", true, false)
        doTest("shouldNotReceiveToAllowsNeverFunctional", true, true)
    }

    fun testShouldNotReceiveToAllowsNeverMultipleArguments() {
        doTest("shouldNotReceiveToAllowsNeverMultipleArguments", false, false)
        doTest("shouldNotReceiveToAllowsNeverMultipleArgumentsMultipleStatements", false, true)
        doTest("shouldNotReceiveToAllowsNeverMultipleArgumentsFunctional", true, false)
        doTest("shouldNotReceiveToAllowsNeverMultipleArgumentsFunctional", true, true)
    }
}
