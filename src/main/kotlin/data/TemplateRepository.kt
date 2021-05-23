package data

import domain.BaseTemplate
import domain.Template
import domain.VelocityTemplate
import domain.entities.FileEntity
import org.apache.velocity.app.VelocityEngine
import javax.inject.Inject

class TemplateRepository @Inject constructor(
    private val velocityEngine: VelocityEngine
) {

    private val templates: MutableMap<String, Template> = mutableMapOf()

    fun getTemplate(name: String): Template {
        if (templates.isEmpty()) load()
        return templates[name] ?: throw IllegalArgumentException("Template with name $name, not found")
    }

    private fun load() {
        templates[MVP_VIEW_TEMPLATE] = BaseTemplate(
            MVP_VIEW_TEMPLATE,
            "%name%View",
            Resources.getResourceContent("/view.template"),
            FileEntity.FileType.KOTLIN
        )
        templates[MVP_PRESENTER_TEMPLATE] = BaseTemplate(
            MVP_PRESENTER_TEMPLATE,
            "%name%Presenter",
            Resources.getResourceContent("/presenter.template"),
            FileEntity.FileType.KOTLIN
        )
        templates[MVP_TOOLBAR_FRAGMENT_TEMPLATE] = VelocityTemplate(
            velocityEngine,
            MVP_TOOLBAR_FRAGMENT_TEMPLATE,
            "%name%Fragment",
            Resources.getResourceContent("/toolbar_fragment.template"),
            FileEntity.FileType.KOTLIN
        )
        templates[SCREEN_DATA_TEMPLATE] = BaseTemplate(
            SCREEN_DATA_TEMPLATE,
            "%name%ScreenData",
            Resources.getResourceContent("/screen_data.template"),
            FileEntity.FileType.KOTLIN
        )
        templates[DEFAULT_LAYOUT_TEMPLATE] = BaseTemplate(
            DEFAULT_LAYOUT_TEMPLATE,
            "fmt_%nameSnakeCase%",
            Resources.getResourceContent("/layout.template"),
            FileEntity.FileType.LAYOUT_XML
        )
    }

    companion object {
        const val MVP_VIEW_TEMPLATE = "MVP View"
        const val MVP_TOOLBAR_FRAGMENT_TEMPLATE = "MVP Toolbar Fragment"
        const val MVP_PRESENTER_TEMPLATE = "MVP Presenter"
        const val SCREEN_DATA_TEMPLATE = "Screen Data"
        const val DEFAULT_LAYOUT_TEMPLATE = "default layout"
    }
}