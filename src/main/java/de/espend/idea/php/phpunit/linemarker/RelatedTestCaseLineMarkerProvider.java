package de.espend.idea.php.phpunit.linemarker;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.phpunit.PhpUnitUtil;
import de.espend.idea.php.phpunit.utils.PhpElementsUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Attach a test class to a given class name
 *
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class RelatedTestCaseLineMarkerProvider implements LineMarkerProvider {
    @Nullable
    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element) {
        return null;
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<? extends PsiElement> psiElements, @NotNull Collection<? super LineMarkerInfo<?>> result) {
        // we need project element; so get it from first item
        if(psiElements.size() == 0) {
            return;
        }

        for(PsiElement psiElement: psiElements) {
            if (PhpElementsUtil.getClassNamePattern().accepts(psiElement)) {
                visitClassName(psiElement, result);
            }
        }
    }

    private void visitClassName(@NotNull PsiElement psiElement, Collection<? super LineMarkerInfo<?>> results) {
        PsiElement phpClass = psiElement.getContext();
        if (!(phpClass instanceof PhpClass) || PhpUnitUtil.isTestClass((PhpClass) phpClass)) {
            return;
        }

        String className = StringUtils.stripStart(((PhpClass) phpClass).getPresentableFQN(), "\\");

        Collection<String> testClasses = getTestClassesViaPath(className);
        if (testClasses.size() == 0) {
            return;
        }

        PhpIndex instance = PhpIndex.getInstance(psiElement.getProject());

        Set<PhpClass> phpClasses = new HashSet<>();
        for (String testClass : testClasses) {
            Collection<PhpClass> anyByFQN = instance.getAnyByFQN("\\" + testClass);
            for (PhpClass aClass : anyByFQN) {
                if (PhpUnitUtil.isTestClass(aClass)) {
                    phpClasses.add(aClass);
                }
            }
        }

        if (phpClasses.size() == 0) {
            return;
        }

        NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder.create(PhpIcons.PHP_TEST_CLASS)
            .setTargets(phpClasses)
            .setTooltipText("Navigate to Test Class");

        results.add(builder.createLineMarkerInfo(psiElement));
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
    @NotNull
    private static Collection<String> getTestClassesViaPath(@NotNull String className) {
        List<String> classNameParts = Arrays.asList(className.split("\\\\"));

        classNameParts.set(classNameParts.size() - 1, classNameParts.get(classNameParts.size() - 1) + "Test");

        Collection<String> testClasses = new HashSet<>();
        testClasses.add(className + "Test");
        for (int i = 0; i < classNameParts.size(); i++) {
            List<String> strings = new ArrayList<>(classNameParts);
            strings.add(i, "Test");
            testClasses.add(StringUtils.join(strings, "\\"));

            List<String> strings2 = new ArrayList<>(classNameParts);
            strings2.add(i, "Tests");
            testClasses.add(StringUtils.join(strings2, "\\"));
        }

        return testClasses;
    }
}
