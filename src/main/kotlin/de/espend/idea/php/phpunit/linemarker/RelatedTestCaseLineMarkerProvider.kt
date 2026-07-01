package de.espend.idea.php.phpunit.linemarker

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.jetbrains.php.PhpIcons
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.phpunit.PhpUnitUtil
import de.espend.idea.php.phpunit.utils.PhpElementsUtil
import org.apache.commons.lang3.StringUtils
import java.util.ArrayList
import java.util.Arrays
import java.util.HashSet
import java.util.regex.Pattern

/**
 * Attach a test class to a given class name
 *
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
class RelatedTestCaseLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        return null
    }

    override fun collectSlowLineMarkers(
        psiElements: MutableList<out PsiElement>,
        result: MutableCollection<in LineMarkerInfo<*>>
    ) {
        // we need project element; so get it from first item
        if (psiElements.size == 0) {
            return
        }

        for (psiElement in psiElements) {
            if (PhpElementsUtil.getClassNamePattern().accepts(psiElement)) {
                visitClassName(psiElement, result)
            }
        }
    }

    private fun visitClassName(psiElement: PsiElement, results: MutableCollection<in LineMarkerInfo<*>>) {
        val phpClass = psiElement.context
        if (phpClass !is PhpClass || PhpUnitUtil.isTestClass(phpClass)) {
            return
        }

        val className = StringUtils.stripStart(phpClass.presentableFQN, "\\")

        val testClasses = getTestClassesViaPath(className)
        if (testClasses.size == 0) {
            return
        }

        val instance = PhpIndex.getInstance(psiElement.project)

        val phpClasses: MutableSet<PhpClass> = HashSet()
        for (testClass in testClasses) {
            val anyByFQN = instance.getAnyByFQN("\\$testClass")
            for (aClass in anyByFQN) {
                if (PhpUnitUtil.isTestClass(aClass)) {
                    phpClasses.add(aClass)
                }
            }
        }

        if (phpClasses.size == 0) {
            return
        }

        val builder: NavigationGutterIconBuilder<PsiElement> = NavigationGutterIconBuilder.create(PhpIcons.PHP_TEST_CLASS)
            .setTargets(phpClasses)
            .setTooltipText("Navigate to Test Class")

        results.add(builder.createLineMarkerInfo(psiElement))
    }

    /**
     * Try you find a test class which based on the class namespace
     *
     * Extend class name with a test namespace in between:
     *  - App\Foobar => App\FoobarTest
     *  - App\Foobar => App\Test\FoobarTest
     *  - App\Foobar => App\Tests\FoobarTest
     *  - App\Foobar => Test\App\FoobarTest
     */
    private fun getTestClassesViaPath(className: String): Collection<String> {
        val classNameParts = Arrays.asList(*Pattern.compile("\\\\").split(className))

        classNameParts[classNameParts.size - 1] = classNameParts.last() + "Test"

        val testClasses: MutableCollection<String> = HashSet()
        testClasses.add(className + "Test")
        for (i in classNameParts.indices) {
            val strings = ArrayList(classNameParts)
            strings.add(i, "Test")
            testClasses.add(StringUtils.join(strings, "\\"))

            val strings2 = ArrayList(classNameParts)
            strings2.add(i, "Tests")
            testClasses.add(StringUtils.join(strings2, "\\"))
        }

        return testClasses
    }
}
