package domain

import domain.entities.FileEntity
import domain.entities.VariableEntity

interface Template {
    fun generate(args: Map<VariableEntity, String>): FileEntity
    fun generateName(args: Map<VariableEntity, String>): String
}