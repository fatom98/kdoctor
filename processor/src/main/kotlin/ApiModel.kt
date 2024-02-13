package tr.com

import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*

internal class ApiModel(
    qualifiedName: KSName,
    docString: String?,
    properties: Sequence<KSPropertyDeclaration>,
    enumConstants: Sequence<KSDeclaration>,
    functions: Sequence<KSFunctionDeclaration>
) {

    val packageName = qualifiedName.getQualifier()
    val fileName = qualifiedName.getShortName()
    val docString = DocString(docString)
    val properties = properties.map { Property(it) }.toList()
    val enumConstants = enumConstants.map { EnumConstant(it) }.toList()
    val functions = functions.filter { !it.isConstructor() }.map { Function(it) }.toList()

    val constructors = functions
        .filter { it.isConstructor() }
        // Enums and java files duplicates class doc again on constructor after build
        .filter { it.docString != docString }
        .map { Function(it) }
        .toList()

    val isDocumented: Boolean
        get() = docString.value.isNotEmpty()
                || properties.isNotEmpty()
                || enumConstants.isNotEmpty()
                || functions.isNotEmpty()
                || constructors.isNotEmpty()

    fun toJsonObject(): JsonObject = buildJsonObject {
        put("doc", docString.value)
        putJsonArray("fields") { properties.forEach { add(it.toJsonObject()) } }
        putJsonArray("enumConstants") { enumConstants.forEach { add(it.toJsonObject()) } }
        putJsonArray("methods") { functions.forEach { add(it.toJsonObject()) } }
        putJsonArray("constructors") { constructors.forEach { add(it.toJsonObject()) } }
    }

    class Property(prop: KSPropertyDeclaration) {
        val name = prop.simpleName.asString()
        val doc = DocString(prop.docString)

        fun toJsonObject(): JsonObject = buildJsonObject {
            put("name", name)
            put("doc", doc.value)
        }
    }

    class Function(func: KSFunctionDeclaration) {
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

    class EnumConstant(enum: KSDeclaration) {
        val name = enum.simpleName.asString()
        val doc = DocString(enum.docString)

        fun toJsonObject(): JsonObject = buildJsonObject {
            put("name", name)
            put("doc", doc.value)
        }
    }

    class DocString(docString: String?) {
        val value = docString.orEmpty().trimStart()
    }

}