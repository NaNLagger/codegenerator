package data

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import javax.inject.Inject


class WriteActionDispatcher @Inject constructor(
    private val project: Project
) {

    fun dispatch(action: () -> Unit) =
        WriteCommandAction.runWriteCommandAction(project, COMMAND_NAME, GROUP_ID, Runnable {
            action()
        })

    companion object {
        private const val COMMAND_NAME = "Code Generator"
        private const val GROUP_ID = "CODE_GENERATOR_ID"
    }
}
