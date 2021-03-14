package presentation.createscreen

import data.FileCreator
import data.TemplateRepository
import data.WriteActionDispatcher
import domain.entities.FileEntity
import domain.entities.VariableEntity
import domain.interactor.ActionInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import utils.toSnakeCase
import javax.inject.Inject

class CreateScreenViewModel @Inject constructor(
    private val actionInteractor: ActionInteractor,
    private val templateRepository: TemplateRepository,
    private val stateFlow: MutableStateFlow<CreateScreenState>,
    private val writeActionDispatcher: WriteActionDispatcher
) {

    init {
        loadInitState()
    }

    fun onOkClicked(name: String, packageName: String, useArgument: Boolean, parentScope: String) {
        val screenPackageName = "${packageName}.${name.toLowerCase()}"
        val viewPackageName = "${screenPackageName}.view"
        val presenterPackageName = "${screenPackageName}.presenter"
        val modelPackageName = "${screenPackageName}.model"
        val fragmentTemplateRes = if (useArgument) {
            TemplateRepository.MVP_FRAGMENT_WITH_ARGUMENT_TEMPLATE
        } else {
            TemplateRepository.MVP_FRAGMENT_TEMPLATE
        }
        val viewTemplate = templateRepository.getTemplate(TemplateRepository.MVP_VIEW_TEMPLATE)
        val fragmentTemplate = templateRepository.getTemplate(fragmentTemplateRes)
        val presenterTemplate = templateRepository.getTemplate(TemplateRepository.MVP_PRESENTER_TEMPLATE)
        val screenDataTemplate = templateRepository.getTemplate(TemplateRepository.SCREEN_DATA_TEMPLATE)
        val layoutTemplate = templateRepository.getTemplate(TemplateRepository.DEFAULT_LAYOUT_TEMPLATE)
        val args = mapOf(
            VariableEntity.NAME to name,
            VariableEntity.PACKAGE_NAME to screenPackageName,
            VariableEntity.NAME_SNAKE_CASE to name.toSnakeCase(),
            VariableEntity.PARENT_SCOPE to "DI.$parentScope"
        )
        val viewFile = viewTemplate.generate(args)
        val fragmentFile = fragmentTemplate.generate(args)
        val presenterFile = presenterTemplate.generate(args)
        val screenDataFile = screenDataTemplate.generate(args)
        val layoutFile = layoutTemplate.generate(args)
        writeActionDispatcher.dispatch {
            actionInteractor.addFileToPackage(viewFile, viewPackageName)
            actionInteractor.addFileToPackage(fragmentFile, viewPackageName)
            actionInteractor.addFileToPackage(presenterFile, presenterPackageName)
            actionInteractor.addFileToResources(layoutFile, FileCreator.LAYOUT_DIRECTORY)
            if (useArgument) {
                actionInteractor.addFileToPackage(screenDataFile, modelPackageName)
            }
        }
    }

    private fun loadInitState() {
        stateFlow.value = CreateScreenState(
            packageName = actionInteractor.getCurrentPackage(),
            diScopes = actionInteractor.getDiProperties()
        )
    }
}