package presentation.createscreen

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import di.ComponentManager

class CreateScreenAnAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        ComponentManager.createActionComponent(e.project!!, e)
        CreateScreenDialog().show()
    }
}