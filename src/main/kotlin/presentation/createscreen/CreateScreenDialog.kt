package presentation.createscreen

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import di.ComponentManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.swing.JComponent

class CreateScreenDialog : DialogWrapper(true) {

    @Inject
    lateinit var scope: CoroutineScope

    @Inject
    lateinit var stateFlow: MutableStateFlow<CreateScreenState>

    @Inject
    lateinit var viewModel: CreateScreenViewModel

    @Inject
    lateinit var project: Project

    private var panel: CreateScreenPanel? = null

    init {
        ComponentManager.actionComponent?.inject(this)
        scope.launch { stateFlow.collect { panel?.setState(it) } }
        title = "Create Screen"
        init()
    }

    override fun createCenterPanel(): JComponent? {
        panel = CreateScreenPanel(project)
        return panel
    }

    override fun doOKAction() {
        val uiPanel = panel ?: return
        viewModel.onOkClicked(
            uiPanel.nameTextField.text,
            uiPanel.packageTextField.text,
            uiPanel.argumentHolderCheckBox.isSelected,
            uiPanel.parentScopeTextField.text
        )
        close(OK_EXIT_CODE)
    }

    override fun dispose() {
        scope.cancel()
        super.dispose()
    }
}