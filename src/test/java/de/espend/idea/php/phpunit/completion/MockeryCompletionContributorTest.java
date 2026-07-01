package de.espend.idea.php.phpunit.completion;

import com.jetbrains.php.lang.PhpFileType;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;

public class MockeryCompletionContributorTest extends PhpUnitLightCodeInsightFixtureTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("common/fixture/MockeryClasses.php");
    }

    @Override
    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit";
    }

    public void testCompletionForClassMethodAreProvidedForExpectsWithoutReturns() {
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
                        "private $dependency;\n" +
                        "   public function setUp(): void\n" +
                        "   {\n" +
                        "       parent::setUp();\n"+
                        "       $this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                        "   }\n" +
                        "" +
                        "   public function testInvokeWithExpects(): void\n" +
                        "   {\n" +
                        "       $this->dependency->expects('<caret>');\n" +
                        "   }\n" +
                        "}",
                "calledMethod"
        );
    }
    public void testCompletionForClassMethodAreProvidedForExpects() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       $this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForAllows() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithAllows(): void\n" +
                "   {\n" +
                "       $this->dependency->allows('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }
    public void testCompletionForClassMethodAreProvidedForShouldReceive() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShould(): void\n" +
                "   {\n" +
                "       $this->dependency->shouldReceive('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForShouldNotReceive() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShouldNot(): void\n" +
                "   {\n" +
                "       $this->dependency->shouldNotReceive('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForShouldReceiveTwoMethodsFirst() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShould(): void\n" +
                "   {\n" +
                "       $this->dependency->shouldReceive('<caret>','secondCalledMethod')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForShouldReceiveTwoMethodsSecond() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShould(): void\n" +
                "   {\n" +
                "       $this->dependency->shouldReceive('calledMethod','<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "secondCalledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForShouldNotReceiveTwoMethodsFirst() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShouldNot(): void\n" +
                "   {\n" +
                "       $this->dependency->shouldNotReceive('<caret>','secondCalledMethod')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForShouldNotReceiveTwoMethodsSecond() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShouldNot(): void\n" +
                "   {\n" +
                "       $this->dependency->shouldNotReceive('calledMethod','<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "secondCalledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForShouldReceiveTwoMethodsFirstAlternative() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShould(): void\n" +
                "   {\n" +
                "       $this->dependency->shouldReceive(['<caret>' => 'mocked result','secondCalledMethod' => 'mocked result']);\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForShouldReceiveTwoMethodsSecondAlternative() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShould(): void\n" +
                "   {\n" +
                "       $this->dependency->shouldReceive(['calledMethod' => 'mocked result','<caret>' => 'mocked result'])->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "secondCalledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForSpyExpects() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::spy(Dependency::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       $this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForSpyAllows() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::spy(Dependency::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithAllows(): void\n" +
                "   {\n" +
                "       $this->dependency->allows('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForSpyShouldReceive() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::spy(Dependency::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShouldReceive(): void\n" +
                "   {\n" +
                "       $this->dependency->shouldReceive('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForSpyShouldNotReceive() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::spy(Dependency::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShouldNotReceive(): void\n" +
                "   {\n" +
                "       $this->dependency->shouldNotReceive('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForSpyShouldHaveReceived() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::spy(Dependency::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShouldHaveReceived(): void\n" +
                "   {\n" +
                "       $this->dependency->shouldHaveReceived('<caret>');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForSpyShouldNotHaveReceived() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::spy(Dependency::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShouldNotHaveReceived(): void\n" +
                "   {\n" +
                "       $this->dependency->shouldNotHaveReceived('<caret>');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForExpectsAliasCreateMock() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock('alias:MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       $this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForShouldReceiveGeneratedPartialCreateMethodDefault() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock(Dependency::class . \"[calledMethod]\");\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShould(): void\n" +
                "   {\n" +
                "       $this->dependency->shouldReceive('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForShouldReceiveGeneratedPartialCreateMethodFQN() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency[calledMethod]');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShould(): void\n" +
                "   {\n" +
                "       $this->dependency->shouldReceive('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForExpectsCreateMockFromInterface() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock(DependencyInterface::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       $this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForExpectsCreateMockFromInterfaceAndAlternativeInterface() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock(Dependency::class, AlternativeInterface::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       $this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod","alternativeCalledMethod"
        );
    }


    public void testCompletionForClassMethodAreProvidedForExpectsCreateMockFromInterfaceAndAlternativeInterfaceAsList() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency, MockeryPlugin\\DemoProject\\AlternativeInterface');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       $this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod","alternativeCalledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForExpectsCreateMockFromDependencyWithConstructor() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock(DependencyWithConstructor::class, ['suffix']);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       $this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForExpectsCreateMockFromDependencyWithConstructorAndAlternativeInterface() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock(DependencyWithConstructor::class, AlternativeInterface::class, ['suffix']);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       $this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod","alternativeCalledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForExpectsCreateMockFromDependencyWithConstructorFQN() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\DependencyWithConstructor', ['suffix']);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       $this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForExpectsCreateMockFromDependencyWithConstructorAndAlternativeInterfaceFQN() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\DependencyWithConstructor', 'MockeryPlugin\\DemoProject\\AlternativeInterface', ['suffix']);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       $this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod","alternativeCalledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForExpectsCreateNamedMock() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::namedMock('SomeName', Dependency::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       $this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForExpectsOverloadCreateMock() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock('overload:MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       $this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForExpectsProxyCreateMock() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock(new Dependency);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       $this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForExpectsPartialMock() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock(Dependency::class)->makePartial();\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       $this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForExpectsPartialMockFQN() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency')->makePartial();\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       $this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForExpectsPartialMockWithAlternativeInterface() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock(Dependency::class, AlternativeInterface::class)->makePartial();\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       $this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod","alternativeCalledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForExpectsPartialMockWithAlternativeInterfaceFQN() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency, MockeryPlugin\\DemoProject\\AlternativeInterface')->makePartial();\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       $this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod","alternativeCalledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForSpyExpectsFQN() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::spy('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       $this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForSpyExpectsCreateFromInterface() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::spy(DependencyInterface::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       $this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForSpyExpectsCreateFromDependencyAndInterface() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::spy(Dependency::class, AlternativeInterface::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       $this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod","alternativeCalledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForSpyExpectsCreateFromDependencyAndInterfaceAsList() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::spy('MockeryPlugin\\DemoProject\\Dependency, MockeryPlugin\\DemoProject\\AlternativeInterface');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       $this->dependency->expects('<caret>')->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod","alternativeCalledMethod"
        );
    }


    public void testCompletionForClassMethodAreProvidedForCreateGeneratedPartial() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock(Dependency::class . \"[<caret>]\");\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForCreateGeneratedPartialFromString() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency[<caret>]');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForExpectsWithArrayElementMethodName() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       $this->dependency->expects(['<caret>'])->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }

    public void testCompletionForClassMethodAreProvidedForExpectsWithArrayHashElementMethodName() {
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
                "private $dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       $this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       $this->dependency->expects(['<caret>'=>'returnValue'])->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                "calledMethod"
        );
    }
}