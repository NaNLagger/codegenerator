package presentation.createscreen

import com.intellij.openapi.project.Project
import com.intellij.ui.EditorTextField
import com.intellij.ui.TextFieldWithAutoCompletion
import presentation.constraintsLeft
import presentation.constraintsRight
import java.awt.Dimension
import java.awt.GridBagLayout
import javax.swing.BoxLayout
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JPanel

class CreateScreenPanel(
    private val project: Project
) : JPanel() {

    val nameTextField = EditorTextField()
    val packageTextField = EditorTextField()
    val parentScopeTextField = TextFieldWithAutoCompletion.create(project, emptyList(), false, "")
    val argumentHolderCheckBox = JCheckBox()

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        add(JPanel().apply {
            layout = GridBagLayout()
            add(JLabel("Name:"), constraintsLeft(0, 0))
            add(nameTextField, constraintsRight(1, 0))
            add(JLabel("Package:"), constraintsLeft(0, 2))
            add(packageTextField, constraintsRight(1, 2))
            add(JLabel("Parent Scope:"), constraintsLeft(0, 3))
            add(parentScopeTextField, constraintsRight(1, 3))
            add(JLabel("Use Argument Holder"), constraintsLeft(0, 4))
            add(argumentHolderCheckBox, constraintsRight(1, 4))
        })
    }

    override fun getPreferredSize() = Dimension(350, 110)

    fun setState(state: CreateScreenState) {
        nameTextField.text = state.name
        packageTextField.text = state.packageName
        argumentHolderCheckBox.isSelected = state.useArgumentHolder
        parentScopeTextField.setVariants(state.diScopes)
    }
}