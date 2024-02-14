package model

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.symbol.*

internal val KSDeclaration.name: String
    get() = qualifiedName?.asString() ?: simpleName.asString()

internal fun KSName.isInPackage(packageName: String): Boolean = asString().contains(packageName)

internal fun KSPropertyDeclaration.isDocumented(): Boolean = !docString.isNullOrEmpty()

// Catching NPE is necessary because of a bug in KSP. Doc string is lazy but never initialized for java enums.
internal fun KSDeclaration.isDocumented(): Boolean = runCatching {
    !docString.isNullOrEmpty()
}.getOrElse { false }

// Catching NPE is necessary because of a bug in KSP. Doc string is lazy but never initialized for java enums.
internal fun KSFunctionDeclaration.isDocumented(): Boolean = runCatching {
    !docString.isNullOrEmpty()
}.getOrElse { false }

internal fun KSClassDeclaration.toClassModel(): ApiModel {

    val documentedProperties = getDeclaredProperties().filter { it.isDocumented() }
    val documentedFunctions = getDeclaredFunctions().filter { it.isDocumented() }

    val enumConstants =
        if (classKind == ClassKind.ENUM_CLASS)
            declarations.filter { it !is KSFunctionDeclaration }.filter { it.isDocumented() }
        else emptySequence()

    return ApiModel(
        qualifiedName = qualifiedName ?: simpleName,
        docString = docString,
        properties = documentedProperties,
        enumConstants = enumConstants,
        functions = documentedFunctions
    )
}