package di

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project

object ComponentManager {

    val actionComponent: ActionComponent?
        get() = _actionComponent

    private var _actionComponent: ActionComponent? = null


    fun createActionComponent(project: Project, actionEvent: AnActionEvent): ActionComponent {
        return DaggerActionComponent.factory().create(project, actionEvent).also {
            _actionComponent = it
        }
    }
}