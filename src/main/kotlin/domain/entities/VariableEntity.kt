package domain.entities

class VariableEntity(
    val name: String
) {

    val formattedName: String = "%$name%"

    companion object {
        val NAME = VariableEntity("name")
        val PACKAGE_NAME = VariableEntity("packageName")
        val NAME_SNAKE_CASE = VariableEntity("nameSnakeCase")
        val PARENT_SCOPE = VariableEntity("parentScope")
    }
}