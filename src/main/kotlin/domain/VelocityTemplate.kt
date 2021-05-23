package domain

import domain.entities.FileEntity
import domain.entities.VariableEntity
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import java.io.StringWriter

class VelocityTemplate(
    private val velocityEngine: VelocityEngine,
    name: String,
    templateName: String,
    templateBody: String,
    fileType: FileEntity.FileType
) : BaseTemplate(name, templateName, templateBody, fileType) {

    override fun generate(args: Map<VariableEntity, String>): FileEntity {
        val context = VelocityContext()
        args.forEach { (entity, value) ->
            context.put(entity.name, value)
        }
        val writer = StringWriter()
        velocityEngine.evaluate(context, writer, templateName, templateBody)
        return FileEntity(generateName(args), writer.toString(), fileType)
    }
}