package model

import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*

internal class Property(prop: KSPropertyDeclaration) {
    val name = prop.simpleName.asString()
    val doc = DocString(prop.docString)

    fun toJsonObject(): JsonObject = buildJsonObject {
        put("name", name)
        put("doc", doc.value)
    }
}

internal class Function(func: KSFunctionDeclaration) {
    val name = func.simpleName.asString()
    val paramTypes = func.parameters.map { it.type.toString() }
    val doc = DocString(func.docString)

    @OptIn(ExperimentalSerializationApi::class)
    fun toJsonObject(): JsonObject = buildJsonObject {
        put("name", name)
        putJsonArray("paramTypes") { addAll(paramTypes) }
        put("doc", doc.value)
    }

    override fun toString(): String = name
}

internal class EnumConstant(enum: KSDeclaration) {
    val name = enum.simpleName.asString()
    val doc = DocString(enum.docString)

    fun toJsonObject(): JsonObject = buildJsonObject {
        put("name", name)
        put("doc", doc.value)
    }
}

internal class DocString(docString: String?) {
    val value = docString.orEmpty().trimStart()
}