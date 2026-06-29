package de.espend.idea.php.phpunit.type;

import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;

public class MockeryMethodNameTypeProviderTest extends PhpUnitLightCodeInsightFixtureTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("common/fixture/MockeryClasses.php");
    }

    @Override
    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit/";
    }

    public void testMockeryAllowsUsingMethodResolveToCorrectMethod() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                        "namespace MockeryPlugin\\DemoProject;\n" +
                        "use Mockery;\n" +
                        "use Mockery\\MockInterface;\n" +
                        "use Mockery\\Adapter\\Phpunit\\MockeryTestCase\n" +
                        "class MainClassWithMockeryTest extends MockeryTestCase\n" +
                        "{\n" +
                        "/** @var Dependency|MockInterface */\n" +
                        "private $dependency;\n" +
                        "   public function setUp(): void\n" +
                        "   {\n" +
                        "       parent::setUp();\n" +
                        "       $this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                        "   }\n" +
                        "" +
                        "   public function testInvokeWithExpects(): void\n" +
                        "   {\n" +
                        "       $this->dependency->allows()->called<caret>Method('parameter')->andReturns('mocked result');\n" +
                        "   }\n" +
                        "}\n",
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testMockeryExpectsUsingMethodResolveToCorrectMethod() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                        "namespace MockeryPlugin\\DemoProject;\n" +
                        "use Mockery;\n" +
                        "use Mockery\\MockInterface;\n" +
                        "use Mockery\\Adapter\\Phpunit\\MockeryTestCase\n" +
                        "class MainClassWithMockeryTest extends MockeryTestCase\n" +
                        "{\n" +
                        "/** @var Dependency|MockInterface */\n" +
                        "private $dependency;\n" +
                        "   public function setUp(): void\n" +
                        "   {\n" +
                        "       parent::setUp();\n" +
                        "       $this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                        "   }\n" +
                        "" +
                        "   public function testInvokeWithExpects(): void\n" +
                        "   {\n" +
                        "       $this->dependency->expects()->called<caret>Method('parameter')->andReturns('mocked result');\n" +
                        "   }\n" +
                        "}\n",
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testMockeryShouldReceiveUsingMethodResolveToCorrectMethod() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                        "namespace MockeryPlugin\\DemoProject;\n" +
                        "use Mockery;\n" +
                        "use Mockery\\MockInterface;\n" +
                        "use Mockery\\Adapter\\Phpunit\\MockeryTestCase\n" +
                        "class MainClassWithMockeryTest extends MockeryTestCase\n" +
                        "{\n" +
                        "/** @var Dependency|MockInterface */\n" +
                        "private $dependency;\n" +
                        "   public function setUp(): void\n" +
                        "   {\n" +
                        "       parent::setUp();\n" +
                        "       $this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                        "   }\n" +
                        "" +
                        "   public function testInvokeWithExpects(): void\n" +
                        "   {\n" +
                        "       $this->dependency->shouldReceive()->called<caret>Method('parameter');\n" +
                        "   }\n" +
                        "}\n",
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testMockeryShouldNotReceiveUsingMethodResolveToCorrectMethod() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                        "namespace MockeryPlugin\\DemoProject;\n" +
                        "use Mockery;\n" +
                        "use Mockery\\MockInterface;\n" +
                        "use Mockery\\Adapter\\Phpunit\\MockeryTestCase\n" +
                        "class MainClassWithMockeryTest extends MockeryTestCase\n" +
                        "{\n" +
                        "/** @var Dependency|MockInterface */\n" +
                        "private $dependency;\n" +
                        "   public function setUp(): void\n" +
                        "   {\n" +
                        "       parent::setUp();\n" +
                        "       $this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                        "   }\n" +
                        "" +
                        "   public function testInvokeWithExpects(): void\n" +
                        "   {\n" +
                        "       $this->dependency->shouldNotReceive()->called<caret>Method('parameter');\n" +
                        "   }\n" +
                        "}\n",
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testMockeryShouldHaveReceivedUsingMethodResolveToCorrectMethod() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                        "namespace MockeryPlugin\\DemoProject;\n" +
                        "use Mockery;\n" +
                        "use Mockery\\MockInterface;\n" +
                        "use Mockery\\Adapter\\Phpunit\\MockeryTestCase\n" +
                        "class MainClassWithMockeryTest extends MockeryTestCase\n" +
                        "{\n" +
                        "/** @var Dependency|MockInterface */\n" +
                        "private $dependency;\n" +
                        "   public function setUp(): void\n" +
                        "   {\n" +
                        "       parent::setUp();\n" +
                        "       $this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                        "   }\n" +
                        "" +
                        "   public function testInvokeWithExpects(): void\n" +
                        "   {\n" +
                        "       $this->dependency->shouldHaveReceived()->called<caret>Method('parameter');\n" +
                        "   }\n" +
                        "}\n",
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

}
