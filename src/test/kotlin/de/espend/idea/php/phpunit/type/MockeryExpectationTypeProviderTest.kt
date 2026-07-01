package de.espend.idea.php.phpunit.type

import com.intellij.patterns.PlatformPatterns
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.elements.Method
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase

class MockeryExpectationTypeProviderTest : PhpUnitLightCodeInsightFixtureTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("common/fixture/MockeryClasses.php")
    }

    override fun getTestDataPath(): String {
        return "src/test/java/de/espend/idea/php/phpunit/"
    }

    fun testExpectationTypeProvidedForAllowsMockedMethodOnField() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                        "namespace MockeryPlugin\\DemoProject;\n" +
                        "use Mockery;\n" +
                        "use Mockery\\MockInterface;\n" +
                        "use Mockery\\Adapter\\Phpunit\\MockeryTestCase\n" +
                        "class MainClassWithMockeryTest extends MockeryTestCase\n" +
                        "{\n" +
                        "/** @var Dependency|MockInterface */\n" +
                        "private \$dependency;\n" +
                        "   public function setUp(): void\n" +
                        "   {\n" +
                        "       parent::setUp();\n" +
                        "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                        "   }\n" +
                        "" +
                        "   public function testInvokeWithExpects(): void\n" +
                        "   {\n" +
                        "       \$this->dependency->allows()->calledMethod('parameter')->and<caret>Returns('mocked result');\n" +
                        "   }\n" +
                        "}\n",
                PlatformPatterns.psiElement(Method::class.java).withName("andReturns")
        )
    }

    fun testExpectationTypeProvidedForExpectsMockedMethodOnField() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                        "namespace MockeryPlugin\\DemoProject;\n" +
                        "use Mockery;\n" +
                        "use Mockery\\MockInterface;\n" +
                        "use Mockery\\Adapter\\Phpunit\\MockeryTestCase\n" +
                        "class MainClassWithMockeryTest extends MockeryTestCase\n" +
                        "{\n" +
                        "/** @var Dependency|MockInterface */\n" +
                        "private \$dependency;\n" +
                        "   public function setUp(): void\n" +
                        "   {\n" +
                        "       parent::setUp();\n" +
                        "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                        "   }\n" +
                        "" +
                        "   public function testInvokeWithExpects(): void\n" +
                        "   {\n" +
                        "       \$this->dependency->expects()->calledMethod('parameter')->and<caret>Returns('mocked result');\n" +
                        "   }\n" +
                        "}\n",
                PlatformPatterns.psiElement(Method::class.java).withName("andReturns")
        )
    }

    fun testExpectationTypeProvidedForShouldReceiveMockedMethodOnField() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                        "namespace MockeryPlugin\\DemoProject;\n" +
                        "use Mockery;\n" +
                        "use Mockery\\MockInterface;\n" +
                        "use Mockery\\Adapter\\Phpunit\\MockeryTestCase\n" +
                        "class MainClassWithMockeryTest extends MockeryTestCase\n" +
                        "{\n" +
                        "/** @var Dependency|MockInterface */\n" +
                        "private \$dependency;\n" +
                        "   public function setUp(): void\n" +
                        "   {\n" +
                        "       parent::setUp();\n" +
                        "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                        "   }\n" +
                        "" +
                        "   public function testInvokeWithExpects(): void\n" +
                        "   {\n" +
                        "       \$this->dependency->shouldReceive()->calledMethod('parameter')->and<caret>Return('mocked result');\n" +
                        "   }\n" +
                        "}\n",
                PlatformPatterns.psiElement(Method::class.java).withName("andReturn")
        )
    }

    fun testExpectationTypeProvidedForShouldNotReceiveMockedMethodOnField() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                        "namespace MockeryPlugin\\DemoProject;\n" +
                        "use Mockery;\n" +
                        "use Mockery\\MockInterface;\n" +
                        "use Mockery\\Adapter\\Phpunit\\MockeryTestCase\n" +
                        "class MainClassWithMockeryTest extends MockeryTestCase\n" +
                        "{\n" +
                        "/** @var Dependency|MockInterface */\n" +
                        "private \$dependency;\n" +
                        "   public function setUp(): void\n" +
                        "   {\n" +
                        "       parent::setUp();\n" +
                        "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                        "   }\n" +
                        "" +
                        "   public function testInvokeWithExpects(): void\n" +
                        "   {\n" +
                        "       \$this->dependency->shouldReceive()->shouldNotReceive('parameter')->wi<caret>th('arg');\n" +
                        "   }\n" +
                        "}\n",
                PlatformPatterns.psiElement(Method::class.java).withName("with")
        )
    }

    fun testExpectationTypeProvidedForShouldHaveReceivedMockedMethodOnField() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                        "namespace MockeryPlugin\\DemoProject;\n" +
                        "use Mockery;\n" +
                        "use Mockery\\MockInterface;\n" +
                        "use Mockery\\Adapter\\Phpunit\\MockeryTestCase\n" +
                        "class MainClassWithMockeryTest extends MockeryTestCase\n" +
                        "{\n" +
                        "/** @var Dependency|MockInterface */\n" +
                        "private \$dependency;\n" +
                        "   public function setUp(): void\n" +
                        "   {\n" +
                        "       parent::setUp();\n" +
                        "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                        "   }\n" +
                        "" +
                        "   public function testInvokeWithExpects(): void\n" +
                        "   {\n" +
                        "       \$this->dependency->shouldHaveReceived()->calledMethod('parameter')->wi<caret>th('parameter');\n" +
                        "   }\n" +
                        "}\n",
                PlatformPatterns.psiElement(Method::class.java).withName("with")
        )
    }

    fun testExpectationTypeProvidedForAllowsMockedMethodOnVariable() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                        "namespace MockeryPlugin\\DemoProject;\n" +
                        "use Mockery;\n" +
                        "use Mockery\\MockInterface;\n" +
                        "use Mockery\\Adapter\\Phpunit\\MockeryTestCase\n" +
                        "class MainClassWithMockeryTest extends MockeryTestCase\n" +
                        "{\n" +
                        "/** @var Dependency|MockInterface */\n" +
                        "private \$dependency;\n" +
                        "   public function setUp(): void\n" +
                        "   {\n" +
                        "       parent::setUp();\n" +
                        "       \$x = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                        "       \$x->allows()->calledMethod('parameter')->and<caret>Returns('mocked result');\n" +
                        "   }\n" +
                        "}\n",
                PlatformPatterns.psiElement(Method::class.java).withName("andReturns")
        )
    }

    fun testExpectationTypeProvidedForAllowsMockedMethodWhenSplitAfterAllows() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                        "namespace MockeryPlugin\\DemoProject;\n" +
                        "use Mockery;\n" +
                        "use Mockery\\MockInterface;\n" +
                        "use Mockery\\Adapter\\Phpunit\\MockeryTestCase\n" +
                        "class MainClassWithMockeryTest extends MockeryTestCase\n" +
                        "{\n" +
                        "/** @var Dependency|MockInterface */\n" +
                        "private \$dependency;\n" +
                        "   public function setUp(): void\n" +
                        "   {\n" +
                        "       parent::setUp();\n" +
                        "       \$x = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                        "       \$y = \$x->allows();\n" +
                        "       \$y->calledMethod('parameter')->and<caret>Returns('mocked result');\n" +
                        "   }\n" +
                        "}\n",
                PlatformPatterns.psiElement(Method::class.java).withName("andReturns")
        )
    }

    fun testExpectationTypeProvidedForAllowsMockedMethodWhenSplitAfterMockedMethod() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                        "namespace MockeryPlugin\\DemoProject;\n" +
                        "use Mockery;\n" +
                        "use Mockery\\MockInterface;\n" +
                        "use Mockery\\Adapter\\Phpunit\\MockeryTestCase\n" +
                        "class MainClassWithMockeryTest extends MockeryTestCase\n" +
                        "{\n" +
                        "/** @var Dependency|MockInterface */\n" +
                        "private \$dependency;\n" +
                        "   public function setUp(): void\n" +
                        "   {\n" +
                        "       parent::setUp();\n" +
                        "       \$x = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                        "       \$y = \$x->allows()->calledMethod('parameter');\n" +
                        "       \$y->and<caret>Returns('mocked result');\n" +
                        "   }\n" +
                        "}\n",
                PlatformPatterns.psiElement(Method::class.java).withName("andReturns")
        )
    }

    fun testExpectationTypeProvidedForUnknownMockedMethod() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                        "namespace MockeryPlugin\\DemoProject;\n" +
                        "use Mockery;\n" +
                        "use Mockery\\MockInterface;\n" +
                        "use Mockery\\Adapter\\Phpunit\\MockeryTestCase\n" +
                        "class MainClassWithMockeryTest extends MockeryTestCase\n" +
                        "{\n" +
                        "/** @var Dependency|MockInterface */\n" +
                        "private \$dependency;\n" +
                        "   public function setUp(): void\n" +
                        "   {\n" +
                        "       parent::setUp();\n" +
                        "       \$x = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                        "       \$x->allows()->unknown('parameter')->and<caret>Returns('mocked result');\n" +
                        "   }\n" +
                        "}\n",
                PlatformPatterns.psiElement(Method::class.java).withName("andReturns")
        )
    }

}
