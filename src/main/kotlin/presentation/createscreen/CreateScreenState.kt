package presentation.createscreen

import presentation.createscreen.model.BindingType
import presentation.createscreen.model.ViewComponentType
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.MutableTreeNode

data class CreateScreenState(
    val name: String = "",
    val packageName: String = "",
    val parentScope: String = "",
    val viewComponentType: ViewComponentType = ViewComponentType.Toolbar,
    val bindingType: BindingType = BindingType.ViewBinding,
    val useArgumentHolder: Boolean = false,
    val diScopes: List<String> = emptyList(),
    val previewNodes: MutableTreeNode = DefaultMutableTreeNode(),
    val templatePreview: String = ""
)