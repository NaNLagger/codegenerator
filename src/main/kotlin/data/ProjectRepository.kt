package data

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.psi.PsiManager
import com.intellij.psi.search.PsiShortNamesCache
import domain.entities.DirectoryEntity
import domain.entities.ModuleEntity
import domain.entities.SourceRootEntity
import org.jetbrains.kotlin.idea.search.projectScope
import org.jetbrains.kotlin.idea.util.sourceRoots
import javax.inject.Inject
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName

class ProjectRepository @Inject constructor(
    private val project: Project
) {

    fun getProjectName() = project.name

    fun getProjectPath() = project.basePath ?: ""

    fun getCodeSourceRoot(module: ModuleEntity): SourceRootEntity? {
        return getSourceRoots(module).find {
            val pathTrimmed = it.path.removePrefix(getModulePath(module))
            pathTrimmed.contains("src", true)
                    && pathTrimmed.contains("main")
                    && !pathTrimmed.contains("assets", true)
                    && !pathTrimmed.contains("res", true)
        }
    }

    fun getResourceSourceRoot(module: ModuleEntity): SourceRootEntity? {
        return getSourceRoots(module).find {
            val pathTrimmed = it.path.removePrefix(getModulePath(module))
            pathTrimmed.contains("src", true)
                    && pathTrimmed.contains("main", true)
                    && pathTrimmed.contains("res", true)
        }
    }

    fun getModuleDirectory(module: ModuleEntity): DirectoryEntity {
        return project.guessProjectDir()?.let {
            val projectDirectory = PsiManager.getInstance(project).findDirectory(it)!!
            module.nameWithoutPrefix.split('.').fold(projectDirectory) { directory, name ->
                directory.findSubdirectory(name)
                    ?: throw IllegalStateException("$name directory doesn't exist in ${directory.virtualFile.path}!")
            }.let { psiDirectory -> DirectoryEntity(psiDirectory) }
        } ?: throw IllegalStateException("cannot find directory of ${getProjectName()} project")
    }

    fun findFields(): List<String> {
        return PsiShortNamesCache.getInstance(project)
            .allFieldNames
            .toList()
    }

    fun getManifestPackage(module: ModuleEntity): String {
        val mainDirectory = getModuleDirectory(module).psiDirectory.findSubdirectory("src")?.findSubdirectory("main")
        return mainDirectory?.findFile("AndroidManifest.xml")?.let { manifestFile ->
            Regex("<manifest[^>]*package=\"([^\"]+)\"").find(manifestFile.text)?.groupValues?.last()
        } ?: ""
    }

    fun getFieldPackage(name: String): String {
        val field = PsiShortNamesCache.getInstance(project).getFieldsByName(name, project.projectScope()).firstOrNull()
        return field?.getKotlinFqName()?.asString() ?: ""
    }

    private fun getSourceRoots(module: ModuleEntity): List<SourceRootEntity> {
        return ModuleManager.getInstance(project).findModuleByName(module.name)?.sourceRoots?.map {
            SourceRootEntity(it)
        } ?: throw IllegalStateException("${module.name} module doesn't exist!")
    }

    private fun getModulePath(module: ModuleEntity): String {
        return getModuleDirectory(module).psiDirectory.virtualFile.path
    }
}