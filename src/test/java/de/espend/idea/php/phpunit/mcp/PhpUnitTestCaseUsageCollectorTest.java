package de.espend.idea.php.phpunit.mcp;

import com.intellij.openapi.application.ApplicationManager;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;
import de.espend.idea.php.phpunit.mcp.collector.PhpUnitTestCaseUsageCollector;

public class PhpUnitTestCaseUsageCollectorTest extends PhpUnitLightCodeInsightFixtureTestCase {
    @Override
    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit/common/fixture";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        myFixture.copyFileToProject("MockeryClasses.php");

        myFixture.addFileToProject("vendor/PHPUnitStubs.php", "<?php\n" +
            "namespace PHPUnit\\Framework {\n" +
            "    abstract class TestCase {\n" +
            "        /** @return \\PHPUnit_Framework_MockObject_MockObject */\n" +
            "        protected function createMock($originalClassName) {}\n" +
            "    }\n" +
            "}\n" +
            "namespace {\n" +
            "    class PHPUnit_Framework_MockObject_MockObject {\n" +
            "        /** @return PHPUnit_Framework_MockObject_Builder_InvocationMocker */\n" +
            "        public function method($constraint) {}\n" +
            "    }\n" +
            "    class PHPUnit_Framework_MockObject_Builder_InvocationMocker {}\n" +
            "}\n"
        );

        myFixture.addFileToProject("src/App/Foo.php", "<?php\n" +
            "namespace App;\n" +
            "class Foo {\n" +
            "    public function calculate(): void {}\n" +
            "    public function other(): void {}\n" +
            "}\n"
        );

        myFixture.addFileToProject("tests/FooTest.php", "<?php\n" +
            "namespace Tests;\n" +
            "use App\\Foo;\n" +
            "class FooTest extends \\PHPUnit\\Framework\\TestCase {\n" +
            "    public function testDirectMethodUsage(): void {\n" +
            "        $foo = new Foo();\n" +
            "        $foo->calculate();\n" +
            "    }\n" +
            "    public function testPhpUnitMockMethodUsage(): void {\n" +
            "        $this->createMock(Foo::class)->method('calculate');\n" +
            "    }\n" +
            "    public function testMockeryMethodUsage(): void {\n" +
            "        $mock = \\Mockery::mock(Foo::class);\n" +
            "        $mock->shouldReceive('calculate');\n" +
            "    }\n" +
            "    public function testOtherMethodUsage(): void {\n" +
            "        $foo = new Foo();\n" +
            "        $foo->other();\n" +
            "    }\n" +
            "}\n"
        );
    }

    public void testFindsTestsForClassMethodTarget() {
        String result = collect("App\\Foo:calculate", null);

        assertTrue(result, result.contains("\\Tests\\FooTest::testDirectMethodUsage"));
        assertTrue(result, result.contains("\\Tests\\FooTest::testPhpUnitMockMethodUsage"));
        assertTrue(result, result.contains("\\Tests\\FooTest::testMockeryMethodUsage"));
        assertFalse(result, result.contains("\\Tests\\FooTest::testOtherMethodUsage"));
    }

    public void testFindsTestsForShortClassMethodTarget() {
        String result = collect("Foo:calculate", null);

        assertTrue(result, result.contains("\\Tests\\FooTest::testDirectMethodUsage"));
        assertTrue(result, result.contains("\\Tests\\FooTest::testPhpUnitMockMethodUsage"));
        assertTrue(result, result.contains("\\Tests\\FooTest::testMockeryMethodUsage"));
    }

    public void testCollectsTargetsFromFileGlob() {
        String result = collect(null, "src/App/Foo.php");

        assertTrue(result, result.contains("\\Tests\\FooTest::testDirectMethodUsage"));
        assertTrue(result, result.contains("\\Tests\\FooTest::testPhpUnitMockMethodUsage"));
        assertTrue(result, result.contains("\\Tests\\FooTest::testMockeryMethodUsage"));
        assertTrue(result, result.contains("\\Tests\\FooTest::testOtherMethodUsage"));
    }

    private String collect(String target, String fileGlob) {
        return ApplicationManager.getApplication().runReadAction((com.intellij.openapi.util.Computable<String>) () ->
            new PhpUnitTestCaseUsageCollector(getProject()).collect(target, fileGlob)
        );
    }
}
