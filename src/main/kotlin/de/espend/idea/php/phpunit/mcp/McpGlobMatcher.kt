package de.espend.idea.php.phpunit.mcp

import com.intellij.openapi.util.io.FileUtil
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

object McpGlobMatcher {
    fun matches(path: String, glob: String): Boolean {
        val normalizedPath = path.replace('\\', '/')
        val normalizedGlob = glob.trim().replace('\\', '/')

        return try {
            Pattern.compile("^" + FileUtil.convertAntToRegexp(normalizedGlob, false) + "$")
                .matcher(normalizedPath)
                .matches()
        } catch (_: PatternSyntaxException) {
            false
        }
    }
}
