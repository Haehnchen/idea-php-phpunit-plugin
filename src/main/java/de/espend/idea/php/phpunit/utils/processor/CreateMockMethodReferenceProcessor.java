package de.espend.idea.php.phpunit.utils.processor;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import de.espend.idea.php.phpunit.utils.ChainVisitorUtil;
import de.espend.idea.php.phpunit.utils.PhpElementsUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Try to find a "createMock" method inside a call chain
 *
 *  - $this->createMock(...)->expects()->method('foobar')
 *  - $this->foo->expects()->method('foobar') // via eg "setup" method
 *
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class CreateMockMethodReferenceProcessor implements ChainVisitorUtil.ChainProcessorInterface {
    @Nullable
    private String parameter;

    @Override
    public boolean process(@NotNull MethodReference methodReference) {
        boolean isMockShortcutMethod = PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "\\PHPUnit\\Framework\\TestCase", "createMock")
            || PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "PHPUnit_Framework_TestCase", "createMock")
            || PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "\\PHPUnit\\Framework\\TestCase", "createPartialMock")
            || PhpElementsUtil.isMethodReferenceInstanceOf(methodReference, "PHPUnit_Framework_TestCase", "createPartialMock");

        if (isMockShortcutMethod) {
            PsiElement[] parameters = methodReference.getParameters();

            if(parameters.length > 0) {
                this.parameter = PhpElementsUtil.getStringValue(parameters[0]);
            }

            return false;
        }

        // allowed chain of classes types
        return PhpElementsUtil.isMethodReferenceInstanceOf(
            methodReference,
            "PHPUnit_Framework_MockObject_MockObject",
            "PHPUnit_Framework_MockObject_Builder_InvocationMocker",
            "PHPUnit\\Framework\\MockObject\\MockObject",
            "PHPUnit\\Framework\\MockObject\\Builder\\InvocationMocker"
        );
    }

    @Nullable
    public String getParameter() {
        return parameter;
    }

    @Nullable
    public static String createParameter(@NotNull MethodReference methodReference) {
        CreateMockMethodReferenceProcessor processor = new CreateMockMethodReferenceProcessor();
        ChainVisitorUtil.visit(methodReference, processor);
        return processor.getParameter();
    }
}
