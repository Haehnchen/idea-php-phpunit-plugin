package de.espend.idea.php.phpunit.inspection;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.inspections.PhpInspection;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor;
import de.espend.idea.php.phpunit.utils.MockeryPsiRefactoringUtil;
import de.espend.idea.php.phpunit.utils.PhpElementsUtil;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReplaceLegacyMockeryInspection extends PhpInspection implements ActionListener {

    public static final String QUICK_FIX_NAME = "Replace legacy Mockery syntax";

    @Override
    public @NotNull
    PsiElementVisitor buildVisitor(@NotNull ProblemsHolder problemsHolder, boolean b) {
        return new PhpElementVisitor() {
            @Override
            public void visitPhpMethodReference(MethodReference reference) {
                if (PhpElementsUtil.isMethodReferenceInstanceOf(reference, "Mockery_LegacyMockInterface", "shouldReceive") ||
                        PhpElementsUtil.isMethodReferenceInstanceOf(reference, "Mockery\\LegacyMockInterface", "shouldReceive") || PhpElementsUtil.isMethodReferenceInstanceOf(reference, "Mockery_LegacyMockInterface", "shouldNotReceive") ||
                        PhpElementsUtil.isMethodReferenceInstanceOf(reference, "Mockery\\LegacyMockInterface", "shouldNotReceive")) {
                    problemsHolder.registerProblem(reference, "Replace legacy Mockery syntax", ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new replaceLegacyMockery());

                }
            }
        };
    }

    @Override
    public JComponent createOptionsPanel() {
        PropertiesComponent instance = PropertiesComponent.getInstance();
        boolean preferMultipleStatements = instance.getBoolean("preferMultipleStatements",false);
        boolean preferFunctionNotation = instance.getBoolean("preferFunctionNotation",false);


        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(new JLabel("Preferred form for allows/expects statements:"));
        createRadioGroup(panel, Triple.of("use array notation", "preferArray", !preferMultipleStatements), Triple.of("use multiple allows/expects statements", "preferMultipleStatements", preferMultipleStatements));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(new JLabel("Preferred way to deal with multiple method parameters:"));
        createRadioGroup(panel, Triple.of("allows('methodName')", "preferStringNotation", !preferFunctionNotation), Triple.of("allows()->methodName", "preferFunctionNotation", preferFunctionNotation));
        panel.add(new JLabel("Note that, if allows()->method is preferred, then multiple statements will be used automatically"));
        return panel;
    }

    @SafeVarargs
    public final void createRadioGroup(JPanel panel, Triple<String, String, Boolean>... triples) {
        ButtonGroup buttonGroup = new ButtonGroup();

        for (Triple<String, String, Boolean> triple : triples) {
            JRadioButton radioButton = new JRadioButton(triple.getLeft());
            radioButton.addActionListener(this);
            radioButton.setActionCommand(triple.getMiddle());
            buttonGroup.add(radioButton);
            radioButton.setSelected(triple.getRight());
            panel.add(radioButton);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        PropertiesComponent instance = PropertiesComponent.getInstance();
        switch (actionCommand) {
            case "preferArray":
                instance.setValue("preferMultipleStatements",false);
                break;
            case "preferMultipleStatements":
                instance.setValue("preferMultipleStatements",true);
                break;
            case "preferStringNotation":
                instance.setValue("preferFunctionNotation",false);
                break;
            case "preferFunctionNotation":
                instance.setValue("preferFunctionNotation",true);
                break;
        }
    }

    public static final class replaceLegacyMockery implements LocalQuickFix {

        @Override
        public @IntentionName
        @NotNull
        String getName() {
            return QUICK_FIX_NAME;
        }

        @Override
        public @IntentionFamilyName
        @NotNull
        String getFamilyName() {
            return getName();
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            final MethodReference methodReference = (MethodReference) descriptor.getPsiElement();
            PropertiesComponent instance = PropertiesComponent.getInstance();
            boolean preferFunctionNotation = instance.getBoolean("preferFunctionNotation",false);
            boolean preferMultipleStatements = instance.getBoolean("preferMultipleStatements",false);
            
            if (PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "Mockery_LegacyMockInterface", "shouldReceive") ||
                    PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "Mockery\\LegacyMockInterface", "shouldReceive")) {
                if (MockeryPsiRefactoringUtil.checkForOnceInMethodSequence(methodReference) || MockeryPsiRefactoringUtil.checkForCountInMethodSequence(methodReference)) {
                    MockeryPsiRefactoringUtil.replaceShouldReceiveFromMethodReference(project, methodReference, "expects", preferFunctionNotation, preferMultipleStatements);
                } else {
                    MockeryPsiRefactoringUtil.replaceShouldReceiveFromMethodReference(project, methodReference, "allows", preferFunctionNotation, preferMultipleStatements);
                }
            } else {
                MockeryPsiRefactoringUtil.replaceShouldNotReceive(project, methodReference, "allows", preferFunctionNotation, preferMultipleStatements);
            }
        }
    }
}
