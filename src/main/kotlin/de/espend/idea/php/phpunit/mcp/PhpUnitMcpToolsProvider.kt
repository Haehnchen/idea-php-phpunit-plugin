package de.espend.idea.php.phpunit.mcp

import com.intellij.mcpserver.McpTool
import com.intellij.mcpserver.McpToolsProvider
import com.intellij.mcpserver.impl.util.asTools
import de.espend.idea.php.phpunit.mcp.toolset.PhpUnitToolset

class PhpUnitMcpToolsProvider : McpToolsProvider {
    override fun getTools(): List<McpTool> {
        return try {
            PhpUnitToolset().asTools()
        } catch (_: Exception) {
            emptyList()
        }
    }
}
