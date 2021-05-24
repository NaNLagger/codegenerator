package domain.entities

class VariableEntity(
    val name: String
) {

    val formattedName: String = "%$name%"

    companion object {
        val NAME = VariableEntity("name")
        val PACKAGE_NAME = VariableEntity("packageName")
        val PARENT_SCOPE = VariableEntity("parentScope")
        val NAME_SNAKE_CASE = VariableEntity("nameSnakeCase")
        val USE_ARGUMENT = VariableEntity("useArgument")
        val SYNTHETIC = VariableEntity("synthetic")
        val VIEW_BINDING = VariableEntity("viewBinding")
        val MANIFEST_PACKAGE = VariableEntity("manifestPackage")
        val PARENT_SCOPE_PACKAGE = VariableEntity("parentScopePackage")
    }
}