package data

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import domain.entities.ModuleEntity
import domain.entities.SourceRootEntity
import org.jetbrains.kotlin.idea.util.sourceRoots
import javax.inject.Inject

class ProjectRepository @Inject constructor(
    private val project: Project
) {

    fun getProjectName() = project.name

    fun getProjectPath() = project.basePath ?: ""

    fun getCodeSourceRoot(module: ModuleEntity): SourceRootEntity? {
        return getSourceRoots(module).find {
            val pathTrimmed = it.path.removeModulePathPrefix(module)
            pathTrimmed.contains("src", true)
                    && pathTrimmed.contains("main")
                    && !pathTrimmed.contains("assets", true)
                    && !pathTrimmed.contains("res", true)
        }
    }

    fun getResourceSourceRoot(module: ModuleEntity): SourceRootEntity? {
        return getSourceRoots(module).find {
            val pathTrimmed = it.path.removeModulePathPrefix(module)
            pathTrimmed.contains("src", true)
                    && pathTrimmed.contains("main", true)
                    && pathTrimmed.contains("res", true)
        }
    }

    private fun getSourceRoots(module: ModuleEntity): List<SourceRootEntity> {
        return ModuleManager.getInstance(project).findModuleByName(module.name)?.sourceRoots?.map {
            SourceRootEntity(
                it
            )
        }
            ?: throw IllegalStateException("${module.name} module doesn't exist!")
    }

    private fun String.removeModulePathPrefix(module: ModuleEntity) =
        removePrefix(getProjectPath() + "/" + module.nameWithoutPrefix)
}