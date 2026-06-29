package de.espend.idea.php.phpunit.annotator;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;


public class MockeryAnnotatorTest extends LightJavaCodeInsightFixtureTestCase {


    @Override
    protected String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit";
    }

    public void testNoMethodAnnotation() {
        myFixture.configureByFiles("annotator/fixtures/MockeryAnnotatorNoMethod.php", "common/fixture/MockeryClasses.php");
        myFixture.checkHighlighting(true, false, false, false);
    }

    public void testNoMethodMultipleClassesAnnotation() {
        myFixture.configureByFiles("annotator/fixtures/MockeryAnnotatorNoMethodMultipleClasses.php", "common/fixture/MockeryClasses.php");
        myFixture.checkHighlighting(true, false, false, false);
    }

    public void testPrivateMethodAnnotation() {
        myFixture.configureByFiles("annotator/fixtures/MockeryAnnotatorPrivateMethod.php", "common/fixture/MockeryClasses.php");
        myFixture.checkHighlighting(true, false, false, false);
    }

    public void testProtectedMethodAnnotation() {
        myFixture.configureByFiles("annotator/fixtures/MockeryAnnotatorProtectedMethod.php", "common/fixture/MockeryClasses.php");
        myFixture.checkHighlighting(true, false, false, false);
    }

    public void testArrayScopeAnnotation() {
        myFixture.configureByFiles("annotator/fixtures/MockeryAnnotatorArrayScopes.php", "common/fixture/MockeryClasses.php");
        myFixture.checkHighlighting(true, false, false, false);
    }

    public void testLegacyMethodAnnotation() {
        myFixture.configureByFiles("annotator/fixtures/MockeryAnnotatorLegacyMethods.php", "common/fixture/MockeryClasses.php");
        myFixture.checkHighlighting(true, false, false, false);
    }
}
