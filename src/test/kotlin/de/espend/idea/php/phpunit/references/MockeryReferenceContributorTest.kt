package de.espend.idea.php.phpunit.references

import com.intellij.patterns.PlatformPatterns
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.elements.Method
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase

class MockeryReferenceContributorTest : PhpUnitLightCodeInsightFixtureTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("common/fixture/MockeryClasses.php")
    }

    override fun getTestDataPath(): String {
        return "src/test/kotlin/de/espend/idea/php/phpunit"
    }

    fun testThatReferencesForClassMethodAreProvidedForExpects() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForAllows() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithAllows(): void\n" +
                "   {\n" +
                "       \$this->dependency->allows('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }
    fun testThatReferencesForClassMethodAreProvidedForShouldReceive() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShould(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldReceive('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForShouldNotReceive() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShouldNot(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldNotReceive('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForShouldReceiveTwoMethodsFirst() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShould(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldReceive('called<caret>Method','secondCalledMethod')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForShouldReceiveTwoMethodsSecond() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShould(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldReceive('calledMethod','secondCalled<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("secondCalledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForShouldNotReceiveTwoMethodsFirst() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShouldNot(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldNotReceive('called<caret>Method','secondCalledMethod')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForShouldNotReceiveTwoMethodsSecond() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShouldNot(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldNotReceive('calledMethod','secondCalled<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("secondCalledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForShouldReceiveTwoMethodsFirstAlt() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShould(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldReceive(['called<caret>Method' => 'mocked result','secondCalledMethod' => 'mocked result']);\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForShouldReceiveTwoMethodsSecondAlt() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShould(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldReceive(['calledMethod' => 'mocked result','secondCalled<caret>Method' => 'mocked result'])->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("secondCalledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForSpyExpects() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::spy(Dependency::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForSpyAllows() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::spy(Dependency::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithAllows(): void\n" +
                "   {\n" +
                "       \$this->dependency->allows('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForSpyShouldReceive() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::spy(Dependency::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShouldReceive(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldReceive('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForSpyShouldNotReceive() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::spy(Dependency::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShouldNotReceive(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldNotReceive('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForSpyShouldHaveReceived() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::spy(Dependency::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShouldHaveReceived(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldHaveReceived('called<caret>Method');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForSpyShouldNotHaveReceived() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::spy(Dependency::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShouldNotHaveReceived(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldNotHaveReceived('called<caret>Method');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForExpectsAliasCreateMock() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock('alias:MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForShouldReceiveGeneratedPartialCreateMethodDefault() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock(Dependency::class . \"[calledMethod]\");\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShould(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldReceive('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForShouldReceiveGeneratedPartialCreateMethodFQN() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency[calledMethod]');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShould(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldReceive('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForExpectsCreateMockFromInterface() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock(DependencyInterface::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForExpectsCreateMockFromInterfaceAndAlternativeInterface() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock(Dependency::class, AlternativeInterface::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForExpectsCreateMockFromInterfaceAndAlternativeInterfaceAltCalledMethod() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock(Dependency::class, AlternativeInterface::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('alternativeCalled<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("alternativeCalledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForExpectsCreateMockFromInterfaceAndAlternativeInterfaceAsList() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency, MockeryPlugin\\DemoProject\\AlternativeInterface');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForExpectsCreateMockFromInterfaceAndAlternativeInterfaceAsListAltCalledMethod() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency, MockeryPlugin\\DemoProject\\AlternativeInterface');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('alternativeCalled<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("alternativeCalledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForExpectsCreateMockFromDependencyWithConstructor() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock(DependencyWithConstructor::class, ['suffix']);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForExpectsCreateMockFromDependencyWithConstructorAndAlternativeInterface() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock(DependencyWithConstructor::class, AlternativeInterface::class, ['suffix']);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForExpectsCreateMockFromDependencyWithConstructorAndAlternativeInterfaceAltCalledMethod() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock(DependencyWithConstructor::class, AlternativeInterface::class, ['suffix']);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('alternativeCalled<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("alternativeCalledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForExpectsCreateMockFromDependencyWithConstructorFQN() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\DependencyWithConstructor', ['suffix']);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForExpectsCreateMockFromDependencyWithConstructorAndAlternativeInterfaceFQN() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\DependencyWithConstructor', 'MockeryPlugin\\DemoProject\\AlternativeInterface', ['suffix']);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForExpectsCreateMockFromDependencyWithConstructorAndAlternativeInterfaceFQNAltCalledMethod() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\DependencyWithConstructor', 'MockeryPlugin\\DemoProject\\AlternativeInterface', ['suffix']);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('alternativeCalled<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("alternativeCalledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForExpectsCreateNamedMock() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::namedMock('SomeName', Dependency::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForExpectsOverloadCreateMock() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock('overload:MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForExpectsProxyCreateMock() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock(new Dependency);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForExpectsPartialMock() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock(Dependency::class)->makePartial();\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForExpectsPartialMockFQN() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency')->makePartial();\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForExpectsPartialMockWithAlternativeInterface() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock(Dependency::class, AlternativeInterface::class)->makePartial();\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForExpectsPartialMockWithAlternativeInterfaceAltCalledMethod() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock(Dependency::class, AlternativeInterface::class)->makePartial();\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('alternativeCalled<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("alternativeCalledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForExpectsPartialMockWithAlternativeInterfaceFQN() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency, MockeryPlugin\\DemoProject\\AlternativeInterface')->makePartial();\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForExpectsPartialMockWithAlternativeInterfaceFQNAltCalledMethod() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency, MockeryPlugin\\DemoProject\\AlternativeInterface')->makePartial();\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('alternativeCalled<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("alternativeCalledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForSpyExpectsFQN() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::spy('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForSpyExpectsCreateFromInterface() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::spy(DependencyInterface::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForSpyExpectsCreateFromDependencyAndInterface() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::spy(Dependency::class, AlternativeInterface::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForSpyExpectsCreateFromDependencyAndInterfaceAltCalledMethod() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::spy(Dependency::class, AlternativeInterface::class);\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('alternativeCalled<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("alternativeCalledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForSpyExpectsCreateFromDependencyAndInterfaceAsList() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::spy('MockeryPlugin\\DemoProject\\Dependency, MockeryPlugin\\DemoProject\\AlternativeInterface');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('called<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForSpyExpectsCreateFromDependencyAndInterfaceAsListAltCalledMethod() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::spy('MockeryPlugin\\DemoProject\\Dependency, MockeryPlugin\\DemoProject\\AlternativeInterface');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects('alternativeCalled<caret>Method')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("alternativeCalledMethod")
        )
    }

    // Tests for Generated partial declaration

    fun testThatReferencesForClassMethodAreProvidedForCreateGeneratedPartial() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock(Dependency::class . \"[calle<caret>dMethod]\");\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShould(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldReceive('calledMethod')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForCreateGeneratedPartialFromString() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency[calle<caret>dMethod]');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithShould(): void\n" +
                "   {\n" +
                "       \$this->dependency->shouldReceive('calledMethod')->andReturns('mocked result');\n" +
                "   }\n" +
                "}", 
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForExpectsWithArrayElementMethodName() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects(['called<caret>Method'])->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }

    fun testThatReferencesForClassMethodAreProvidedForExpectsWithArrayHashElementMethodName() {
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
                "private \$dependency;\n" +
                "   public function setUp(): void\n" +
                "   {\n" +
                "       parent::setUp();\n" +
                "       \$this->dependency = Mockery::mock('MockeryPlugin\\DemoProject\\Dependency');\n" +
                "   }\n" +
                "" +
                "   public function testInvokeWithExpects(): void\n" +
                "   {\n" +
                "       \$this->dependency->expects(['called<caret>Method'=>'returnValue'])->andReturns('mocked result');\n" +
                "   }\n" +
                "}",
                PlatformPatterns.psiElement(Method::class.java).withName("calledMethod")
        )
    }
}