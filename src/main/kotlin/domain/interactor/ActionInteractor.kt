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

    fun addFileToPackage(file: FileEntity, packageName: String) {
        projectRepository.getCodeSourceRoot(currentActionRepository.currentModuleEntity)?.let {
            val directory = fileCreator.getDirectoryByPackage(it, packageName)
            fileCreator.addFile(file, directory)
        }
    }

    fun addFileToResources(file: FileEntity, resFolder: String = LAYOUT_DIRECTORY) {
        projectRepository.getResourceSourceRoot(currentActionRepository.currentModuleEntity)?.let {
            val directory = fileCreator.getDirectory(it)
            val layoutDirectory = fileCreator.findOrCreateSubdirectory(directory, resFolder)
            fileCreator.addFile(file, layoutDirectory)
        }
    }

    fun getFullPathByPackage(fileName: String, fileType: FileEntity.FileType, packageName: String): String? {
        return projectRepository.getCodeSourceRoot(currentActionRepository.currentModuleEntity)?.let {
            val directory = fileCreator.getDirectoryByPackage(it, packageName)
            directory.psiDirectory.virtualFile.path + "/" + fullFileName(fileName, fileType)
        }
    }

    fun getFullPathByRes(fileName: String, fileType: FileEntity.FileType, resFolder: String = LAYOUT_DIRECTORY): String? {
        return projectRepository.getResourceSourceRoot(currentActionRepository.currentModuleEntity)?.let {
            val directory = fileCreator.getDirectory(it)
            val layoutDirectory = fileCreator.findOrCreateSubdirectory(directory, resFolder)
            layoutDirectory.psiDirectory.virtualFile.path + "/" + fullFileName(fileName, fileType)
        }
    }

    fun getModulePath(): String {
        return projectRepository.getResourceSourceRoot(currentActionRepository.currentModuleEntity)?.let {
            val sourceIndex = it.path.lastIndexOf("main")
            it.path.removeRange(sourceIndex, it.path.length)
        } ?: ""
    }

    fun getDiProperties(): List<String> {
        return projectRepository.findFields()
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