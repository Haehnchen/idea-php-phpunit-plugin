package de.espend.idea.php.phpunit.utils

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase
import de.espend.idea.php.phpunit.utils.mockstring.Filter
import de.espend.idea.php.phpunit.utils.mockstring.FilterFactory
import de.espend.idea.php.phpunit.utils.mockstring.MethodMockFilter
import de.espend.idea.php.phpunit.utils.mockstring.MockBuilderFilter

class PhpUnitMockStringFilterFactoryTest : PhpUnitLightCodeInsightFixtureTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("common/fixture/PhpUnitMockStringClasses.php")
    }

    override fun getTestDataPath(): String {
        return "src/test/java/de/espend/idea/php/phpunit"
    }

    fun testWrongParameterPositionDoesNotCreateFilter() {
        assertNull(findFilter("<?php\n" +
                "class FilterFactoryWrongParameterPosition extends PHPUnit_Framework_TestCase\n" +
                "{\n" +
                "    public function testMock()\n" +
                "    {\n" +
                "        \$this->getMock('<caret>PhpUnitMockStringTarget', array('publicMethod'));\n" +
                "    }\n" +
                "}\n"
        ))
    }

    fun testGetMockForAbstractClassUsesSeventhParameter() {
        assertFilter(MockBuilderFilter::class.java, findFilter("<?php\n" +
                "class FilterFactoryAbstractClassParameter extends PHPUnit_Framework_TestCase\n" +
                "{\n" +
                "    public function testMock()\n" +
                "    {\n" +
                "        \$this->getMockForAbstractClass(PhpUnitMockAbstractTarget::class, array(), '', true, true, true, array('<caret>publicMethod'));\n" +
                "    }\n" +
                "}\n"
        ))
    }

    fun testGetMockForTraitUsesSeventhParameter() {
        assertFilter(MockBuilderFilter::class.java, findFilter("<?php\n" +
                "class FilterFactoryTraitParameter extends PHPUnit_Framework_TestCase\n" +
                "{\n" +
                "    public function testMock()\n" +
                "    {\n" +
                "        \$this->getMockForTrait(PhpUnitMockTraitTarget::class, array(), '', true, true, true, array('<caret>traitMethod'));\n" +
                "    }\n" +
                "}\n"
        ))
    }

    fun testGetMockClassUsesSecondParameter() {
        assertFilter(MockBuilderFilter::class.java, findFilter("<?php\n" +
                "class FilterFactoryGetMockClassParameter extends PHPUnit_Framework_TestCase\n" +
                "{\n" +
                "    public function testMock()\n" +
                "    {\n" +
                "        \$this->getMockClass(PhpUnitMockStringTarget::class, array('<caret>publicMethod'));\n" +
                "    }\n" +
                "}\n"
        ))
    }

    fun testMethodMockMethodsUseMethodMockFilter() {
        for (methodName in arrayOf(
                "resetMethodCalledStack",
                "getCalledArgs",
                "isMethodCalled",
                "countMethodCalled",
                "revertMethod",
                "interceptMethodByCode",
                "interceptMethod",
                "mockMethodResult",
                "mockMethodResultByMap",
                "revertMethodResult",
                "callProtectedMethod"
        )) {
            assertFilter(MethodMockFilter::class.java, findFilter("<?php\n" +
                    "class FilterFactoryMethodMock extends PHPUnit_Framework_TestCase\n" +
                    "{\n" +
                    "    public function testMock()\n" +
                    "    {\n" +
                    "        MethodMock::" + methodName + "(PhpUnitMockStringTarget::class, '<caret>publicMethod');\n" +
                    "    }\n" +
                    "}\n"
            ))
        }
    }

    fun testPhpUnitHelperUsesMethodMockFilter() {
        assertFilter(MethodMockFilter::class.java, findFilter("<?php\n" +
                "class FilterFactoryPhpUnitHelper extends PHPUnit_Framework_TestCase\n" +
                "{\n" +
                "    public function testMock()\n" +
                "    {\n" +
                "        PHPUnit_Helper::callProtectedMethod(PhpUnitMockStringTarget::class, '<caret>protectedMethod');\n" +
                "    }\n" +
                "}\n"
        ))
    }

    private fun findFilter(content: String): Filter? {
        myFixture.configureByText(PhpFileType.INSTANCE, content)

        var psiElement = myFixture.file.findElementAt(myFixture.caretOffset)
        var stringLiteralExpression = PsiTreeUtil.getParentOfType(psiElement, StringLiteralExpression::class.java)
        assertNotNull(stringLiteralExpression)

        return FilterFactory.getFilter(stringLiteralExpression!!)
    }

    private fun assertFilter(expected: Class<out Filter>, filter: Filter?) {
        assertNotNull(filter)
        assertTrue("Expected " + expected.name + " but got " + filter!!.javaClass.name, expected.isInstance(filter))
    }
}
