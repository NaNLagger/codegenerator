package domain.entities

data class ModuleEntity(
    val name: String,
    val nameWithoutPrefix: String
) {
    override fun toString() = nameWithoutPrefix
}