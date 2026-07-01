package de.espend.idea.php.phpunit.renaming;

import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;

public class MockeryRenameTest extends PhpUnitLightCodeInsightFixtureTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("common/fixture/MockeryClasses.php");
    }

    @Override
    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit";
    }

    protected void doTest(String targetFileName) {
        myFixture.configureByFile("common/fixture/renamingandusage/" + targetFileName);
        myFixture.renameElementAtCaret("newName");
        myFixture.checkResultByFile("renaming/fixtures/after/" + targetFileName);

    }

    public void testRenamingAllowsUsingCreateMockByClassName() {
        doTest("testFindUsagesFindAllowsUsingCreateMockByClassName.php");
    }

    public void testRenamingExpectsUsingCreateMockByClassName() {
        doTest("testFindUsagesFindExpectsUsingCreateMockByClassName.php");
    }

    public void testRenamingShouldReceiveUsingCreateMockByClassName() {
        doTest("testFindUsagesFindShouldReceiveUsingCreateMockByClassName.php");
    }

    public void testRenamingShouldNotReceiveUsingCreateMockByClassName() {
        doTest("testFindUsagesFindShouldNotReceiveUsingCreateMockByClassName.php");
    }

    public void testRenamingAllowsUsingCreateMockByFQN() {
        doTest("testFindUsagesFindAllowsUsingCreateMockByFQN.php");
    }

    public void testRenamingAllowsUsingCreateMockFromInterface() {
        doTest("testFindUsagesFindAllowsUsingCreateMockFromInterface.php");
    }

    public void testRenamingAllowsAlternativeMethodUsingCreateMockFromClassNameAndAlternativeInterface() {
        doTest("testFindUsagesFindAllowsAlternativeMethodUsingCreateMockFromClassNameAndAlternativeInterface.php");
    }

    public void testRenamingAllowsAlternativeMethodUsingCreateMockFromFQNAndAlternativeInterfaceFQN() {
        doTest("testFindUsagesFindAllowsAlternativeMethodUsingCreateMockFromFQNAndAlternativeInterfaceFQN.php");
    }

    public void testRenamingAllowsUsingCreateMockWithAlias() {
        doTest("testFindUsagesFindAllowsUsingCreateMockWithAlias.php");
    }

    public void testRenamingAllowsUsingCreateMockGeneratedPartialByClassName() {
        doTest("testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByClassName.php");
    }

    public void testRenamingAllowsUsingCreateMockGeneratedPartialByClassNameDoubleQuotes() {
        doTest("testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByClassNameDoubleQuotes.php");
    }

    public void testRenamingAllowsUsingCreateMockGeneratedPartialByFQN() {
        doTest("testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByFQN.php");
    }

    public void testRenamingAllowsUsingCreateMockGeneratedPartialByClassNameTwoMethods() {
        doTest("testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByClassNameTwoMethods.php");
    }

    public void testRenamingAllowsUsingCreateMockGeneratedPartialByFQNTwoMethods() {
        doTest("testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByFQNTwoMethods.php");
    }

    public void testRenamingAllowsUsingCreateMockWithConstructorByClassName() {
        doTest("testFindUsagesFindAllowsUsingCreateMockWithConstructorByClassName.php");
    }

    public void testRenamingAllowsUsingCreateMockWithConstructorAndAlternativeInterfaceByClassName() {
        doTest("testFindUsagesFindAllowsUsingCreateMockWithConstructorAndAlternativeInterfaceByClassName.php");
    }

    public void testRenamingAllowsAlternativeUsingCreateMockWithConstructorAndAlternativeInterfaceByClassName() {
        doTest("testFindUsagesFindAllowsAlternativeUsingCreateMockWithConstructorAndAlternativeInterfaceByClassName.php");
    }

    public void testRenamingAllowsUsingCreateMockWithConstructorByFQN() {
        doTest("testFindUsagesFindAllowsUsingCreateMockWithConstructorByFQN.php");
    }

    public void testRenamingAllowsUsingCreateMockWithConstructorAndAlternativeInterfaceByFQN() {
        doTest("testFindUsagesFindAllowsUsingCreateMockWithConstructorAndAlternativeInterfaceByFQN.php");
    }

    public void testRenamingAllowsAlternativeUsingCreateMockWithConstructorAndAlternativeInterfaceByFQN() {
        doTest("testFindUsagesFindAllowsAlternativeUsingCreateMockWithConstructorAndAlternativeInterfaceByFQN.php");
    }

    public void testRenamingAllowsUsingCreateMockWithOverloadByFQN() {
        doTest("testFindUsagesFindAllowsUsingCreateMockWithOverloadByFQN.php");
    }

    public void testRenamingAllowsUsingProxyCreateMock() {
        doTest("testFindUsagesFindAllowsUsingProxyCreateMock.php");
    }

    public void testRenamingAllowsUsingCreateMockByClassNameRuntimePartial() {
        doTest("testFindUsagesFindAllowsUsingCreateMockByClassNameRuntimePartial.php");
    }

    public void testRenamingAllowsUsingCreateMockByFQNRuntimePartial() {
        doTest("testFindUsagesFindAllowsUsingCreateMockByFQNRuntimePartial.php");
    }

    public void testRenamingAllowsMethodUsingCreateMockFromClassNameAndAlternativeInterfaceRuntimePartial() {
        doTest("testFindUsagesFindAllowsMethodUsingCreateMockFromClassNameAndAlternativeInterfaceRuntimePartial.php");
    }

    public void testRenamingAllowsMethodUsingCreateMockFromFQNAndAlternativeInterfaceFQNRuntimePartial() {
        doTest("testFindUsagesFindAllowsMethodUsingCreateMockFromFQNAndAlternativeInterfaceFQNRuntimePartial.php");
    }

    public void testRenamingAllowsAlternativeMethodUsingCreateMockFromClassNameAndAlternativeInterfaceRuntimePartial() {
        doTest("testFindUsagesFindAllowsAlternativeMethodUsingCreateMockFromClassNameAndAlternativeInterfaceRuntimePartial.php");
    }

    public void testRenamingAllowsAlternativeMethodUsingCreateMockFromFQNAndAlternativeInterfaceFQNRuntimePartial() {
        doTest("testFindUsagesFindAllowsAlternativeMethodUsingCreateMockFromFQNAndAlternativeInterfaceFQNRuntimePartial.php");
    }

    public void testRenamingAllowsUsingCreateMockByClassNameWithMultipleMethodsFirst() {
        doTest("testFindUsagesFindAllowsUsingCreateMockByClassNameWithMultipleMethodsFirst.php");
    }

    public void testRenamingAllowsUsingCreateMockByClassNameWithMultipleMethodsSecond() {
        doTest("testFindUsagesFindAllowsUsingCreateMockByClassNameWithMultipleMethodsSecond.php");
    }

    public void testRenamingAllowsUsingCreateNamedMockByClassName() {
        doTest("testFindUsagesFindAllowsUsingCreateNamedMockByClassName.php");
    }

    public void testRenamingAllowsUsingCreateSpyByClassName() {
        doTest("testFindUsagesFindAllowsUsingCreateSpyByClassName.php");
    }

    public void testRenamingAllowsUsingCreateSpyByFQN() {
        doTest("testFindUsagesFindAllowsUsingCreateSpyByFQN.php");
    }

    public void testRenamingAllUsagesCreateMockByClassName() {
        doTest("testFindUsagesFindsAllUsagesCreateMockByClassName.php");
    }
}