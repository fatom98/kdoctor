package model

import com.google.devtools.ksp.symbol.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray

internal class ApiModel(
    val containingFile: KSFile,
    val fullClassName: KSName,
    val docString: DocString,
    propertyDeclarations: Sequence<KSPropertyDeclaration>,
    functionsDeclarations: Sequence<KSFunctionDeclaration>,
    enumConstantDeclarations: Sequence<KSDeclaration>
) {
    val properties = propertyDeclarations.map { Property(it) }
    val functions = functionsDeclarations.map { Function(it) }
    val enumConstants = enumConstantDeclarations.map { EnumConstant(it) }

    fun toJsonObject(): JsonObject = buildJsonObject {
        put("doc", docString.value)
        putJsonArray("fields") { properties.forEach { add(it.toJsonObject()) } }
        putJsonArray("enumConstants") { enumConstants.forEach { add(it.toJsonObject()) } }
        putJsonArray("methods") {
            functions.filter { !it.isConstructor }.forEach { add(it.toJsonObject()) }
        }
        putJsonArray("constructors") {
            functions.filter { it.isConstructor }.forEach { add(it.toJsonObject()) }
        }
    }
}