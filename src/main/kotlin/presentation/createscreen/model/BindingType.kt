package presentation.createscreen.model

enum class BindingType(val title: String) {
    ViewBinding("View Binding"),
    Synthetic("kotlin synthetic(deprecated)");

    override fun toString(): String {
        return title
    }}