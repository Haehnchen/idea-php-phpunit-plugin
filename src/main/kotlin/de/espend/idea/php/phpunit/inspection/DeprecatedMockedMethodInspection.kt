package de.espend.idea.php.phpunit.inspection

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import de.espend.idea.php.phpunit.utils.PatternUtil
import de.espend.idea.php.phpunit.utils.PhpElementsUtil
import de.espend.idea.php.phpunit.utils.PhpUnitPluginUtil
import org.apache.commons.lang3.StringUtils
import java.util.Objects

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
class DeprecatedMockedMethodInspection : LocalInspectionTool() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PsiElementVisitor() {
            override fun visitElement(psiElement: PsiElement) {
                if (psiElement !is StringLiteralExpression) {
                    super.visitElement(psiElement)
                    return
                }

                if (PatternUtil.getMethodReferenceWithParameterPattern().accepts(psiElement)) {
                    visitCreateMock(psiElement, holder)
                }

                if (PatternUtil.getArrayParameterPattern().accepts(psiElement)) {
                    visitCreatePartialMock(psiElement, holder)
                }

                super.visitElement(psiElement)
            }
        }
    }

    private fun visitCreateMock(psiElement: StringLiteralExpression, holder: ProblemsHolder) {
        val clazz = PhpUnitPluginUtil.findCreateMockParameterOnParameterScope(psiElement) ?: return

        val method = psiElement.contents
        if (!method.isBlank()) {
            registerProblemIfDeprecated(psiElement, holder, clazz, method)
        }
    }

    private fun registerProblemIfDeprecated(
        psiElement: StringLiteralExpression,
        holder: ProblemsHolder,
        clazz: String,
        method: String
    ) {
        val isDeprecated = PhpIndex.getInstance(psiElement.project)
            .getAnyByFQN(clazz).stream()
            .map { phpClass -> phpClass.findMethodByName(method) }
            .filter { methodElement -> Objects.nonNull(methodElement) }
            .anyMatch { methodElement -> methodElement!!.isDeprecated }

        if (isDeprecated) {
            holder.registerProblem(psiElement, "Method '$method' is deprecated ", ProblemHighlightType.LIKE_DEPRECATED)
        }
    }

    private fun visitCreatePartialMock(psiElement: StringLiteralExpression, holder: ProblemsHolder) {
        val parentOfType = PsiTreeUtil.getParentOfType(psiElement, MethodReference::class.java) ?: return

        if (PhpUnitPluginUtil.isCreatePartialMockMethod(parentOfType)) {
            val originalClassName = parentOfType.getParameter("originalClassName", 0) ?: return

            val method = psiElement.contents
            if (method.isBlank()) {
                return
            }

            val clazz = PhpElementsUtil.getStringValue(originalClassName)
            if (StringUtils.isBlank(clazz)) {
                return
            }

            registerProblemIfDeprecated(psiElement, holder, clazz!!, method)
        }
    }
}
