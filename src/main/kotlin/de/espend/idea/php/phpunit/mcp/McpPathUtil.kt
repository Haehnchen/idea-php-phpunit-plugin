package de.espend.idea.php.phpunit.mcp

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile

object McpPathUtil {
    fun getRelativeProjectPath(project: Project, virtualFile: VirtualFile): String {
        for (contentRoot in ProjectRootManager.getInstance(project).contentRoots) {
            val relativePath = VfsUtil.getRelativePath(virtualFile, contentRoot, '/')
            if (relativePath != null) {
                return relativePath
            }
        }

        return virtualFile.path
    }
}
