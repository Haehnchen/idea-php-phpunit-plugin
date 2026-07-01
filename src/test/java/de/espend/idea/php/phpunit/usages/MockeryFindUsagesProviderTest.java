package de.espend.idea.php.phpunit.usages;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageInfo;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;
import java.util.Collection;

public class MockeryFindUsagesProviderTest extends PhpUnitLightCodeInsightFixtureTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("common/fixture/MockeryClasses.php");
    }

    @Override
    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit";
    }

    protected void doTest(String targetFileName, int numberOfUsages) {
        myFixture.configureByFile("common/fixture/renamingandusage/" + targetFileName);
        Collection<UsageInfo> usageInfos = myFixture.findUsages(myFixture.getElementAtCaret());
        assertEquals(numberOfUsages, usageInfos.size());

        for (UsageInfo usage : usageInfos) {
            TextRange range = usage.getRangeInElement();
            PsiElement usageElement = usage.getElement();
            String usageText = usageElement.getText();

            String newContent = usageText.substring(0, range.getStartOffset()) + "<usage>" + usageText.substring(range.getEndOffset());

            PsiElement newString = PhpPsiElementFactory.createStatement(usage.getProject(), newContent);
            WriteCommandAction.runWriteCommandAction(usageElement.getProject(),
                    () -> {
                        usageElement.replace(newString);
                    });
        }
        myFixture.checkResultByFile("usages/fixtures/after/" + targetFileName);
    }

    public void testFindUsagesFindAllowsUsingCreateMockByClassName() {
        doTest("testFindUsagesFindAllowsUsingCreateMockByClassName.php", 1);
    }

    public void testFindUsagesFindExpectsUsingCreateMockByClassName() {
        doTest("testFindUsagesFindExpectsUsingCreateMockByClassName.php", 1);
    }

    public void testFindUsagesFindShouldReceiveUsingCreateMockByClassName() {
        doTest("testFindUsagesFindShouldReceiveUsingCreateMockByClassName.php", 1);
    }

    public void testFindUsagesFindShouldNotReceiveUsingCreateMockByClassName() {
        doTest("testFindUsagesFindShouldNotReceiveUsingCreateMockByClassName.php", 1);
    }

    public void testFindUsagesFindAllowsUsingCreateMockByFQN() {
        doTest("testFindUsagesFindAllowsUsingCreateMockByFQN.php", 1);
    }

    public void testFindUsagesFindAllowsUsingCreateMockFromInterface() {
        doTest("testFindUsagesFindAllowsUsingCreateMockFromInterface.php", 1);
    }

    public void testFindUsagesFindAllowsAlternativeMethodUsingCreateMockFromClassNameAndAlternativeInterface() {
        doTest("testFindUsagesFindAllowsAlternativeMethodUsingCreateMockFromClassNameAndAlternativeInterface.php", 1);
    }

    public void testFindUsagesFindAllowsAlternativeMethodUsingCreateMockFromFQNAndAlternativeInterfaceFQN() {
        doTest("testFindUsagesFindAllowsAlternativeMethodUsingCreateMockFromFQNAndAlternativeInterfaceFQN.php", 1);
    }

    public void testFindUsagesFindAllowsUsingCreateMockWithAlias() {
        doTest("testFindUsagesFindAllowsUsingCreateMockWithAlias.php", 1);
    }

    // Generated partials will have 2 usages as the method name in the creation is also a usage
    public void testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByClassName() {
        doTest("testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByClassName.php", 2);
    }

    public void testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByClassNameDoubleQuotes() {
        doTest("testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByClassNameDoubleQuotes.php", 2);
    }

    public void testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByFQN() {
        doTest("testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByFQN.php", 2);
    }

    public void testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByClassNameTwoMethods() {
        doTest("testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByClassNameTwoMethods.php", 2);
    }

    public void testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByFQNTwoMethods() {
        doTest("testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByFQNTwoMethods.php", 2);
    }

    public void testFindUsagesFindAllowsUsingCreateMockWithConstructorByClassName() {
        doTest("testFindUsagesFindAllowsUsingCreateMockWithConstructorByClassName.php", 1);
    }

    public void testFindUsagesFindAllowsUsingCreateMockWithConstructorAndAlternativeInterfaceByClassName() {
        doTest("testFindUsagesFindAllowsUsingCreateMockWithConstructorAndAlternativeInterfaceByClassName.php", 1);
    }

    public void testFindUsagesFindAllowsAlternativeUsingCreateMockWithConstructorAndAlternativeInterfaceByClassName() {
        doTest("testFindUsagesFindAllowsAlternativeUsingCreateMockWithConstructorAndAlternativeInterfaceByClassName.php", 1);
    }

    public void testFindUsagesFindAllowsUsingCreateMockWithConstructorByFQN() {
        doTest("testFindUsagesFindAllowsUsingCreateMockWithConstructorByFQN.php", 1);
    }

    public void testFindUsagesFindAllowsUsingCreateMockWithConstructorAndAlternativeInterfaceByFQN() {
        doTest("testFindUsagesFindAllowsUsingCreateMockWithConstructorAndAlternativeInterfaceByFQN.php", 1);
    }

    public void testFindUsagesFindAllowsAlternativeUsingCreateMockWithConstructorAndAlternativeInterfaceByFQN() {
        doTest("testFindUsagesFindAllowsAlternativeUsingCreateMockWithConstructorAndAlternativeInterfaceByFQN.php", 1);
    }

    public void testFindUsagesFindAllowsUsingCreateMockWithOverloadByFQN() {
        doTest("testFindUsagesFindAllowsUsingCreateMockWithOverloadByFQN.php", 1);
    }

    public void testFindUsagesFindAllowsUsingProxyCreateMock() {
        doTest("testFindUsagesFindAllowsUsingProxyCreateMock.php", 1);
    }

    public void testFindUsagesFindAllowsUsingCreateMockByClassNameRuntimePartial() {
        doTest("testFindUsagesFindAllowsUsingCreateMockByClassNameRuntimePartial.php", 1);
    }

    public void testFindUsagesFindAllowsUsingCreateMockByFQNRuntimePartial() {
        doTest("testFindUsagesFindAllowsUsingCreateMockByFQNRuntimePartial.php", 1);
    }

    public void testFindUsagesFindAllowsMethodUsingCreateMockFromClassNameAndAlternativeInterfaceRuntimePartial() {
        doTest("testFindUsagesFindAllowsMethodUsingCreateMockFromClassNameAndAlternativeInterfaceRuntimePartial.php", 1);
    }

    public void testFindUsagesFindAllowsMethodUsingCreateMockFromFQNAndAlternativeInterfaceFQNRuntimePartial() {
        doTest("testFindUsagesFindAllowsMethodUsingCreateMockFromFQNAndAlternativeInterfaceFQNRuntimePartial.php", 1);
    }

    public void testFindUsagesFindAllowsAlternativeMethodUsingCreateMockFromClassNameAndAlternativeInterfaceRuntimePartial() {
        doTest("testFindUsagesFindAllowsAlternativeMethodUsingCreateMockFromClassNameAndAlternativeInterfaceRuntimePartial.php", 1);
    }

    public void testFindUsagesFindAllowsAlternativeMethodUsingCreateMockFromFQNAndAlternativeInterfaceFQNRuntimePartial() {
        doTest("testFindUsagesFindAllowsAlternativeMethodUsingCreateMockFromFQNAndAlternativeInterfaceFQNRuntimePartial.php", 1);
    }

    public void testFindUsagesFindAllowsUsingCreateMockByClassNameWithMultipleMethodsFirst() {
        doTest("testFindUsagesFindAllowsUsingCreateMockByClassNameWithMultipleMethodsFirst.php", 1);
    }

    public void testFindUsagesFindAllowsUsingCreateMockByClassNameWithMultipleMethodsSecond() {
        doTest("testFindUsagesFindAllowsUsingCreateMockByClassNameWithMultipleMethodsSecond.php", 1);
    }

    public void testFindUsagesFindAllowsUsingCreateNamedMockByClassName() {
        doTest("testFindUsagesFindAllowsUsingCreateNamedMockByClassName.php", 1);
    }

    public void testFindUsagesFindAllowsUsingCreateSpyByClassName() {
        doTest("testFindUsagesFindAllowsUsingCreateSpyByClassName.php", 1);
    }

    public void testFindUsagesFindAllowsUsingCreateSpyByFQN() {
        doTest("testFindUsagesFindAllowsUsingCreateSpyByFQN.php", 1);
    }

    public void testFindUsagesFindsAllUsagesCreateMockByClassName() {
        doTest("testFindUsagesFindsAllUsagesCreateMockByClassName.php", 3);
    }
}
