package presentation.createscreen

data class CreateScreenState(
    val name: String = "",
    val packageName: String = "",
    val parentScope: String = "",
    val useArgumentHolder: Boolean = false,
    val diScopes: List<String> = emptyList()
)