package symbolprocessor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import model.ApiModel
import model.isInPackage
import model.name
import model.toClassModel

internal class SymbolProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    private lateinit var models: List<ApiModel>

    override fun process(resolver: Resolver): List<KSAnnotated> {

        val packageToScan = options["packageToScan"].orEmpty()
        logger.info("Scanning '$packageToScan' package")

        models = resolver
            .getAllFiles()
            .filter { it.packageName.isInPackage(packageToScan) }
            .flatMap { it.declarations }
            .filterIsInstance<KSClassDeclaration>()
            .map {
                logger.info("Scanning '${it.name}' file")
                it.toClassModel()
            }
            .toList()

        return emptyList()
    }

    override fun finish() {
        models.filter { it.isDocumented }.forEach { writeToFile(it) }
    }

    private fun writeToFile(model: ApiModel) {
        codeGenerator.createNewFile(
            Dependencies.ALL_FILES,
            model.packageName,
            model.fileName,
            "json"
        ).write(model.toJsonObject().toString().toByteArray())
    }

}