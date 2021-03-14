package di

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import dagger.BindsInstance
import dagger.Component
import presentation.createscreen.CreateScreenDialog
import javax.inject.Singleton

@Singleton
@Component(modules = [ActionModule::class])
interface ActionComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance project: Project, @BindsInstance actionEvent: AnActionEvent): ActionComponent
    }

    fun inject(dialog: CreateScreenDialog)
}