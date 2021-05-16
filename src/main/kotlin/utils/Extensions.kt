package utils

import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.EditorTextField
import com.intellij.ui.treeStructure.Tree
import javax.swing.JCheckBox

fun String.toSnakeCase() = replace(Regex("([^_A-Z])([A-Z])"), "$1_$2").toLowerCase()

fun EditorTextField.addTextChangeListener(listener: (String) -> Unit) {
    this.addDocumentListener(object : DocumentListener {
        override fun documentChanged(event: DocumentEvent) {
            listener(this@addTextChangeListener.text)
        }
    })
}

fun JCheckBox.addCheckChangeListener(listener: (Boolean) -> Unit) {
    this.addActionListener { listener(this.isSelected) }
}

@Suppress("UNCHECKED_CAST")
fun <T> ComboBox<T>.addSelectedListener(listener: (T) -> Unit) {
    this.addActionListener { this.selectedItem?.let { listener(it as T) } }
}

fun Tree.expandAll() {
    var i = 0
    while (i < this.rowCount) {
        this.expandRow(i)
        i++
    }
}