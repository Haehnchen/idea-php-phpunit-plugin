package de.espend.idea.php.phpunit.type

import com.intellij.patterns.PlatformPatterns
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.elements.Method
import de.espend.idea.php.phpunit.PhpUnitLightCodeInsightFixtureTestCase

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 * @see MockProphecyTypeProvider
 */
class MockProphecyTypeProviderTest : PhpUnitLightCodeInsightFixtureTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("classes.php")
    }

    override fun getTestDataPath(): String {
        return "src/test/kotlin/de/espend/idea/php/phpunit/type/fixtures"
    }

    fun testResolveForPhpunitMock() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var \$t \\PHPUnit\\Framework\\TestCase */\n" +
                "\$t->createMock(Foo::class)->b<caret>ar();",
            PlatformPatterns.psiElement(Method::class.java).withName("bar")
        )

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var \$t \\PHPUnit\\Framework\\TestCase */\n" +
                "\$t->createMock('Foo')->b<caret>ar();",
            PlatformPatterns.psiElement(Method::class.java).withName("bar")
        )
    }

    fun testResolveForPhpunitLegacyMockMethods() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var \$t \\PHPUnit\\Framework\\TestCase */\n" +
                "\$t->getMock(Foo::class)->b<caret>ar();",
            PlatformPatterns.psiElement(Method::class.java).withName("bar")
        )

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var \$t \\PHPUnit\\Framework\\TestCase */\n" +
                "\$t->getMockForAbstractClass(Foo::class)->b<caret>ar();",
            PlatformPatterns.psiElement(Method::class.java).withName("bar")
        )

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var \$t \\PHPUnit\\Framework\\TestCase */\n" +
                "\$t->getMockForTrait(Foo::class)->b<caret>ar();",
            PlatformPatterns.psiElement(Method::class.java).withName("bar")
        )
    }

    fun testResolveForPhpunitGetMockClass() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var \$t \\PHPUnit\\Framework\\TestCase */\n" +
                "\$t->getMockClass(Foo::class)->b<caret>ar();",
            PlatformPatterns.psiElement(Method::class.java).withName("bar")
        )
    }

    fun testResolveForLegacyPhpunitTestCaseMock() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var \$t \\PHPUnit_Framework_TestCase */\n" +
                "\$t->createMock(Foo::class)->b<caret>ar();",
            PlatformPatterns.psiElement(Method::class.java).withName("bar")
        )
    }

    fun testResolveForLegacyPhpunitTestCaseMockMethods() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var \$t \\PHPUnit_Framework_TestCase */\n" +
                "\$t->getMock(Foo::class)->b<caret>ar();",
            PlatformPatterns.psiElement(Method::class.java).withName("bar")
        )

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var \$t \\PHPUnit_Framework_TestCase */\n" +
                "\$t->getMockClass(Foo::class)->b<caret>ar();",
            PlatformPatterns.psiElement(Method::class.java).withName("bar")
        )

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var \$t \\PHPUnit_Framework_TestCase */\n" +
                "\$t->getMockForAbstractClass(Foo::class)->b<caret>ar();",
            PlatformPatterns.psiElement(Method::class.java).withName("bar")
        )

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var \$t \\PHPUnit_Framework_TestCase */\n" +
                "\$t->getMockForTrait(Foo::class)->b<caret>ar();",
            PlatformPatterns.psiElement(Method::class.java).withName("bar")
        )
    }

    fun testResolveForProphecyMock() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var \$t \\PHPUnit\\Framework\\TestCase */\n" +
                "\$t->prophesize(Foo::class)->b<caret>ar();",
            PlatformPatterns.psiElement(Method::class.java).withName("bar")
        )
    }

    fun testResolveForProphecyProphetMock() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var \$t \\Prophecy\\Prophet */\n" +
                "\$t->prophesize(Foo::class)->b<caret>ar();",
            PlatformPatterns.psiElement(Method::class.java).withName("bar")
        )
    }

    fun testResolveForProphecyMockWithStringClass() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var \$t \\PHPUnit\\Framework\\TestCase */\n" +
                "\$t->prophesize('Foo')->b<caret>ar();",
            PlatformPatterns.psiElement(Method::class.java).withName("bar")
        )
    }

    fun testResolveForProphecyMockWithStringClassWithTrait() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE,
            "<?php" +
                "/** @var \$t \\Prophecy\\PhpUnit\\ProphecyTrait */\n" +
                "\$t->prophesize('Foo')->b<caret>ar();",
            PlatformPatterns.psiElement(Method::class.java).withName("bar")
        )
    }
}
