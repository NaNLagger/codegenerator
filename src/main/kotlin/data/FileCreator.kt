package data

import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import domain.entities.DirectoryEntity
import domain.entities.FileEntity
import domain.entities.SourceRootEntity
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import javax.inject.Inject

class FileCreator @Inject constructor(
    private val project: Project
) {

    fun addFile(file: FileEntity, directory: DirectoryEntity) {
        val language = when (file.fileType) {
            FileEntity.FileType.KOTLIN -> KotlinLanguage.INSTANCE
            FileEntity.FileType.LAYOUT_XML -> XMLLanguage.INSTANCE
        }
        val psiFile = PsiFileFactory.getInstance(project)
            .createFileFromText("${file.name}.${file.fileType.extension}", language, file.content)
        directory.psiDirectory.add(psiFile)
        psiFile.getChildOfType<KtClassOrObject>()
    }

    fun getDirectory(sourceRootEntity: SourceRootEntity): DirectoryEntity {
        return DirectoryEntity(PsiManager.getInstance(project).findDirectory(sourceRootEntity.virtualFile)!!)
    }

    fun getPropertiesOfClass(sourceRootEntity: SourceRootEntity, packageName: String, fileName: String): List<String> {
        val psiFile = getDirectoryByPackage(sourceRootEntity, packageName).psiDirectory.findFile(fileName)!!
        val ktObject = psiFile.getChildOfType<KtClassOrObject>()!!
        return ktObject.declarations.filterIsInstance<KtProperty>().map { it.name.orEmpty() }
    }

    fun findOrCreateSubdirectory(directory: DirectoryEntity, name: String): DirectoryEntity {
        val psiDirectory =
            directory.psiDirectory.findSubdirectory(name) ?: directory.psiDirectory.createSubdirectory(name)
        return DirectoryEntity(psiDirectory)
    }

    fun getDirectoryByPackage(sourceRootEntity: SourceRootEntity, packageName: String): DirectoryEntity {
        val psiDirectory = PsiManager.getInstance(project).findDirectory(sourceRootEntity.virtualFile)!!
        var subdirectory = psiDirectory
        packageName.split(".").forEach {
            subdirectory = subdirectory.findSubdirectory(it) ?: subdirectory.createSubdirectory(it)
        }
        return DirectoryEntity(subdirectory)
    }

    companion object {
        const val LAYOUT_DIRECTORY = "layout"
    }
}