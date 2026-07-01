package de.espend.idea.php.phpunit.references;

import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;

public class MockeryReferenceContributorTest extends PhpUnitLightCodeInsightFixtureTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("common/fixture/MockeryClasses.php");
    }

    @Override
    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit";
    }

    public void testThatReferencesForClassMethodAreProvidedForExpects() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForAllows() {
        assertReferencesMatch(
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
                "       $this->dependency->allows('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }
    public void testThatReferencesForClassMethodAreProvidedForShouldReceive() {
        assertReferencesMatch(
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
                "       $this->dependency->shouldReceive('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForShouldNotReceive() {
        assertReferencesMatch(
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
                "       $this->dependency->shouldNotReceive('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForShouldReceiveTwoMethodsFirst() {
        assertReferencesMatch(
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
                "       $this->dependency->shouldReceive('called<caret>Method','secondCalledMethod')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForShouldReceiveTwoMethodsSecond() {
        assertReferencesMatch(
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
                "       $this->dependency->shouldReceive('calledMethod','secondCalled<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("secondCalledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForShouldNotReceiveTwoMethodsFirst() {
        assertReferencesMatch(
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
                "       $this->dependency->shouldNotReceive('called<caret>Method','secondCalledMethod')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForShouldNotReceiveTwoMethodsSecond() {
        assertReferencesMatch(
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
                "       $this->dependency->shouldNotReceive('calledMethod','secondCalled<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("secondCalledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForShouldReceiveTwoMethodsFirstAlt() {
        assertReferencesMatch(
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
                "       $this->dependency->shouldReceive(['called<caret>Method' => 'mocked result','secondCalledMethod' => 'mocked result']);\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForShouldReceiveTwoMethodsSecondAlt() {
        assertReferencesMatch(
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
                "       $this->dependency->shouldReceive(['calledMethod' => 'mocked result','secondCalled<caret>Method' => 'mocked result'])->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("secondCalledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForSpyExpects() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForSpyAllows() {
        assertReferencesMatch(
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
                "       $this->dependency->allows('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForSpyShouldReceive() {
        assertReferencesMatch(
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
                "       $this->dependency->shouldReceive('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForSpyShouldNotReceive() {
        assertReferencesMatch(
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
                "       $this->dependency->shouldNotReceive('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForSpyShouldHaveReceived() {
        assertReferencesMatch(
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
                "       $this->dependency->shouldHaveReceived('called<caret>Method');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForSpyShouldNotHaveReceived() {
        assertReferencesMatch(
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
                "       $this->dependency->shouldNotHaveReceived('called<caret>Method');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForExpectsAliasCreateMock() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForShouldReceiveGeneratedPartialCreateMethodDefault() {
        assertReferencesMatch(
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
                "       $this->dependency->shouldReceive('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForShouldReceiveGeneratedPartialCreateMethodFQN() {
        assertReferencesMatch(
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
                "       $this->dependency->shouldReceive('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForExpectsCreateMockFromInterface() {
//
        assertReferencesMatch(
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
                "       $this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForExpectsCreateMockFromInterfaceAndAlternativeInterface() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForExpectsCreateMockFromInterfaceAndAlternativeInterfaceAltCalledMethod() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('alternativeCalled<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("alternativeCalledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForExpectsCreateMockFromInterfaceAndAlternativeInterfaceAsList() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForExpectsCreateMockFromInterfaceAndAlternativeInterfaceAsListAltCalledMethod() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('alternativeCalled<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("alternativeCalledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForExpectsCreateMockFromDependencyWithConstructor() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForExpectsCreateMockFromDependencyWithConstructorAndAlternativeInterface() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForExpectsCreateMockFromDependencyWithConstructorAndAlternativeInterfaceAltCalledMethod() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('alternativeCalled<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("alternativeCalledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForExpectsCreateMockFromDependencyWithConstructorFQN() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForExpectsCreateMockFromDependencyWithConstructorAndAlternativeInterfaceFQN() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForExpectsCreateMockFromDependencyWithConstructorAndAlternativeInterfaceFQNAltCalledMethod() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('alternativeCalled<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("alternativeCalledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForExpectsCreateNamedMock() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForExpectsOverloadCreateMock() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForExpectsProxyCreateMock() {
//       Won't work
        assertReferencesMatch(
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
                "       $this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForExpectsPartialMock() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForExpectsPartialMockFQN() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForExpectsPartialMockWithAlternativeInterface() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForExpectsPartialMockWithAlternativeInterfaceAltCalledMethod() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('alternativeCalled<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("alternativeCalledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForExpectsPartialMockWithAlternativeInterfaceFQN() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForExpectsPartialMockWithAlternativeInterfaceFQNAltCalledMethod() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('alternativeCalled<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("alternativeCalledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForSpyExpectsFQN() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForSpyExpectsCreateFromInterface() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForSpyExpectsCreateFromDependencyAndInterface() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForSpyExpectsCreateFromDependencyAndInterfaceAltCalledMethod() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('alternativeCalled<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("alternativeCalledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForSpyExpectsCreateFromDependencyAndInterfaceAsList() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForSpyExpectsCreateFromDependencyAndInterfaceAsListAltCalledMethod() {
        assertReferencesMatch(
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
                "       $this->dependency->expects('alternativeCalled<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("alternativeCalledMethod")
        );
    }

    // Tests for Generated partial declaration

    public void testThatReferencesForClassMethodAreProvidedForCreateGeneratedPartial() {
        assertReferencesMatch(
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
                "       $this->dependency = Mockery::mock(Dependency::class . \"[calle<caret>dMethod]\");\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShould(): void\n" +
                "   {\n" +
                "       $this->dependency->shouldReceive('calledMethod')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForCreateGeneratedPartialFromString() {
        assertReferencesMatch(
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
                "       $this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency[calle<caret>dMethod]');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShould(): void\n" +
                "   {\n" +
                "       $this->dependency->shouldReceive('calledMethod')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForExpectsWithArrayElementMethodName() {
        assertReferencesMatch(
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
                "       $this->dependency->expects(['called<caret>Method'])->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }

    public void testThatReferencesForClassMethodAreProvidedForExpectsWithArrayHashElementMethodName() {
        assertReferencesMatch(
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
                "       $this->dependency->expects(['called<caret>Method'=>'returnValue'])->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                PlatformPatterns.psiElement(Method.class).withName("calledMethod")
        );
    }
}