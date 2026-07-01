package de.espend.idea.php.phpunit.renaming

import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase

class MockeryRenameTest : PhpUnitLightCodeInsightFixtureTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("common/fixture/MockeryClasses.php")
    }

    override fun getTestDataPath(): String {
        return "src/test/java/de/espend/idea/php/phpunit"
    }

    protected fun doTest(targetFileName: String) {
        myFixture.configureByFile("common/fixture/renamingandusage/" + targetFileName)
        myFixture.renameElementAtCaret("newName")
        myFixture.checkResultByFile("renaming/fixtures/after/" + targetFileName)

    }

    fun testRenamingAllowsUsingCreateMockByClassName() {
        doTest("testFindUsagesFindAllowsUsingCreateMockByClassName.php")
    }

    fun testRenamingExpectsUsingCreateMockByClassName() {
        doTest("testFindUsagesFindExpectsUsingCreateMockByClassName.php")
    }

    fun testRenamingShouldReceiveUsingCreateMockByClassName() {
        doTest("testFindUsagesFindShouldReceiveUsingCreateMockByClassName.php")
    }

    fun testRenamingShouldNotReceiveUsingCreateMockByClassName() {
        doTest("testFindUsagesFindShouldNotReceiveUsingCreateMockByClassName.php")
    }

    fun testRenamingAllowsUsingCreateMockByFQN() {
        doTest("testFindUsagesFindAllowsUsingCreateMockByFQN.php")
    }

    fun testRenamingAllowsUsingCreateMockFromInterface() {
        doTest("testFindUsagesFindAllowsUsingCreateMockFromInterface.php")
    }

    fun testRenamingAllowsAlternativeMethodUsingCreateMockFromClassNameAndAlternativeInterface() {
        doTest("testFindUsagesFindAllowsAlternativeMethodUsingCreateMockFromClassNameAndAlternativeInterface.php")
    }

    fun testRenamingAllowsAlternativeMethodUsingCreateMockFromFQNAndAlternativeInterfaceFQN() {
        doTest("testFindUsagesFindAllowsAlternativeMethodUsingCreateMockFromFQNAndAlternativeInterfaceFQN.php")
    }

    fun testRenamingAllowsUsingCreateMockWithAlias() {
        doTest("testFindUsagesFindAllowsUsingCreateMockWithAlias.php")
    }

    fun testRenamingAllowsUsingCreateMockGeneratedPartialByClassName() {
        doTest("testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByClassName.php")
    }

    fun testRenamingAllowsUsingCreateMockGeneratedPartialByClassNameDoubleQuotes() {
        doTest("testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByClassNameDoubleQuotes.php")
    }

    fun testRenamingAllowsUsingCreateMockGeneratedPartialByFQN() {
        doTest("testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByFQN.php")
    }

    fun testRenamingAllowsUsingCreateMockGeneratedPartialByClassNameTwoMethods() {
        doTest("testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByClassNameTwoMethods.php")
    }

    fun testRenamingAllowsUsingCreateMockGeneratedPartialByFQNTwoMethods() {
        doTest("testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByFQNTwoMethods.php")
    }

    fun testRenamingAllowsUsingCreateMockWithConstructorByClassName() {
        doTest("testFindUsagesFindAllowsUsingCreateMockWithConstructorByClassName.php")
    }

    fun testRenamingAllowsUsingCreateMockWithConstructorAndAlternativeInterfaceByClassName() {
        doTest("testFindUsagesFindAllowsUsingCreateMockWithConstructorAndAlternativeInterfaceByClassName.php")
    }

    fun testRenamingAllowsAlternativeUsingCreateMockWithConstructorAndAlternativeInterfaceByClassName() {
        doTest("testFindUsagesFindAllowsAlternativeUsingCreateMockWithConstructorAndAlternativeInterfaceByClassName.php")
    }

    fun testRenamingAllowsUsingCreateMockWithConstructorByFQN() {
        doTest("testFindUsagesFindAllowsUsingCreateMockWithConstructorByFQN.php")
    }

    fun testRenamingAllowsUsingCreateMockWithConstructorAndAlternativeInterfaceByFQN() {
        doTest("testFindUsagesFindAllowsUsingCreateMockWithConstructorAndAlternativeInterfaceByFQN.php")
    }

    fun testRenamingAllowsAlternativeUsingCreateMockWithConstructorAndAlternativeInterfaceByFQN() {
        doTest("testFindUsagesFindAllowsAlternativeUsingCreateMockWithConstructorAndAlternativeInterfaceByFQN.php")
    }

    fun testRenamingAllowsUsingCreateMockWithOverloadByFQN() {
        doTest("testFindUsagesFindAllowsUsingCreateMockWithOverloadByFQN.php")
    }

    fun testRenamingAllowsUsingProxyCreateMock() {
        doTest("testFindUsagesFindAllowsUsingProxyCreateMock.php")
    }

    fun testRenamingAllowsUsingCreateMockByClassNameRuntimePartial() {
        doTest("testFindUsagesFindAllowsUsingCreateMockByClassNameRuntimePartial.php")
    }

    fun testRenamingAllowsUsingCreateMockByFQNRuntimePartial() {
        doTest("testFindUsagesFindAllowsUsingCreateMockByFQNRuntimePartial.php")
    }

    fun testRenamingAllowsMethodUsingCreateMockFromClassNameAndAlternativeInterfaceRuntimePartial() {
        doTest("testFindUsagesFindAllowsMethodUsingCreateMockFromClassNameAndAlternativeInterfaceRuntimePartial.php")
    }

    fun testRenamingAllowsMethodUsingCreateMockFromFQNAndAlternativeInterfaceFQNRuntimePartial() {
        doTest("testFindUsagesFindAllowsMethodUsingCreateMockFromFQNAndAlternativeInterfaceFQNRuntimePartial.php")
    }

    fun testRenamingAllowsAlternativeMethodUsingCreateMockFromClassNameAndAlternativeInterfaceRuntimePartial() {
        doTest("testFindUsagesFindAllowsAlternativeMethodUsingCreateMockFromClassNameAndAlternativeInterfaceRuntimePartial.php")
    }

    fun testRenamingAllowsAlternativeMethodUsingCreateMockFromFQNAndAlternativeInterfaceFQNRuntimePartial() {
        doTest("testFindUsagesFindAllowsAlternativeMethodUsingCreateMockFromFQNAndAlternativeInterfaceFQNRuntimePartial.php")
    }

    fun testRenamingAllowsUsingCreateMockByClassNameWithMultipleMethodsFirst() {
        doTest("testFindUsagesFindAllowsUsingCreateMockByClassNameWithMultipleMethodsFirst.php")
    }

    fun testRenamingAllowsUsingCreateMockByClassNameWithMultipleMethodsSecond() {
        doTest("testFindUsagesFindAllowsUsingCreateMockByClassNameWithMultipleMethodsSecond.php")
    }

    fun testRenamingAllowsUsingCreateNamedMockByClassName() {
        doTest("testFindUsagesFindAllowsUsingCreateNamedMockByClassName.php")
    }

    fun testRenamingAllowsUsingCreateSpyByClassName() {
        doTest("testFindUsagesFindAllowsUsingCreateSpyByClassName.php")
    }

    fun testRenamingAllowsUsingCreateSpyByFQN() {
        doTest("testFindUsagesFindAllowsUsingCreateSpyByFQN.php")
    }

    fun testRenamingAllUsagesCreateMockByClassName() {
        doTest("testFindUsagesFindsAllUsagesCreateMockByClassName.php")
    }
}