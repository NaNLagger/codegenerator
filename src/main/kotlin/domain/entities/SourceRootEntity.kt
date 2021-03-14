package domain.entities

import com.intellij.openapi.vfs.VirtualFile

class SourceRootEntity(
    val virtualFile: VirtualFile
) {

    val path = virtualFile.path
}