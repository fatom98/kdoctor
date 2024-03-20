package model

import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*

internal class Property(declaration: KSPropertyDeclaration) {

    val name = declaration.simpleName.asString()
    val doc = DocString.from(declaration.docString)

    fun toJsonObject(): JsonObject = buildJsonObject {
        put("name", name)
        put("doc", doc.value)
    }
}

internal class Function(declaration: KSFunctionDeclaration) {

    val name = declaration.simpleName.asString()
    val paramTypes = declaration.parameters.map { it.type.toString() }
    val doc = DocString.from(declaration.docString)
    val isConstructor: Boolean = declaration.isConstructor()

    @OptIn(ExperimentalSerializationApi::class)
    fun toJsonObject(): JsonObject = buildJsonObject {
        put("name", name)
        putJsonArray("paramTypes") { addAll(paramTypes) }
        put("doc", doc.value)
    }

    override fun toString(): String = name
}

internal class EnumConstant(declaration: KSDeclaration) {
    val name = declaration.simpleName.asString()
    val doc = DocString.from(declaration.docString)

    fun toJsonObject(): JsonObject = buildJsonObject {
        put("name", name)
        put("doc", doc.value)
    }
}

@JvmInline
value class DocString private constructor(val value: String) {

    fun isEmpty() = value.isEmpty()

    companion object {
        fun from(value: String?): DocString = DocString(value?.trimStart() ?: "")
    }
}