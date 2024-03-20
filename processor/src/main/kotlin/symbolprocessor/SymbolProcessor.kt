package symbolprocessor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import model.ApiModel
import model.createApiModel
import model.isInPackage

internal class SymbolProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    private lateinit var apiModels: Sequence<ApiModel>

    override fun process(resolver: Resolver): List<KSAnnotated> {

        val packageToScan = options["packageToScan"].orEmpty()
        logger.info("Scanning '$packageToScan' package")

        apiModels = resolver
            .getAllFiles()
            .filter { it.isInPackage(packageToScan) }
            .map { it.createApiModel(logger) }
            .flatten()

        return emptyList()
    }

    override fun finish(): Unit = apiModels.forEach { writeToFile(it) }

    private fun writeToFile(model: ApiModel): Unit = codeGenerator
        .createNewFile(
            Dependencies(aggregating = true, model.containingFile),
            packageName = model.fullClassName.getQualifier(),
            fileName = model.fullClassName.getShortName(),
            "json"
        )
        .write(model.toJsonObject().toString().toByteArray())

}