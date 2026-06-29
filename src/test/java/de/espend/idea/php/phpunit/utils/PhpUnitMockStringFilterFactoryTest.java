package de.espend.idea.php.phpunit.utils;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;
import de.espend.idea.php.phpunit.utils.mockstring.Filter;
import de.espend.idea.php.phpunit.utils.mockstring.FilterFactory;
import de.espend.idea.php.phpunit.utils.mockstring.MethodMockFilter;
import de.espend.idea.php.phpunit.utils.mockstring.MockBuilderFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhpUnitMockStringFilterFactoryTest extends PhpUnitLightCodeInsightFixtureTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("common/fixture/PhpUnitMockStringClasses.php");
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit";
    }

    public void testWrongParameterPositionDoesNotCreateFilter() {
        assertNull(findFilter("<?php\n" +
                "class FilterFactoryWrongParameterPosition extends PHPUnit_Framework_TestCase\n" +
                "{\n" +
                "    public function testMock()\n" +
                "    {\n" +
                "        $this->getMock('<caret>PhpUnitMockStringTarget', array('publicMethod'));\n" +
                "    }\n" +
                "}\n"
        ));
    }

    public void testGetMockForAbstractClassUsesSeventhParameter() {
        assertFilter(MockBuilderFilter.class, findFilter("<?php\n" +
                "class FilterFactoryAbstractClassParameter extends PHPUnit_Framework_TestCase\n" +
                "{\n" +
                "    public function testMock()\n" +
                "    {\n" +
                "        $this->getMockForAbstractClass(PhpUnitMockAbstractTarget::class, array(), '', true, true, true, array('<caret>publicMethod'));\n" +
                "    }\n" +
                "}\n"
        ));
    }

    public void testGetMockForTraitUsesSeventhParameter() {
        assertFilter(MockBuilderFilter.class, findFilter("<?php\n" +
                "class FilterFactoryTraitParameter extends PHPUnit_Framework_TestCase\n" +
                "{\n" +
                "    public function testMock()\n" +
                "    {\n" +
                "        $this->getMockForTrait(PhpUnitMockTraitTarget::class, array(), '', true, true, true, array('<caret>traitMethod'));\n" +
                "    }\n" +
                "}\n"
        ));
    }

    public void testGetMockClassUsesSecondParameter() {
        assertFilter(MockBuilderFilter.class, findFilter("<?php\n" +
                "class FilterFactoryGetMockClassParameter extends PHPUnit_Framework_TestCase\n" +
                "{\n" +
                "    public function testMock()\n" +
                "    {\n" +
                "        $this->getMockClass(PhpUnitMockStringTarget::class, array('<caret>publicMethod'));\n" +
                "    }\n" +
                "}\n"
        ));
    }

    public void testMethodMockMethodsUseMethodMockFilter() {
        for (String methodName : new String[]{
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
        }) {
            assertFilter(MethodMockFilter.class, findFilter("<?php\n" +
                    "class FilterFactoryMethodMock extends PHPUnit_Framework_TestCase\n" +
                    "{\n" +
                    "    public function testMock()\n" +
                    "    {\n" +
                    "        MethodMock::" + methodName + "(PhpUnitMockStringTarget::class, '<caret>publicMethod');\n" +
                    "    }\n" +
                    "}\n"
            ));
        }
    }

    public void testPhpUnitHelperUsesMethodMockFilter() {
        assertFilter(MethodMockFilter.class, findFilter("<?php\n" +
                "class FilterFactoryPhpUnitHelper extends PHPUnit_Framework_TestCase\n" +
                "{\n" +
                "    public function testMock()\n" +
                "    {\n" +
                "        PHPUnit_Helper::callProtectedMethod(PhpUnitMockStringTarget::class, '<caret>protectedMethod');\n" +
                "    }\n" +
                "}\n"
        ));
    }

    @Nullable
    private Filter findFilter(@NotNull String content) {
        myFixture.configureByText(PhpFileType.INSTANCE, content);

        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        StringLiteralExpression stringLiteralExpression = PsiTreeUtil.getParentOfType(psiElement, StringLiteralExpression.class);
        assertNotNull(stringLiteralExpression);

        return FilterFactory.getFilter(stringLiteralExpression);
    }

    private void assertFilter(@NotNull Class<? extends Filter> expected, @Nullable Filter filter) {
        assertNotNull(filter);
        assertTrue("Expected " + expected.getName() + " but got " + filter.getClass().getName(), expected.isInstance(filter));
    }
}
