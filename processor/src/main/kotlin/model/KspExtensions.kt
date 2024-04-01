package model

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*

internal fun KSFile.isInPackage(packageName: String): Boolean = this.packageName
    .asString()
    .contains(packageName)

internal fun KSFile.createApiModel(logger: KSPLogger): List<ApiModel> {

    logger.info("Scanning '${fullName}' file")

    val allClassDeclarations = getAllClassDeclarations(declarations, logger=logger)
    return allClassDeclarations.mapNotNull { it.toApiModel() }
}

internal fun KSClassDeclaration.toApiModel(): ApiModel? {

    val documentedProperties = getAllProperties().filter { it.isDocumented() }
    val documentedFunctions = getAllFunctions().filter { it.isDocumented() }
    val documentedEnumConstants = getAllDocumentedEnumConstants(declarations)

    val classDocumented = !docString.isNullOrEmpty()
            || documentedProperties.any()
            || documentedFunctions.any()
            || documentedEnumConstants.any()

    if (!classDocumented)
        return null

    val containingFile = containingFile
    val fullClassName = qualifiedName ?: simpleName

    require(containingFile != null) {
        "File must be a source file. File name: ${fullClassName.asString()}"
    }

    return ApiModel(
        containingFile = containingFile,
        fullClassName = fullClassName,
        docString = DocString.from(docString),
        propertyDeclarations = documentedProperties,
        enumConstantDeclarations = documentedEnumConstants,
        functionsDeclarations = documentedFunctions
    )
}

private val KSFile.fullName: String get() = packageName.asString() + fileName

private fun KSPropertyDeclaration.isDocumented(): Boolean = !docString.isNullOrEmpty()

private fun KSFunctionDeclaration.isDocumented(): Boolean = !docString.isNullOrEmpty()

private fun KSDeclaration.isDocumented(): Boolean = !docString.isNullOrEmpty()

private fun getAllDocumentedEnumConstants(
    declarations: Sequence<KSDeclaration>
): Sequence<KSClassDeclaration> = declarations
    .filterIsInstance<KSClassDeclaration>()
    .filter { it.classKind == ClassKind.ENUM_ENTRY }
    .filter { it.isDocumented() }

private fun getAllClassDeclarations(
    declarations: Sequence<KSDeclaration>,
    allClassDeclarations: MutableList<KSClassDeclaration> = mutableListOf(),
    logger: KSPLogger
): List<KSClassDeclaration> {

    val classDeclarations = declarations.filterIsInstance<KSClassDeclaration>()

    if (classDeclarations.none())
        return allClassDeclarations

    allClassDeclarations.addAll(classDeclarations)

    return getAllClassDeclarations(
        classDeclarations.map { it.declarations }.flatten(),
        allClassDeclarations,
        logger
    )

}