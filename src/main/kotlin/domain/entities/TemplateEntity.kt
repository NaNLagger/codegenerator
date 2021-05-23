package domain.entities

data class TemplateEntity(val filePath: String, val templateId: String) {

    val fileName: String
        get() = filePath.substring(filePath.lastIndexOf('/'))

    override fun toString(): String {
        return fileName
    }
}