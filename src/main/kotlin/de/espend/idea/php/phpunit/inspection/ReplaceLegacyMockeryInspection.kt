package de.espend.idea.php.phpunit.inspection

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.codeInspection.util.IntentionName
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import de.espend.idea.php.phpunit.utils.MockeryPsiRefactoringUtil
import de.espend.idea.php.phpunit.utils.PhpElementsUtil
import org.apache.commons.lang3.tuple.Triple
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.ButtonGroup
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JRadioButton

class ReplaceLegacyMockeryInspection : PhpInspection(), ActionListener {
    override fun buildVisitor(problemsHolder: ProblemsHolder, b: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpMethodReference(reference: MethodReference) {
                if (PhpElementsUtil.isMethodReferenceInstanceOf(reference, "Mockery_LegacyMockInterface", "shouldReceive") ||
                    PhpElementsUtil.isMethodReferenceInstanceOf(reference, "Mockery\\LegacyMockInterface", "shouldReceive") ||
                    PhpElementsUtil.isMethodReferenceInstanceOf(reference, "Mockery_LegacyMockInterface", "shouldNotReceive") ||
                    PhpElementsUtil.isMethodReferenceInstanceOf(reference, "Mockery\\LegacyMockInterface", "shouldNotReceive")
                ) {
                    problemsHolder.registerProblem(
                        reference,
                        "Replace legacy Mockery syntax",
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                        replaceLegacyMockery()
                    )
                }
            }
        }
    }

    override fun createOptionsPanel(): JComponent {
        val instance = PropertiesComponent.getInstance()
        val preferMultipleStatements = instance.getBoolean("preferMultipleStatements", false)
        val preferFunctionNotation = instance.getBoolean("preferFunctionNotation", false)

        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.add(Box.createRigidArea(Dimension(0, 10)))

        panel.add(JLabel("Preferred form for allows/expects statements:"))
        createRadioGroup(
            panel,
            Triple.of("use array notation", "preferArray", !preferMultipleStatements),
            Triple.of("use multiple allows/expects statements", "preferMultipleStatements", preferMultipleStatements)
        )
        panel.add(Box.createRigidArea(Dimension(0, 10)))

        panel.add(JLabel("Preferred way to deal with multiple method parameters:"))
        createRadioGroup(
            panel,
            Triple.of("allows('methodName')", "preferStringNotation", !preferFunctionNotation),
            Triple.of("allows()->methodName", "preferFunctionNotation", preferFunctionNotation)
        )
        panel.add(JLabel("Note that, if allows()->method is preferred, then multiple statements will be used automatically"))
        return panel
    }

    fun createRadioGroup(panel: JPanel, vararg triples: Triple<String, String, Boolean>) {
        val buttonGroup = ButtonGroup()

        for (triple in triples) {
            val radioButton = JRadioButton(triple.left)
            radioButton.addActionListener(this)
            radioButton.actionCommand = triple.middle
            buttonGroup.add(radioButton)
            radioButton.isSelected = triple.right
            panel.add(radioButton)
        }
    }

    override fun actionPerformed(e: ActionEvent) {
        val actionCommand: String = e.actionCommand
        val instance = PropertiesComponent.getInstance()
        when (actionCommand) {
            "preferArray" -> instance.setValue("preferMultipleStatements", false)
            "preferMultipleStatements" -> instance.setValue("preferMultipleStatements", true)
            "preferStringNotation" -> instance.setValue("preferFunctionNotation", false)
            "preferFunctionNotation" -> instance.setValue("preferFunctionNotation", true)
        }
    }

    class replaceLegacyMockery : LocalQuickFix {
        override fun getName(): @IntentionName String {
            return QUICK_FIX_NAME
        }

        override fun getFamilyName(): @IntentionFamilyName String {
            return name
        }

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            val methodReference = descriptor.psiElement as MethodReference
            val instance = PropertiesComponent.getInstance()
            val preferFunctionNotation = instance.getBoolean("preferFunctionNotation", false)
            val preferMultipleStatements = instance.getBoolean("preferMultipleStatements", false)

            if (PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "Mockery_LegacyMockInterface", "shouldReceive") ||
                PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "Mockery\\LegacyMockInterface", "shouldReceive")
            ) {
                if (MockeryPsiRefactoringUtil.checkForOnceInMethodSequence(methodReference) ||
                    MockeryPsiRefactoringUtil.checkForCountInMethodSequence(methodReference)
                ) {
                    MockeryPsiRefactoringUtil.replaceShouldReceiveFromMethodReference(
                        project,
                        methodReference,
                        "expects",
                        preferFunctionNotation,
                        preferMultipleStatements
                    )
                } else {
                    MockeryPsiRefactoringUtil.replaceShouldReceiveFromMethodReference(
                        project,
                        methodReference,
                        "allows",
                        preferFunctionNotation,
                        preferMultipleStatements
                    )
                }
            } else {
                MockeryPsiRefactoringUtil.replaceShouldNotReceive(
                    project,
                    methodReference,
                    "allows",
                    preferFunctionNotation,
                    preferMultipleStatements
                )
            }
        }
    }

    companion object {
        const val QUICK_FIX_NAME: String = "Replace legacy Mockery syntax"
    }
}
