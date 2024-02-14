package model

import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray

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
        // Enums and java files have duplicated class doc again on constructor
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

}