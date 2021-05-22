package presentation.createscreen

import data.TemplateRepository
import data.WriteActionDispatcher
import domain.entities.FileEntity
import domain.entities.TemplateEntity
import domain.entities.VariableEntity
import domain.interactor.ActionInteractor
import io.reactivex.subjects.BehaviorSubject
import presentation.createscreen.model.BindingType
import presentation.createscreen.model.ViewComponentType
import utils.toSnakeCase
import javax.inject.Inject
import javax.swing.tree.DefaultMutableTreeNode

class CreateScreenViewModel @Inject constructor(
    private val actionInteractor: ActionInteractor,
    private val templateRepository: TemplateRepository,
    private val stateSubject: BehaviorSubject<CreateScreenState>,
    private val writeActionDispatcher: WriteActionDispatcher
) {

    private lateinit var screenState: CreateScreenState

    init {
        loadInitState()
    }

    fun onOkClicked() {
        val templateEntities = createTemplateEntities()
        val args = mapOf(
            VariableEntity.NAME to screenState.name,
            VariableEntity.PACKAGE_NAME to screenState.packageName,
            VariableEntity.NAME_SNAKE_CASE to screenState.name.toSnakeCase(),
            VariableEntity.PARENT_SCOPE to screenState.parentScope
        )
        writeActionDispatcher.dispatch {
            templateEntities.forEach {
                val template = templateRepository.getTemplate(it.templateId)
                actionInteractor.addFile(it.filePath, template.generate(args))
            }
        }
    }

    private fun loadInitState() {

        screenState = CreateScreenState(
            packageName = actionInteractor.getCurrentPackage(),
            diScopes = actionInteractor.getDiProperties()
        )
        screenState = screenState.copy(previewNodes = pathToTree(createTemplateEntities()))
        stateSubject.onNext(screenState)
    }

    fun onNameChange(name: String) {
        screenState = screenState.copy(name = name)
        screenState = screenState.copy(previewNodes = pathToTree(createTemplateEntities()))
        stateSubject.onNext(screenState)
    }

    fun onParentScopeChange(parentScope: String) {
        screenState = screenState.copy(parentScope = parentScope)
        stateSubject.onNext(screenState)
    }

    fun onViewComponentTypeChange(viewComponentType: ViewComponentType) {
        screenState = screenState.copy(viewComponentType = viewComponentType)
        stateSubject.onNext(screenState)
    }

    fun onBindingTypeChange(bindingType: BindingType) {
        screenState = screenState.copy(bindingType = bindingType)
        stateSubject.onNext(screenState)
    }

    fun onArgumentHolderChange(useArgument: Boolean) {
        screenState = screenState.copy(useArgumentHolder = useArgument)
        stateSubject.onNext(screenState)
    }

    private fun pathToTree(templates: List<TemplateEntity>): DefaultMutableTreeNode {
        val modulePath = actionInteractor.getModuleMainPath()
        val resultNodes: MutableMap<String, DefaultMutableTreeNode> = mutableMapOf()
        var rootNode: DefaultMutableTreeNode? = null
        templates.forEach { entity ->
            val path = entity.filePath.replace(modulePath, "")
            val packagePath = screenState.packageName.replace('.', '/')
            val clearPath = path.replace(packagePath, screenState.packageName)
            val nodes = clearPath.split('/')
            nodes.forEachIndexed { index, node ->
                val prevNode = nodes.getOrNull(index - 1)
                if (prevNode == null && resultNodes.containsKey(node).not()) {
                    resultNodes[node] = DefaultMutableTreeNode(node)
                    rootNode = resultNodes[node]
                } else if (resultNodes.containsKey(node).not()) {
                    resultNodes[node] = DefaultMutableTreeNode(node)
                    resultNodes[prevNode]?.add(resultNodes[node])
                }
            }
        }
        return rootNode ?: DefaultMutableTreeNode("main")
    }

    private fun createTemplateEntities(): List<TemplateEntity> {
        return screenState.let {
            val viewPackageName = "${it.packageName}.view"
            val presenterPackageName = "${it.packageName}.presenter"
            val modelPackageName = "${it.packageName}.model"
            val fragmentTemplateRes = getViewComponentTemplateRes(it.viewComponentType, it.useArgumentHolder)
            val viewTemplate = templateRepository.getTemplate(TemplateRepository.MVP_VIEW_TEMPLATE)
            val fragmentTemplate = templateRepository.getTemplate(fragmentTemplateRes)
            val presenterTemplate = templateRepository.getTemplate(TemplateRepository.MVP_PRESENTER_TEMPLATE)
            val screenDataTemplate = templateRepository.getTemplate(TemplateRepository.SCREEN_DATA_TEMPLATE)
            val layoutTemplate = templateRepository.getTemplate(TemplateRepository.DEFAULT_LAYOUT_TEMPLATE)
            val args = mapOf(
                VariableEntity.NAME to it.name,
                VariableEntity.PACKAGE_NAME to it.packageName,
                VariableEntity.NAME_SNAKE_CASE to it.name.toSnakeCase(),
                VariableEntity.PARENT_SCOPE to it.parentScope
            )
            val viewPath = actionInteractor.getFullPathByPackage(
                viewTemplate.generateName(args),
                FileEntity.FileType.KOTLIN,
                viewPackageName
            ) ?: ""
            val presenterPath = actionInteractor.getFullPathByPackage(
                presenterTemplate.generateName(args),
                FileEntity.FileType.KOTLIN,
                presenterPackageName
            ) ?: ""
            val fragmentPath = actionInteractor.getFullPathByPackage(
                fragmentTemplate.generateName(args),
                FileEntity.FileType.KOTLIN,
                viewPackageName
            ) ?: ""
            val modelPath = actionInteractor.getFullPathByPackage(
                screenDataTemplate.generateName(args),
                FileEntity.FileType.KOTLIN,
                modelPackageName
            ) ?: ""
            val layoutPath = actionInteractor.getFullPathByRes(
                layoutTemplate.generateName(args),
                FileEntity.FileType.LAYOUT_XML
            ) ?: ""

            val result: MutableList<TemplateEntity> = mutableListOf()
            result.add(TemplateEntity(viewPath, TemplateRepository.MVP_VIEW_TEMPLATE))
            result.add(TemplateEntity(presenterPath, TemplateRepository.MVP_PRESENTER_TEMPLATE))
            result.add(TemplateEntity(fragmentPath, fragmentTemplateRes))
            if (it.viewComponentType != ViewComponentType.Flow) {
                result.add(TemplateEntity(layoutPath, TemplateRepository.DEFAULT_LAYOUT_TEMPLATE))
            }
            if (it.useArgumentHolder) {
                result.add(TemplateEntity(modelPath, TemplateRepository.SCREEN_DATA_TEMPLATE))
            }
            result
        }
    }

    private fun getViewComponentTemplateRes(
        viewComponentType: ViewComponentType,
        useArgument: Boolean
    ): String {
        return when (viewComponentType) {
            ViewComponentType.Toolbar -> getToolbarTemplateRes(useArgument)
            ViewComponentType.Flow -> getToolbarTemplateRes(useArgument)
            ViewComponentType.BottomDialog -> getToolbarTemplateRes(useArgument)
        }
    }

    private fun getToolbarTemplateRes(useArgument: Boolean): String {
        return if (useArgument) {
            TemplateRepository.MVP_FRAGMENT_WITH_ARGUMENT_TEMPLATE
        } else {
            TemplateRepository.MVP_FRAGMENT_TEMPLATE
        }
    }

    private fun getFlowTemplateRes(useArgument: Boolean): String {
        return "" //TODO
    }

    private fun getBottomDialogTemplateRes(useArgument: Boolean): String {
        return "" //TODO
    }
}