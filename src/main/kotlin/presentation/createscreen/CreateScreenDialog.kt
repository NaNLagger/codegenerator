package presentation.createscreen

import com.intellij.openapi.project.Project
import di.ComponentManager
import io.reactivex.subjects.BehaviorSubject
import presentation.BaseDialog
import utils.addCheckChangeListener
import utils.addSelectedListener
import utils.addTextChangeListener
import javax.inject.Inject

class CreateScreenDialog : BaseDialog<CreateScreenPanel>() {

    @Inject
    lateinit var stateSubject: BehaviorSubject<CreateScreenState>

    @Inject
    lateinit var viewModel: CreateScreenViewModel

    @Inject
    lateinit var project: Project

    init {
        ComponentManager.actionComponent?.inject(this)
        title = "Create Screen"
        init()
    }

    override fun createView(): CreateScreenPanel = CreateScreenPanel(project)

    override fun doOKAction() {
        viewModel.onOkClicked()
        close(OK_EXIT_CODE)
    }

    override fun onViewCreated() {
        stateSubject.subscribe { view.setState(it) }.addLifecycle()

        view.editName.addTextChangeListener { viewModel.onNameChange(it) }
        view.editParentScope.addTextChangeListener { viewModel.onParentScopeChange(it) }
        view.argumentHolderCheckBox.addCheckChangeListener { viewModel.onArgumentHolderChange(it) }
        view.comboBoxViewComponent.addSelectedListener { viewModel.onViewComponentTypeChange(it) }
        view.comboBoxBinding.addSelectedListener { viewModel.onBindingTypeChange(it) }
    }

}