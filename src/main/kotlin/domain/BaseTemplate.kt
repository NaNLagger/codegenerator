package domain

import domain.entities.FileEntity
import domain.entities.VariableEntity

open class BaseTemplate(
    val name: String,
    val templateName: String,
    val templateBody: String,
    val fileType: FileEntity.FileType
) : Template {

    override fun generate(args: Map<VariableEntity, String>): FileEntity {
        return FileEntity(replaceByTemplate(templateName, args), replaceByTemplate(templateBody, args), fileType)
    }

    override fun generateName(args: Map<VariableEntity, String>): String {
        return replaceByTemplate(templateName, args)
    }

    private fun replaceByTemplate(template: String, args: Map<VariableEntity, String>): String {
        var result = template
        args.forEach { (variable, value) ->
            result = result.replace(variable.formattedName, value)
        }
        return result
    }
}