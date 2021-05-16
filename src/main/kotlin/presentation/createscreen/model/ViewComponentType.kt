package presentation.createscreen.model

enum class ViewComponentType(val title: String) {
    Toolbar("ToolbarFragment"),
    BottomDialog("BaseBottomDialogFragment"),
    Flow("CoreFlowFragment");

    override fun toString(): String {
        return title
    }
}