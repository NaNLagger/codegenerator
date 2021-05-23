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
        val args = generateArgs()
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

    fun onSelectedNode(node: DefaultMutableTreeNode) {
        val userObject = node.userObject
        screenState = if(userObject is TemplateEntity) {
            val template = templateRepository.getTemplate(userObject.templateId)
            val args = generateArgs()
            screenState.copy(templatePreview = template.generate(args).content)
        } else {
            screenState.copy(templatePreview = "Click on template in tree to see the preview code")
        }
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
                    resultNodes[node] = DefaultMutableTreeNode(if (index == nodes.lastIndex) entity else node)
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
            val fragmentTemplateRes = getViewComponentTemplateRes(it.viewComponentType)
            val viewTemplate = templateRepository.getTemplate(TemplateRepository.MVP_VIEW_TEMPLATE)
            val fragmentTemplate = templateRepository.getTemplate(fragmentTemplateRes)
            val presenterTemplate = templateRepository.getTemplate(TemplateRepository.MVP_PRESENTER_TEMPLATE)
            val screenDataTemplate = templateRepository.getTemplate(TemplateRepository.SCREEN_DATA_TEMPLATE)
            val layoutTemplate = templateRepository.getTemplate(TemplateRepository.DEFAULT_LAYOUT_TEMPLATE)
            val args = generateArgs()
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

    private fun getViewComponentTemplateRes(viewComponentType: ViewComponentType): String {
        return when (viewComponentType) {
            ViewComponentType.Toolbar -> TemplateRepository.MVP_TOOLBAR_FRAGMENT_TEMPLATE
            ViewComponentType.Flow -> TemplateRepository.MVP_TOOLBAR_FRAGMENT_TEMPLATE
            ViewComponentType.BottomDialog -> TemplateRepository.MVP_TOOLBAR_FRAGMENT_TEMPLATE
        }
    }

    private fun generateArgs(): Map<VariableEntity, String> {
        return screenState.let {
            mapOf(
                VariableEntity.NAME to it.name,
                VariableEntity.PACKAGE_NAME to it.packageName,
                VariableEntity.NAME_SNAKE_CASE to it.name.toSnakeCase(),
                VariableEntity.PARENT_SCOPE to it.parentScope,
                VariableEntity.USE_ARGUMENT to if (it.useArgumentHolder) "use" else "",
                VariableEntity.SYNTHETIC to if (it.bindingType == BindingType.Synthetic) "use" else "",
                VariableEntity.VIEW_BINDING to if (it.bindingType == BindingType.ViewBinding) "use" else "",
                VariableEntity.MANIFEST_PACKAGE to it.packageName,
                VariableEntity.PARENT_SCOPE_PACKAGE to it.packageName,
            )
        }
    }
}