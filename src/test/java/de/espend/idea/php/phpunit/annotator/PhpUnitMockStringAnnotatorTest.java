package de.espend.idea.php.phpunit.annotator;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

public class PhpUnitMockStringAnnotatorTest extends LightJavaCodeInsightFixtureTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit";
    }

    public void testPhpUnitMockStringWarnings() {
        myFixture.configureByFiles(
                "annotator/fixtures/PhpUnitMockStringAnnotatorTest.php",
                "common/fixture/PhpUnitMockStringClasses.php"
        );

        myFixture.checkHighlighting(true, false, false, false);
    }
}
