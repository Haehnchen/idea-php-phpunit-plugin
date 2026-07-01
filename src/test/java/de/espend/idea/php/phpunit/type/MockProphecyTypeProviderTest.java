package de.espend.idea.php.phpunit.type;

import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see MockProphecyTypeProvider
 */
public class MockProphecyTypeProviderTest extends PhpUnitLightCodeInsightFixtureTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("classes.php");
    }

    @Override
    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/phpunit/type/fixtures";
    }

    public void testResolveForPhpunitMock() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var $t \\PHPUnit\\Framework\\TestCase */\n" +
                "$t->createMock(Foo::class)->b<caret>ar();",
            PlatformPatterns.psiElement(Method.class).withName("bar")
        );

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var $t \\PHPUnit\\Framework\\TestCase */\n" +
                "$t->createMock('Foo')->b<caret>ar();",
            PlatformPatterns.psiElement(Method.class).withName("bar")
        );
    }

    public void testResolveForPhpunitLegacyMockMethods() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var $t \\PHPUnit\\Framework\\TestCase */\n" +
                "$t->getMock(Foo::class)->b<caret>ar();",
            PlatformPatterns.psiElement(Method.class).withName("bar")
        );

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var $t \\PHPUnit\\Framework\\TestCase */\n" +
                "$t->getMockForAbstractClass(Foo::class)->b<caret>ar();",
            PlatformPatterns.psiElement(Method.class).withName("bar")
        );

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var $t \\PHPUnit\\Framework\\TestCase */\n" +
                "$t->getMockForTrait(Foo::class)->b<caret>ar();",
            PlatformPatterns.psiElement(Method.class).withName("bar")
        );
    }

    public void testResolveForPhpunitGetMockClass() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var $t \\PHPUnit\\Framework\\TestCase */\n" +
                "$t->getMockClass(Foo::class)->b<caret>ar();",
            PlatformPatterns.psiElement(Method.class).withName("bar")
        );
    }

    public void testResolveForLegacyPhpunitTestCaseMock() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var $t \\PHPUnit_Framework_TestCase */\n" +
                "$t->createMock(Foo::class)->b<caret>ar();",
            PlatformPatterns.psiElement(Method.class).withName("bar")
        );
    }

    public void testResolveForLegacyPhpunitTestCaseMockMethods() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var $t \\PHPUnit_Framework_TestCase */\n" +
                "$t->getMock(Foo::class)->b<caret>ar();",
            PlatformPatterns.psiElement(Method.class).withName("bar")
        );

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var $t \\PHPUnit_Framework_TestCase */\n" +
                "$t->getMockClass(Foo::class)->b<caret>ar();",
            PlatformPatterns.psiElement(Method.class).withName("bar")
        );

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var $t \\PHPUnit_Framework_TestCase */\n" +
                "$t->getMockForAbstractClass(Foo::class)->b<caret>ar();",
            PlatformPatterns.psiElement(Method.class).withName("bar")
        );

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var $t \\PHPUnit_Framework_TestCase */\n" +
                "$t->getMockForTrait(Foo::class)->b<caret>ar();",
            PlatformPatterns.psiElement(Method.class).withName("bar")
        );
    }

    public void testResolveForProphecyMock() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var $t \\PHPUnit\\Framework\\TestCase */\n" +
                "$t->prophesize(Foo::class)->b<caret>ar();",
            PlatformPatterns.psiElement(Method.class).withName("bar")
        );
    }

    public void testResolveForProphecyProphetMock() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var $t \\Prophecy\\Prophet */\n" +
                "$t->prophesize(Foo::class)->b<caret>ar();",
            PlatformPatterns.psiElement(Method.class).withName("bar")
        );
    }

    public void testResolveForProphecyMockWithStringClass() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var $t \\PHPUnit\\Framework\\TestCase */\n" +
                "$t->prophesize('Foo')->b<caret>ar();",
            PlatformPatterns.psiElement(Method.class).withName("bar")
        );
    }

    public void testResolveForProphecyMockWithStringClassWithTrait() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var $t \\Prophecy\\PhpUnit\\ProphecyTrait */\n" +
                "$t->prophesize('Foo')->b<caret>ar();",
            PlatformPatterns.psiElement(Method.class).withName("bar")
        );
    }
}
