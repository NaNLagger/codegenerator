package domain.interactor

import data.CurrentActionRepository
import data.FileCreator
import data.FileCreator.Companion.LAYOUT_DIRECTORY
import data.ProjectRepository
import domain.entities.FileEntity
import javax.inject.Inject

class ActionInteractor @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val currentActionRepository: CurrentActionRepository,
    private val fileCreator: FileCreator
) {

    fun getCurrentPackage(): String {
        val currentPath = currentActionRepository.currentPath
        val currentModuleEntity = currentActionRepository.currentModuleEntity
        val sourceRootPath = projectRepository.getCodeSourceRoot(currentModuleEntity)?.path ?: ""
        return if (currentPath != sourceRootPath && currentPath.contains(sourceRootPath)) {
            currentPath.removePrefix("$sourceRootPath/")
                .removeFilePath(currentActionRepository.isDirectory)
                .replace("/", ".")
        } else {
            ""
        }
    }

    fun addFile(path: String, file: FileEntity) {
        val moduleDirectory = projectRepository.getModuleDirectory(currentActionRepository.currentModuleEntity)
        val modulePath = moduleDirectory.psiDirectory.virtualFile.path
        val resultDirectory = path
            .removePrefix(modulePath)
            .split('/')
            .filter { it.isNotBlank() }
            .dropLast(1)
            .fold(moduleDirectory) { directory, name ->
                fileCreator.findOrCreateSubdirectory(directory, name)
            }

        fileCreator.addFile(file, resultDirectory)
    }

    fun getFullPathByPackage(fileName: String, fileType: FileEntity.FileType, packageName: String): String? {
        return projectRepository.getCodeSourceRoot(currentActionRepository.currentModuleEntity)?.let {
            it.path + "/" + packageName.replace('.', '/') + "/" + fullFileName(fileName, fileType)
        }
    }

    fun getFullPathByRes(
        fileName: String,
        fileType: FileEntity.FileType,
        resFolder: String = LAYOUT_DIRECTORY
    ): String? {
        return projectRepository.getResourceSourceRoot(currentActionRepository.currentModuleEntity)?.let {
            it.path + "/" + resFolder + "/" + fullFileName(fileName, fileType)
        }
    }

    fun getModuleMainPath(): String {
        return projectRepository.getResourceSourceRoot(currentActionRepository.currentModuleEntity)?.let {
            val sourceIndex = it.path.lastIndexOf("main")
            it.path.removeRange(sourceIndex, it.path.length)
        } ?: ""
    }

    fun getDiProperties(): List<String> {
        return projectRepository.findFields()
    }

    fun getManifestPackage(): String {
        return projectRepository.getManifestPackage(currentActionRepository.currentModuleEntity)
    }

    fun getFieldPackage(name: String): String {
        return projectRepository.getFieldPackage(name).replace(".$name", "")
    }

    private fun fullFileName(fileName: String, fileType: FileEntity.FileType) =
        "${fileName}.${fileType.extension}"

    private fun String.removeFilePath(isDirectory: Boolean) =
        if (!isDirectory) {
            removeRange(indexOfLast { it == '/' }, length)
        } else {
            this
        }
}