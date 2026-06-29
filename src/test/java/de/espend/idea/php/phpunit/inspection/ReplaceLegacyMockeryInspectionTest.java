package de.espend.idea.php.phpunit.inspection;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

import java.util.List;

public class ReplaceLegacyMockeryInspectionTest extends LightJavaCodeInsightFixtureTestCase {

    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("common/fixture/MockeryClasses.php");
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit";
    }

    protected void doTest(String testName, Boolean preferFunctionNotation, Boolean preferMultipleStatements) {
        String testFixturePrefix = "inspection/fixtures/";
        myFixture.configureByFile(testFixturePrefix + testName + ".php");

        PropertiesComponent instance = PropertiesComponent.getInstance();
        boolean oldPreferFunctionNotation = instance.getBoolean("preferFunctionNotation",false);
        boolean oldPreferMultipleStatements = instance.getBoolean("preferMultipleStatements",false);
        instance.setValue("preferFunctionNotation", preferFunctionNotation);
        instance.setValue("preferMultipleStatements",preferMultipleStatements);

        myFixture.enableInspections(new ReplaceLegacyMockeryInspection());
        final List<HighlightInfo> highlightInfos = myFixture.doHighlighting();
        assertNotNull(highlightInfos);
        final IntentionAction action = myFixture.findSingleIntention(ReplaceLegacyMockeryInspection.QUICK_FIX_NAME);
        assertNotNull(action);

        myFixture.launchAction(action);
        myFixture.checkResultByFile(testFixturePrefix + testName + ".after.php");

        instance.setValue("preferFunctionNotation", oldPreferFunctionNotation);
        instance.setValue("preferMultipleStatements",oldPreferMultipleStatements);
    }


    public void testShouldReceiveToAllows() {
        doTest("shouldReceiveToAllows", false, false);
        doTest("shouldReceiveToAllows", false, true);
        doTest("shouldReceiveToAllowsFunctional", true, false);
        doTest("shouldReceiveToAllowsFunctional", true, true);
    }

    public void testShouldReceiveAndReturnToAllows() {
        doTest("shouldReceiveAndReturnToAllows", false, false);
        doTest("shouldReceiveAndReturnToAllows", false, true);
        doTest("shouldReceiveAndReturnToAllowsFunctional", true, false);
        doTest("shouldReceiveAndReturnToAllowsFunctional", true, true);
    }

    public void testShouldReceiveAndReturnWithToAllows() {
        doTest("shouldReceiveAndReturnWithToAllows", false, false);
        doTest("shouldReceiveAndReturnWithToAllows", false, true);
        doTest("shouldReceiveAndReturnWithToAllowsFunctional", true, false);
        doTest("shouldReceiveAndReturnWithToAllowsFunctional", true, true);
    }

    public void testShouldReceiveWithAndReturnToAllows() {
        doTest("shouldReceiveWithAndReturnToAllows", false, false);
        doTest("shouldReceiveWithAndReturnToAllows", false, true);
        doTest("shouldReceiveWithAndReturnToAllowsFunctional", true, false);
        doTest("shouldReceiveWithAndReturnToAllowsFunctional", true, true);
    }

    public void testShouldReceiveMultipleToAllows() {
        doTest("shouldReceiveMultipleToAllows", false, false);
        doTest("shouldReceiveMultipleToAllowsMultipleStatements", false, true);
        doTest("shouldReceiveMultipleToAllowsFunctional", true, false);
        doTest("shouldReceiveMultipleToAllowsFunctional", true, true);
    }

    public void testShouldReceiveMultipleAndReturnToAllows() {
        doTest("shouldReceiveMultipleAndReturnToAllows", false, false);
        doTest("shouldReceiveMultipleAndReturnToAllowsMultipleStatements", false, true);
        doTest("shouldReceiveMultipleAndReturnToAllowsFunctional", true, false);
        doTest("shouldReceiveMultipleAndReturnToAllowsFunctional", true, true);
    }

    public void testShouldReceiveOnceToExpects() {
        doTest("shouldReceiveOnceToExpects", false, false);
        doTest("shouldReceiveOnceToExpects", false, true);
        doTest("shouldReceiveOnceToExpectsFunctional", true, false);
        doTest("shouldReceiveOnceToExpectsFunctional", true, true);
    }

    public void testShouldReceiveWithOnceToExpects() {
        doTest("shouldReceiveWithOnceToExpects", false, false);
        doTest("shouldReceiveWithOnceToExpects", false, true);
        doTest("shouldReceiveWithOnceToExpectsFunctional", true, false);
        doTest("shouldReceiveWithOnceToExpectsFunctional", true, true);
    }

    public void testShouldReceiveOnceWithToExpects() {
        doTest("shouldReceiveOnceWithToExpects", false, false);
        doTest("shouldReceiveOnceWithToExpects", false, true);
        doTest("shouldReceiveOnceWithToExpectsFunctional", true, false);
        doTest("shouldReceiveOnceWithToExpectsFunctional", true, true);
    }

    public void testShouldReceiveOnceAndReturnToExpects() {
        doTest("shouldReceiveOnceAndReturnToExpects", false, false);
        doTest("shouldReceiveOnceAndReturnToExpects", false, true);
        doTest("shouldReceiveOnceAndReturnToExpectsFunctional", true, false);
        doTest("shouldReceiveOnceAndReturnToExpectsFunctional", true, true);
    }

    public void testShouldReceiveAndReturnOnceToExpects() {
        doTest("shouldReceiveAndReturnOnceToExpects", false, false);
        doTest("shouldReceiveAndReturnOnceToExpects", false, true);
        doTest("shouldReceiveAndReturnOnceToExpectsFunctional", true, false);
        doTest("shouldReceiveAndReturnOnceToExpectsFunctional", true, true);
    }

    public void testShouldReceiveWithAndReturnOnceToExpects() {
        doTest("shouldReceiveWithAndReturnOnceToExpects", false, false);
        doTest("shouldReceiveWithAndReturnOnceToExpects", false, true);
        doTest("shouldReceiveWithAndReturnOnceToExpectsFunctional", true, false);
        doTest("shouldReceiveWithAndReturnOnceToExpectsFunctional", true, true);
    }

    public void testShouldReceiveOnceWithAndReturnToExpects() {
        doTest("shouldReceiveOnceWithAndReturnToExpects", false, false);
        doTest("shouldReceiveOnceWithAndReturnToExpects", false, true);
        doTest("shouldReceiveOnceWithAndReturnToExpectsFunctional", true, false);
        doTest("shouldReceiveOnceWithAndReturnToExpectsFunctional", true, true);
    }

    public void testShouldReceiveMultipleOnceToExpects() {
        doTest("shouldReceiveMultipleOnceToExpects", false, false);
        doTest("shouldReceiveMultipleOnceToExpectsMultipleStatements", false, true);
        doTest("shouldReceiveMultipleOnceToExpectsFunctional", true, false);
        doTest("shouldReceiveMultipleOnceToExpectsFunctional", true, true);
    }

    public void testShouldReceiveMultipleAndReturnOnceToExpects() {
        doTest("shouldReceiveMultipleAndReturnOnceToExpects", false, false);
        doTest("shouldReceiveMultipleAndReturnOnceToExpectsMultipleStatements", false, true);
        doTest("shouldReceiveMultipleAndReturnOnceToExpectsFunctional", true, false);
        doTest("shouldReceiveMultipleAndReturnOnceToExpectsFunctional", true, true);
    }

    public void testShouldReceiveTwiceToExpects() {
        doTest("shouldReceiveTwiceToExpects", false, false);
        doTest("shouldReceiveTwiceToExpects", false, true);
        doTest("shouldReceiveTwiceToExpectsFunctional", true, false);
        doTest("shouldReceiveTwiceToExpectsFunctional", true, true);
    }

    public void testShouldReceiveTimesToExpects() {
        doTest("shouldReceiveTimesToExpects", false, false);
        doTest("shouldReceiveTimesToExpects", false, true);
        doTest("shouldReceiveTimesToExpectsFunctional", true, false);
        doTest("shouldReceiveTimesToExpectsFunctional", true, true);
    }

    public void testShouldReceiveWithAndReturnTwiceToExpects() {
        doTest("shouldReceiveWithAndReturnTwiceToExpects", false, false);
        doTest("shouldReceiveWithAndReturnTwiceToExpects", false, true);
        doTest("shouldReceiveWithAndReturnTwiceToExpectsFunctional", true, false);
        doTest("shouldReceiveWithAndReturnTwiceToExpectsFunctional", true, true);
    }

    public void testShouldReceiveTwiceWithAndReturnToExpects() {
        doTest("shouldReceiveTwiceWithAndReturnToExpects", false, false);
        doTest("shouldReceiveTwiceWithAndReturnToExpects", false, true);
        doTest("shouldReceiveTwiceWithAndReturnToExpectsFunctional", true, false);
        doTest("shouldReceiveTwiceWithAndReturnToExpectsFunctional", true, true);
    }

    public void testShouldReceiveMultipleTwiceToExpects() {
        doTest("shouldReceiveMultipleTwiceToExpects", false, false);
        doTest("shouldReceiveMultipleTwiceToExpectsMultipleStatements", false, true);
        doTest("shouldReceiveMultipleTwiceToExpectsFunctional", true, false);
        doTest("shouldReceiveMultipleTwiceToExpectsFunctional", true, true);
    }

    public void testShouldReceiveMultipleAndReturnTwiceToExpects() {
        doTest("shouldReceiveMultipleAndReturnTwiceToExpects", false, false);
        doTest("shouldReceiveMultipleAndReturnTwiceToExpectsMultipleStatements", false, true);
        doTest("shouldReceiveMultipleAndReturnTwiceToExpectsFunctional", true, false);
        doTest("shouldReceiveMultipleAndReturnTwiceToExpectsFunctional", true, true);
    }

    public void testShouldReceiveMultipleTwiceAndReturnToExpects() {
        doTest("shouldReceiveMultipleTwiceAndReturnToExpects", false, false);
        doTest("shouldReceiveMultipleTwiceAndReturnToExpectsMultipleStatements", false, true);
        doTest("shouldReceiveMultipleTwiceAndReturnToExpectsFunctional", true, false);
        doTest("shouldReceiveMultipleTwiceAndReturnToExpectsFunctional", true, true);
    }

    public void testShouldNotReceiveToAllowsNever() {
        doTest("shouldNotReceiveToAllowsNever", false, false);
        doTest("shouldNotReceiveToAllowsNever", false, true);
        doTest("shouldNotReceiveToAllowsNeverFunctional", true, false);
        doTest("shouldNotReceiveToAllowsNeverFunctional", true, true);
    }

    public void testShouldNotReceiveToAllowsNeverMultipleArguments() {
        doTest("shouldNotReceiveToAllowsNeverMultipleArguments", false, false);
        doTest("shouldNotReceiveToAllowsNeverMultipleArgumentsMultipleStatements", false, true);
        doTest("shouldNotReceiveToAllowsNeverMultipleArgumentsFunctional", true, false);
        doTest("shouldNotReceiveToAllowsNeverMultipleArgumentsFunctional", true, true);
    }
}
