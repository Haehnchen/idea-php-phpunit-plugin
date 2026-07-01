package de.espend.idea.php.phpunit.usages

import com.intellij.openapi.command.WriteCommandAction
import com.jetbrains.php.lang.psi.PhpPsiElementFactory
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase

class MockeryFindUsagesProviderTest : PhpUnitLightCodeInsightFixtureTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("common/fixture/MockeryClasses.php")
    }

    override fun getTestDataPath(): String {
        return "src/test/kotlin/de/espend/idea/php/phpunit"
    }

    protected fun doTest(targetFileName: String, numberOfUsages: Int) {
        myFixture.configureByFile("common/fixture/renamingandusage/" + targetFileName)
        val usageInfos = myFixture.findUsages(myFixture.getElementAtCaret())
        assertEquals(numberOfUsages, usageInfos.size)

        for (usage in usageInfos) {
            val range = usage.getRangeInElement()!!
            val usageElement = usage.getElement()!!
            val usageText = usageElement.getText()

            val newContent = usageText.substring(0, range.getStartOffset()) + "<usage>" + usageText.substring(range.getEndOffset())

            val newString = PhpPsiElementFactory.createStatement(usage.getProject(), newContent)
            WriteCommandAction.runWriteCommandAction(usageElement.getProject(), Runnable {
                usageElement.replace(newString)
            })
        }
        myFixture.checkResultByFile("usages/fixtures/after/" + targetFileName)
    }

    fun testFindUsagesFindAllowsUsingCreateMockByClassName() {
        doTest("testFindUsagesFindAllowsUsingCreateMockByClassName.php", 1)
    }

    fun testFindUsagesFindExpectsUsingCreateMockByClassName() {
        doTest("testFindUsagesFindExpectsUsingCreateMockByClassName.php", 1)
    }

    fun testFindUsagesFindShouldReceiveUsingCreateMockByClassName() {
        doTest("testFindUsagesFindShouldReceiveUsingCreateMockByClassName.php", 1)
    }

    fun testFindUsagesFindShouldNotReceiveUsingCreateMockByClassName() {
        doTest("testFindUsagesFindShouldNotReceiveUsingCreateMockByClassName.php", 1)
    }

    fun testFindUsagesFindAllowsUsingCreateMockByFQN() {
        doTest("testFindUsagesFindAllowsUsingCreateMockByFQN.php", 1)
    }

    fun testFindUsagesFindAllowsUsingCreateMockFromInterface() {
        doTest("testFindUsagesFindAllowsUsingCreateMockFromInterface.php", 1)
    }

    fun testFindUsagesFindAllowsAlternativeMethodUsingCreateMockFromClassNameAndAlternativeInterface() {
        doTest("testFindUsagesFindAllowsAlternativeMethodUsingCreateMockFromClassNameAndAlternativeInterface.php", 1)
    }

    fun testFindUsagesFindAllowsAlternativeMethodUsingCreateMockFromFQNAndAlternativeInterfaceFQN() {
        doTest("testFindUsagesFindAllowsAlternativeMethodUsingCreateMockFromFQNAndAlternativeInterfaceFQN.php", 1)
    }

    fun testFindUsagesFindAllowsUsingCreateMockWithAlias() {
        doTest("testFindUsagesFindAllowsUsingCreateMockWithAlias.php", 1)
    }

    // Generated partials will have 2 usages as the method name in the creation is also a usage
    fun testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByClassName() {
        doTest("testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByClassName.php", 2)
    }

    fun testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByClassNameDoubleQuotes() {
        doTest("testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByClassNameDoubleQuotes.php", 2)
    }

    fun testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByFQN() {
        doTest("testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByFQN.php", 2)
    }

    fun testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByClassNameTwoMethods() {
        doTest("testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByClassNameTwoMethods.php", 2)
    }

    fun testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByFQNTwoMethods() {
        doTest("testFindUsagesFindAllowsUsingCreateMockGeneratedPartialByFQNTwoMethods.php", 2)
    }

    fun testFindUsagesFindAllowsUsingCreateMockWithConstructorByClassName() {
        doTest("testFindUsagesFindAllowsUsingCreateMockWithConstructorByClassName.php", 1)
    }

    fun testFindUsagesFindAllowsUsingCreateMockWithConstructorAndAlternativeInterfaceByClassName() {
        doTest("testFindUsagesFindAllowsUsingCreateMockWithConstructorAndAlternativeInterfaceByClassName.php", 1)
    }

    fun testFindUsagesFindAllowsAlternativeUsingCreateMockWithConstructorAndAlternativeInterfaceByClassName() {
        doTest("testFindUsagesFindAllowsAlternativeUsingCreateMockWithConstructorAndAlternativeInterfaceByClassName.php", 1)
    }

    fun testFindUsagesFindAllowsUsingCreateMockWithConstructorByFQN() {
        doTest("testFindUsagesFindAllowsUsingCreateMockWithConstructorByFQN.php", 1)
    }

    fun testFindUsagesFindAllowsUsingCreateMockWithConstructorAndAlternativeInterfaceByFQN() {
        doTest("testFindUsagesFindAllowsUsingCreateMockWithConstructorAndAlternativeInterfaceByFQN.php", 1)
    }

    fun testFindUsagesFindAllowsAlternativeUsingCreateMockWithConstructorAndAlternativeInterfaceByFQN() {
        doTest("testFindUsagesFindAllowsAlternativeUsingCreateMockWithConstructorAndAlternativeInterfaceByFQN.php", 1)
    }

    fun testFindUsagesFindAllowsUsingCreateMockWithOverloadByFQN() {
        doTest("testFindUsagesFindAllowsUsingCreateMockWithOverloadByFQN.php", 1)
    }

    fun testFindUsagesFindAllowsUsingProxyCreateMock() {
        doTest("testFindUsagesFindAllowsUsingProxyCreateMock.php", 1)
    }

    fun testFindUsagesFindAllowsUsingCreateMockByClassNameRuntimePartial() {
        doTest("testFindUsagesFindAllowsUsingCreateMockByClassNameRuntimePartial.php", 1)
    }

    fun testFindUsagesFindAllowsUsingCreateMockByFQNRuntimePartial() {
        doTest("testFindUsagesFindAllowsUsingCreateMockByFQNRuntimePartial.php", 1)
    }

    fun testFindUsagesFindAllowsMethodUsingCreateMockFromClassNameAndAlternativeInterfaceRuntimePartial() {
        doTest("testFindUsagesFindAllowsMethodUsingCreateMockFromClassNameAndAlternativeInterfaceRuntimePartial.php", 1)
    }

    fun testFindUsagesFindAllowsMethodUsingCreateMockFromFQNAndAlternativeInterfaceFQNRuntimePartial() {
        doTest("testFindUsagesFindAllowsMethodUsingCreateMockFromFQNAndAlternativeInterfaceFQNRuntimePartial.php", 1)
    }

    fun testFindUsagesFindAllowsAlternativeMethodUsingCreateMockFromClassNameAndAlternativeInterfaceRuntimePartial() {
        doTest("testFindUsagesFindAllowsAlternativeMethodUsingCreateMockFromClassNameAndAlternativeInterfaceRuntimePartial.php", 1)
    }

    fun testFindUsagesFindAllowsAlternativeMethodUsingCreateMockFromFQNAndAlternativeInterfaceFQNRuntimePartial() {
        doTest("testFindUsagesFindAllowsAlternativeMethodUsingCreateMockFromFQNAndAlternativeInterfaceFQNRuntimePartial.php", 1)
    }

    fun testFindUsagesFindAllowsUsingCreateMockByClassNameWithMultipleMethodsFirst() {
        doTest("testFindUsagesFindAllowsUsingCreateMockByClassNameWithMultipleMethodsFirst.php", 1)
    }

    fun testFindUsagesFindAllowsUsingCreateMockByClassNameWithMultipleMethodsSecond() {
        doTest("testFindUsagesFindAllowsUsingCreateMockByClassNameWithMultipleMethodsSecond.php", 1)
    }

    fun testFindUsagesFindAllowsUsingCreateNamedMockByClassName() {
        doTest("testFindUsagesFindAllowsUsingCreateNamedMockByClassName.php", 1)
    }

    fun testFindUsagesFindAllowsUsingCreateSpyByClassName() {
        doTest("testFindUsagesFindAllowsUsingCreateSpyByClassName.php", 1)
    }

    fun testFindUsagesFindAllowsUsingCreateSpyByFQN() {
        doTest("testFindUsagesFindAllowsUsingCreateSpyByFQN.php", 1)
    }

    fun testFindUsagesFindsAllUsagesCreateMockByClassName() {
        doTest("testFindUsagesFindsAllUsagesCreateMockByClassName.php", 3)
    }
}
