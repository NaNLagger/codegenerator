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

    fun getDiProperties(): List<String> {
        return projectRepository.getCodeSourceRoot(currentActionRepository.currentModuleEntity)?.let {
            fileCreator.getPropertiesOfClass(it, "ru.skblab.skbbank.di", "DI.kt")
        } ?: emptyList()
    }

    private fun String.removeFilePath(isDirectory: Boolean) =
        if (!isDirectory) {
            removeRange(indexOfLast { it == '/' }, length)
        } else {
            this
        }
}