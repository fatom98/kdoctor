package tr.com

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

internal fun KSClassDeclaration.isDocumented() = !this.docString.isNullOrBlank()

internal fun KSPropertyDeclaration.isDocumented(): Boolean = !this.docString.isNullOrBlank()

internal fun KSClassDeclaration.toClassModel(): ClassModel {

    val qualifiedName = this.qualifiedName ?: this.simpleName
    val docString = this.docString!!
    val properties = this.getAllProperties().filter { it.isDocumented() }

    return ClassModel(
        qualifiedName = qualifiedName.asString(),
        docString = docString,
        properties = properties
    )
}

internal fun Sequence<ClassModel>.toJsonObject() = buildJsonObject {
    this@toJsonObject.forEach { model ->
        putJsonObject(model.qualifiedName) {
            put("docString", model.docString)
            putJsonObject("properties") {
                model.properties.forEach { put(it.name, it.doc) }
            }
        }
    }
}