@file:Suppress("FunctionName", "unused")

package de.espend.idea.php.phpunit.mcp.toolset

import com.intellij.mcpserver.McpToolset
import com.intellij.mcpserver.annotations.McpDescription
import com.intellij.mcpserver.annotations.McpTool
import com.intellij.mcpserver.mcpFail
import com.intellij.mcpserver.project
import com.intellij.openapi.application.readAction
import de.espend.idea.php.phpunit.mcp.collector.PhpUnitTestCaseUsageCollector
import kotlinx.coroutines.currentCoroutineContext

class PhpUnitToolset : McpToolset {
    @McpTool
    @McpDescription("""
        Finds possible PHPUnit test cases for PHP classes or class methods.

        Use target for comma-separated class or class:method inputs. Class names may be short or fully qualified.
        Use fileGlob to collect classes and methods from matching PHP source files first, then search tests for those usages.

        Returns a compact comma-separated list of runnable test identifiers, for example:
        \Tests\Service\FooTest::testCreate,\Tests\Service\FooTest
    """)
    suspend fun find_phpunit_test_cases(
        @McpDescription("""Optional comma-separated class or class:method targets. Examples: 'Foo:test', '\App\Service\Foo:create', 'App\Service\Foo'. Regex class or method values can be wrapped in /.../.""")
        target: String? = null,
        @McpDescription("""Optional Ant-style glob for PHP source files relative to the project root. Example: 'src/Service/Foo.php' or 'src/Service/**/*.php'.""")
        fileGlob: String? = null,
    ): String {
        val normalizedTarget = target?.trim()?.takeIf { it.isNotBlank() }
        val normalizedFileGlob = fileGlob?.trim()?.takeIf { it.isNotBlank() }

        if (normalizedTarget == null && normalizedFileGlob == null) {
            mcpFail("At least one of 'target' or 'fileGlob' must be provided.")
        }

        val project = currentCoroutineContext().project

        return readAction {
            PhpUnitTestCaseUsageCollector(project).collect(normalizedTarget, normalizedFileGlob)
        }
    }
}
