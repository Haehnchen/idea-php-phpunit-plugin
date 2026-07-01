package de.espend.idea.php.phpunit.type

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.AssignmentExpression
import com.jetbrains.php.lang.psi.elements.FieldReference
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4
import de.espend.idea.php.phpunit.utils.PhpUnitPluginUtil
import org.apache.commons.lang3.StringUtils

/**
 * Pipe all fields in setUp to make type them visible in PhpClass scope
 *
 * function setUp() { $this->foobar = .. }
 *
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
class SetUpTypeProvider : PhpTypeProvider4 {
    override fun getKey(): Char {
        return '\u1589'
    }

    override fun getType(element: PsiElement): PhpType? {
        if (element is FieldReference) {
            val name = element.name
            if (name != null) {
                // find method scope, we not directly search for class as Method is our parent scope
                val methodScope = PsiTreeUtil.getStubOrPsiParentOfType(element, Method::class.java)
                if (methodScope != null) {
                    val phpClass = methodScope.containingClass
                    if (phpClass != null && PhpUnitPluginUtil.isTestClassWithoutIndexAccess(phpClass)) {
                        val method = phpClass.findOwnMethodByName("setUp")

                        // "setUp" is our "constructor" for test classes
                        if (method != null) {
                            for (assignmentExpression in PsiTreeUtil.collectElementsOfType(
                                method,
                                AssignmentExpression::class.java,
                            )) {
                                val variable = assignmentExpression.variable

                                // remember or field name and attach is for a later resolve
                                if (variable is FieldReference && name == variable.name) {
                                    return PhpType().add(
                                        "#" + key + StringUtils.stripStart(phpClass.getFQN(), "\\") +
                                            TRIM_KEY + method.name + TRIM_KEY + name,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        return null
    }

    override fun complete(s: String, project: Project): PhpType? {
        return null
    }

    override fun getBySignature(
        expression: String,
        visited: Set<String>,
        depth: Int,
        project: Project,
    ): Collection<PhpNamedElement>? {
        // split: CLASS|setUp|FIELD_NAME
        val split = expression.split(TRIM_KEY.toString().toRegex())
        if (split.size != 3) {
            return null
        }

        val phpNamedElements: MutableCollection<PhpNamedElement> = ArrayList()

        val phpIndex = PhpIndex.getInstance(project)
        for (phpClass in phpIndex.getAnyByFQN(split[0])) {
            val setUp = phpClass.findOwnMethodByName("setUp")
            if (setUp == null) {
                continue
            }

            // find field assignments:
            // $this->foo = $this->createMock();
            for (assignmentExpression in PsiTreeUtil.collectElementsOfType(setUp, AssignmentExpression::class.java)) {
                val variable = assignmentExpression.variable

                if (variable !is FieldReference || split[2] != variable.name) {
                    continue
                }

                // completeType needed for incomplete resolve elements:
                // getBySignature needs valid signatures
                val types = phpIndex.completeType(project, assignmentExpression.type, visited).types
                for (s in types) {
                    if (PhpType.isUnresolved(s)) {
                        phpNamedElements.addAll(phpIndex.getBySignature(s, visited, depth))
                    } else {
                        // \Class\Name
                        phpNamedElements.addAll(phpIndex.getAnyByFQN(s))
                    }
                }
            }
        }

        return phpNamedElements
    }

    companion object {
        private const val TRIM_KEY: Char = '\u1212'
    }
}
