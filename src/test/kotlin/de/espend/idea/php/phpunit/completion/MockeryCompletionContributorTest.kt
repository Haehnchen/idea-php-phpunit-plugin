package de.espend.idea.php.phpunit.completion

import com.jetbrains.php.lang.PhpFileType
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase

class MockeryCompletionContributorTest : PhpUnitLightCodeInsightFixtureTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("common/fixture/MockeryClasses.php")
    }

    override fun getTestDataPath(): String {
        return "src/test/java/de/espend/idea/php/phpunit"
    }

    fun testCompletionForClassMethodAreProvidedForExpectsWithoutReturns() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                        "       parent::setUp();\n"+
                        "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                        "   }\n" +
                        "" +
                        "   public function testInvokeWithExpects(): void\n" +
                        "   {\n" +
                        "       \$this->dependency->expects('<caret>');\n" +
                        "   }\n" +
                        "}",
                "calledMethod"
        )
    }
    fun testCompletionForClassMethodAreProvidedForExpects() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForAllows() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "   public function testInvokeWithAllows(): void\n" +
                "   {\n" +
                "       \$this->dependency->allows('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }
    fun testCompletionForClassMethodAreProvidedForShouldReceive() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "   public function testInvokeWithShould(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldReceive('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForShouldNotReceive() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "   public function testInvokeWithShouldNot(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldNotReceive('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForShouldReceiveTwoMethodsFirst() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "   public function testInvokeWithShould(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldReceive('<caret>','secondCalledMethod')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForShouldReceiveTwoMethodsSecond() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "   public function testInvokeWithShould(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldReceive('calledMethod','<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "secondCalledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForShouldNotReceiveTwoMethodsFirst() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "   public function testInvokeWithShouldNot(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldNotReceive('<caret>','secondCalledMethod')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForShouldNotReceiveTwoMethodsSecond() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "   public function testInvokeWithShouldNot(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldNotReceive('calledMethod','<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "secondCalledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForShouldReceiveTwoMethodsFirstAlternative() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "   public function testInvokeWithShould(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldReceive(['<caret>' => 'mocked result','secondCalledMethod' => 'mocked result']);\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForShouldReceiveTwoMethodsSecondAlternative() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "   public function testInvokeWithShould(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldReceive(['calledMethod' => 'mocked result','<caret>' => 'mocked result'])->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "secondCalledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForSpyExpects() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::spy(Dependency::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForSpyAllows() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::spy(Dependency::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithAllows(): void\n" +
                "   {\n" +
                "       \$this->dependency->allows('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForSpyShouldReceive() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::spy(Dependency::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShouldReceive(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldReceive('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForSpyShouldNotReceive() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::spy(Dependency::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShouldNotReceive(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldNotReceive('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForSpyShouldHaveReceived() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::spy(Dependency::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShouldHaveReceived(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldHaveReceived('<caret>');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForSpyShouldNotHaveReceived() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::spy(Dependency::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShouldNotHaveReceived(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldNotHaveReceived('<caret>');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForExpectsAliasCreateMock() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::mock('alias:MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForShouldReceiveGeneratedPartialCreateMethodDefault() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::mock(Dependency::class . \"[calledMethod]\");\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShould(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldReceive('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForShouldReceiveGeneratedPartialCreateMethodFQN() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency[calledMethod]');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShould(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldReceive('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForExpectsCreateMockFromInterface() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::mock(DependencyInterface::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForExpectsCreateMockFromInterfaceAndAlternativeInterface() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::mock(Dependency::class, AlternativeInterface::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod","alternativeCalledMethod"
        )
    }


    fun testCompletionForClassMethodAreProvidedForExpectsCreateMockFromInterfaceAndAlternativeInterfaceAsList() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency, MockeryPlugin\\DemoProject\\AlternativeInterface');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod","alternativeCalledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForExpectsCreateMockFromDependencyWithConstructor() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::mock(DependencyWithConstructor::class, ['suffix']);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForExpectsCreateMockFromDependencyWithConstructorAndAlternativeInterface() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::mock(DependencyWithConstructor::class, AlternativeInterface::class, ['suffix']);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod","alternativeCalledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForExpectsCreateMockFromDependencyWithConstructorFQN() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\DependencyWithConstructor', ['suffix']);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForExpectsCreateMockFromDependencyWithConstructorAndAlternativeInterfaceFQN() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\DependencyWithConstructor', 'MockeryPlugin\\DemoProject\\AlternativeInterface', ['suffix']);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod","alternativeCalledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForExpectsCreateNamedMock() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::namedMock('SomeName', Dependency::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForExpectsOverloadCreateMock() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::mock('overload:MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForExpectsProxyCreateMock() {
//       Won't work
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::mock(new Dependency);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForExpectsPartialMock() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::mock(Dependency::class)->makePartial();\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForExpectsPartialMockFQN() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency')->makePartial();\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForExpectsPartialMockWithAlternativeInterface() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::mock(Dependency::class, AlternativeInterface::class)->makePartial();\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod","alternativeCalledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForExpectsPartialMockWithAlternativeInterfaceFQN() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency, MockeryPlugin\\DemoProject\\AlternativeInterface')->makePartial();\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod","alternativeCalledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForSpyExpectsFQN() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::spy('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForSpyExpectsCreateFromInterface() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::spy(DependencyInterface::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForSpyExpectsCreateFromDependencyAndInterface() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::spy(Dependency::class, AlternativeInterface::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod","alternativeCalledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForSpyExpectsCreateFromDependencyAndInterfaceAsList() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::spy('MockeryPlugin\\DemoProject\\Dependency, MockeryPlugin\\DemoProject\\AlternativeInterface');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod","alternativeCalledMethod"
        )
    }


    fun testCompletionForClassMethodAreProvidedForCreateGeneratedPartial() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::mock(Dependency::class . \"[<caret>]\");\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForCreateGeneratedPartialFromString() {
        assertCompletionContains(
                PhpFileType.INSTANCE, 
                "<?php\n" +
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
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency[<caret>]');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForExpectsWithArrayElementMethodName() {
        assertCompletionContains(
                PhpFileType.INSTANCE,
                "<?php\n" +
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
                "       \$this->dependency->expects(['<caret>'])->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }

    fun testCompletionForClassMethodAreProvidedForExpectsWithArrayHashElementMethodName() {
        assertCompletionContains(
                PhpFileType.INSTANCE,
                "<?php\n" +
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
                "       \$this->dependency->expects(['<caret>'=>'returnValue'])->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        )
    }
}