package presentation.createscreen

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.EditorTextField
import com.intellij.ui.TextFieldWithAutoCompletion
import com.intellij.ui.treeStructure.Tree
import presentation.createscreen.model.BindingType
import presentation.createscreen.model.ViewComponentType
import utils.expandAll
import java.awt.*
import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

class CreateScreenPanel(
    private val project: Project
) : JPanel() {

    private val rootNode = DefaultMutableTreeNode("root")

    val editName = EditorTextField()
    val editPackage = EditorTextField().apply { isEnabled = false }
    val editParentScope = TextFieldWithAutoCompletion.create(project, emptyList(), false, "")
    val textAreaPreview = JTextArea().apply { isEditable = false }
    val treePreview = Tree(rootNode).apply { isRootVisible = false }
    val argumentHolderCheckBox = JCheckBox()
    val comboBoxViewComponent = ComboBox(ViewComponentType.values())
    val comboBoxBinding = ComboBox(BindingType.values())

    init {
        layout = GridBagLayout().apply {
            columnWidths = intArrayOf(200, 300, 0)
            rowHeights = intArrayOf(250, 200, 0)
            columnWeights = doubleArrayOf(1.0, 1.0, Double.MIN_VALUE)
            rowWeights = doubleArrayOf(1.0, 1.0, Double.MIN_VALUE)
        }
        add(createFormPanel(), GridBagConstraints().apply {
            insets = Insets(10, 10, 10, 10)
            weightx = 1.0
            anchor = GridBagConstraints.NORTH
            fill = GridBagConstraints.BOTH
            gridx = 0
            gridy = 0
        })
        add(createPreviewPanel(), GridBagConstraints().apply {
            insets = Insets(10, 10, 10, 10)
            weightx = 2.0
            anchor = GridBagConstraints.NORTH
            fill = GridBagConstraints.BOTH
            gridx = 1
            gridy = 0
        })
        add(textAreaPreview, GridBagConstraints().apply {
            gridwidth = 2
            insets = Insets(10, 10, 10, 10)
            fill = GridBagConstraints.BOTH
            gridx = 0
            gridy = 1
        })
    }

    override fun getPreferredSize() = Dimension(650, 300)

    fun setState(state: CreateScreenState) {
        editName.text = state.name
        editPackage.text = state.packageName
        argumentHolderCheckBox.isSelected = state.useArgumentHolder
        editParentScope.setVariants(state.diScopes)
        updateTree(state)
    }

    private fun updateTree(state: CreateScreenState) {
        rootNode.removeAllChildren()
        rootNode.add(state.previewNodes)
        (treePreview.model as DefaultTreeModel).reload()
        treePreview.expandAll()
    }

    private fun createPreviewPanel(): JPanel {
        val previewPanel = JPanel().apply {
            layout = GridBagLayout().apply {
                columnWidths = intArrayOf(0, 0)
                rowHeights = intArrayOf(0, 0)
                columnWeights = doubleArrayOf(1.0, Double.MIN_VALUE)
                rowWeights = doubleArrayOf(1.0, Double.MIN_VALUE)
            }
        }
        previewPanel.add(JScrollPane(treePreview), GridBagConstraints().apply {
            fill = GridBagConstraints.BOTH
            gridx = 0
            gridy = 0
        })
        return previewPanel
    }

    private fun createFormPanel(): JPanel {
        val formPanel = JPanel().apply {
            layout = GridBagLayout().apply {
                columnWidths = intArrayOf(0, 0, 0)
                rowHeights = intArrayOf(0, 0, 0, 0, 0, 0)
                columnWeights = doubleArrayOf(0.0, 1.0, Double.MIN_VALUE)
                rowWeights = doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE)
            }
        }
        formPanel.add(JLabel("Name:"), GridBagConstraints().apply {
            insets = Insets(0, 0, 5, 5)
            anchor = GridBagConstraints.WEST
            gridx = 0
            gridy = 0
        })
        formPanel.add(editName, GridBagConstraints().apply {
            insets = Insets(0, 0, 5, 0)
            fill = GridBagConstraints.HORIZONTAL
            gridx = 1
            gridy = 0
        })

        formPanel.add(JLabel("Package:"), GridBagConstraints().apply {
            anchor = GridBagConstraints.WEST
            insets = Insets(0, 0, 5, 5)
            gridx = 0
            gridy = 1
        })
        formPanel.add(editPackage, GridBagConstraints().apply {
            insets = Insets(0, 0, 5, 0)
            fill = GridBagConstraints.HORIZONTAL
            gridx = 1
            gridy = 1
        })

        formPanel.add(JLabel("Parent Scope:"), GridBagConstraints().apply {
            anchor = GridBagConstraints.WEST
            insets = Insets(0, 0, 5, 5)
            gridx = 0
            gridy = 2
        })
        formPanel.add(editParentScope, GridBagConstraints().apply {
            insets = Insets(0, 0, 5, 0)
            fill = GridBagConstraints.HORIZONTAL
            gridx = 1
            gridy = 2
        })

        formPanel.add(JLabel("View Component:"), GridBagConstraints().apply {
            anchor = GridBagConstraints.WEST
            insets = Insets(0, 0, 5, 5)
            gridx = 0
            gridy = 3
        })
        formPanel.add(comboBoxViewComponent, GridBagConstraints().apply {
            insets = Insets(0, 0, 5, 0)
            fill = GridBagConstraints.HORIZONTAL
            gridx = 1
            gridy = 3
        })

        formPanel.add(JLabel("Binding:"), GridBagConstraints().apply {
            anchor = GridBagConstraints.WEST
            insets = Insets(0, 0, 5, 5)
            gridx = 0
            gridy = 4
        })
        formPanel.add(comboBoxBinding, GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            gridx = 1
            gridy = 4
        })

        formPanel.add(JLabel("Use Argument Holder:"), GridBagConstraints().apply {
            anchor = GridBagConstraints.WEST
            insets = Insets(0, 0, 5, 5)
            gridx = 0
            gridy = 5
        })
        formPanel.add(argumentHolderCheckBox, GridBagConstraints().apply {
            fill = GridBagConstraints.NONE
            gridx = 1
            gridy = 5
        })
        return formPanel
    }
}