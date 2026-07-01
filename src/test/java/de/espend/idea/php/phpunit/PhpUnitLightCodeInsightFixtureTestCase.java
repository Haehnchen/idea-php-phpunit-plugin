package de.espend.idea.php.phpunit;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.codeInsight.daemon.LineMarkerProviders;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.IntentionManager;
import com.intellij.codeInspection.*;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpReference;
import com.jetbrains.php.lang.psi.elements.PhpTypedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public abstract class PhpUnitLightCodeInsightFixtureTestCase extends LightJavaCodeInsightFixtureTestCase {

    public void assertCompletionContains(LanguageFileType languageFileType, String configureByText, String... lookupStrings) {

        myFixture.configureByText(languageFileType, configureByText);
        myFixture.completeBasic();

        completionContainsAssert(lookupStrings);
    }

    private void completionContainsAssert(String[] lookupStrings) {
        if(lookupStrings.length == 0) {
            fail("No lookup element given");
        }

        List<String> lookupElements = myFixture.getLookupElementStrings();
        if(lookupElements == null || lookupElements.size() == 0) {
            fail(String.format("failed that empty completion contains %s", Arrays.toString(lookupStrings)));
        }

        for (String s : Arrays.asList(lookupStrings)) {
            if(!lookupElements.contains(s)) {
                fail(String.format("failed that completion contains %s in %s", s, lookupElements.toString()));
            }
        }
    }

    public void assertLineMarker(@NotNull PsiElement psiElement, @NotNull LineMarker.Assert assertMatch) {

        final List<PsiElement> elements = collectPsiElementsRecursive(psiElement);

        for (LineMarkerProvider lineMarkerProvider : LineMarkerProviders.getInstance().allForLanguage(psiElement.getLanguage())) {
            Collection<LineMarkerInfo<?>> lineMarkerInfos = new ArrayList<>();
            lineMarkerProvider.collectSlowLineMarkers(elements, lineMarkerInfos);

            if(lineMarkerInfos.size() == 0) {
                continue;
            }

            for (LineMarkerInfo<?> lineMarkerInfo : lineMarkerInfos) {
                if(assertMatch.match(lineMarkerInfo)) {
                    return;
                }
            }
        }

        fail(String.format("Fail that '%s' matches on of '%s' PsiElements", assertMatch.getClass(), elements.size()));
    }

    public void assertIntentionIsAvailable(LanguageFileType languageFileType, String configureByText, String intentionText) {
        myFixture.configureByText(languageFileType, configureByText);
        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        Set<String> items = new HashSet<>();

        for (IntentionAction intentionAction : IntentionManager.getInstance().getIntentionActions()) {
            if(!intentionAction.isAvailable(getProject(), getEditor(), psiElement.getContainingFile())) {
                continue;
            }

            String text = intentionAction.getText();
            items.add(text);

            if(!text.equals(intentionText)) {
                continue;
            }

            return;
        }

        fail(String.format("Fail intention action '%s' is available in element '%s' with '%s'", intentionText, psiElement.getText(), items));
    }

    public void assertPhpReferenceResolveTo(LanguageFileType languageFileType, String configureByText, ElementPattern<?> pattern) {
        myFixture.configureByText(languageFileType, configureByText);
        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        psiElement = PsiTreeUtil.getParentOfType(psiElement, PhpReference.class);
        if (psiElement == null) {
            fail("Element is not PhpReference.");
        }

        PsiElement resolve = ((PhpReference) psiElement).resolve();
        if(!pattern.accepts(resolve)) {
            fail(String.format("failed pattern matches element of '%s'", resolve == null ? "null" : resolve.toString()));
        }

        assertTrue(pattern.accepts(resolve));
    }

    public void assertPhpReferenceNotResolveTo(LanguageFileType languageFileType, String configureByText, ElementPattern<?> pattern) {
        myFixture.configureByText(languageFileType, configureByText);
        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        psiElement = PsiTreeUtil.getParentOfType(psiElement, PhpReference.class);
        if (psiElement == null) {
            fail("Element is not PhpReference.");
        }

        assertFalse(pattern.accepts(((PhpReference) psiElement).resolve()));
    }

    public void assertReferencesMatch(LanguageFileType languageFileType, String configureByText, ElementPattern<?> pattern) {
        myFixture.configureByText(languageFileType, configureByText);
        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        // get parent for references; mostly we are inside a token element
        PsiElement parent = psiElement.getParent();

        for (PsiReference psiReference : parent.getReferences()) {
            // multi resolve
            if(psiReference instanceof PsiPolyVariantReference) {
                for (ResolveResult resolveResult : ((PsiPolyVariantReference) psiReference).multiResolve(true)) {
                    PsiElement element = resolveResult.getElement();
                    if(pattern.accepts(element)) {
                        return;
                    }
                }
            }

            // single result
            PsiElement resolve = psiReference.resolve();
            if(resolve == null) {
                continue;
            }

            if(pattern.accepts(resolve)) {
                return;
            }
        }

        fail(String.format("Failed pattern matches element of '%d' elements", parent.getReferences().length));
    }

    public void assertLocalInspectionContains(String filename, String content, String contains) {
        Set<String> matches = new HashSet<>();

        Pair<List<ProblemDescriptor>, Integer> localInspectionsAtCaret = getLocalInspectionsAtCaret(filename, content);
        for (ProblemDescriptor result : localInspectionsAtCaret.getFirst()) {
            TextRange textRange = result.getPsiElement().getTextRange();
            if (textRange.contains(localInspectionsAtCaret.getSecond()) && result.toString().equals(contains)) {
                return;
            }

            matches.add(result.toString());
        }

        fail(String.format("Fail matches '%s' with one of %s", contains, matches));
    }

    private Pair<List<ProblemDescriptor>, Integer> getLocalInspectionsAtCaret(@NotNull String filename, @NotNull String content) {

        PsiElement psiFile = myFixture.configureByText(filename, content);

        int caretOffset = myFixture.getCaretOffset();
        if(caretOffset <= 0) {
            fail("Please provide <caret> tag");
        }

        ProblemsHolder problemsHolder = new ProblemsHolder(InspectionManager.getInstance(getProject()), psiFile.getContainingFile(), false);

        for (LocalInspectionEP localInspectionEP : LocalInspectionEP.LOCAL_INSPECTION.getExtensions()) {
            Object object = localInspectionEP.getInstance();
            if(!(object instanceof LocalInspectionTool)) {
                continue;
            }

            final PsiElementVisitor psiElementVisitor = ((LocalInspectionTool) object).buildVisitor(problemsHolder, false);

            psiFile.acceptChildren(new PsiRecursiveElementVisitor() {
                @Override
                public void visitElement(@NotNull PsiElement element) {
                    psiElementVisitor.visitElement(element);
                    super.visitElement(element);
                }
            });

            psiElementVisitor.visitFile(psiFile.getContainingFile());
        }

        return Pair.create(problemsHolder.getResults(), caretOffset);
    }

    public void assertMethodContainsTypes(@NotNull LanguageFileType languageFileType, @NotNull String configureByText, @NotNull String... types) {
        myFixture.configureByText(languageFileType, configureByText);
        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        psiElement = PsiTreeUtil.getParentOfType(psiElement, PhpTypedElement.class);
        if (psiElement == null) {
            fail("Element is not a PhpTypedElement.");
        }

        PhpType phpType = PhpIndex.getInstance(psiElement.getProject()).completeType(
            psiElement.getProject(),
            ((PhpTypedElement) psiElement).getType(),
            new HashSet<>()
        );

        assertContainsElements(phpType.getTypes(), types);
    }

    @NotNull
    private List<PsiElement> collectPsiElementsRecursive(@NotNull PsiElement psiElement) {
        final List<PsiElement> elements = new ArrayList<>();
        elements.add(psiElement.getContainingFile());

        psiElement.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                elements.add(element);
                super.visitElement(element);
            }
        });
        return elements;
    }

    @SuppressWarnings("unused")
    public static class LineMarker {
        public interface Assert {
            boolean match(@NotNull LineMarkerInfo<?> markerInfo);
        }

        public static class ToolTipEqualsAssert implements Assert {
            @NotNull
            private final String toolTip;

            public ToolTipEqualsAssert(@NotNull String toolTip) {
                this.toolTip = toolTip;
            }

            @Override
            public boolean match(@NotNull LineMarkerInfo<?> markerInfo) {
                return markerInfo.getLineMarkerTooltip() != null && markerInfo.getLineMarkerTooltip().equals(toolTip);
            }
        }
    }
}
