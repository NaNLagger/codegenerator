package domain.entities

data class FileEntity(
    val name: String,
    val content: String,
    val fileType: FileType
) {

    enum class FileType(val displayName: String, val extension: String) {
        KOTLIN("Kotlin", "kt"),
        LAYOUT_XML("Layout XML", "xml");

        override fun toString() = displayName
    }
}