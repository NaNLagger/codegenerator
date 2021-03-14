package data

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.module.ModuleUtil
import domain.entities.ModuleEntity
import javax.inject.Inject

class CurrentActionRepository @Inject constructor(
    private val actionEvent: AnActionEvent
) {

    val currentPath: String = extractCurrentPath()
    val currentModuleEntity: ModuleEntity = extractModuleFromEvent()
    val isDirectory: Boolean = extractIsDirectory()

    private fun extractModuleFromEvent(): ModuleEntity {
        return actionEvent.getData(PlatformDataKeys.VIRTUAL_FILE)?.let {
            val moduleName = ModuleUtil.findModuleForFile(it, actionEvent.project!!)?.name ?: ""
            ModuleEntity(
                moduleName,
                moduleName.replace("${actionEvent.project!!.name}.", "")
            )
        } ?: ModuleEntity("", "")
    }

    private fun extractCurrentPath(): String {
        return actionEvent.getData(PlatformDataKeys.VIRTUAL_FILE)?.path ?: ""
    }

    private fun extractIsDirectory(): Boolean {
        return actionEvent.getData(PlatformDataKeys.VIRTUAL_FILE)?.isDirectory ?: false
    }
}