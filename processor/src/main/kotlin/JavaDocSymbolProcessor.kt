package tr.com

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import kotlinx.serialization.json.JsonObject

class JavaDocSymbolProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {

        val packageToScan = options["packageToScan"]

        val jsonObject = resolver
            .getAllFiles()
            .filter { it.packageName.asString() == packageToScan }
            .flatMap { it.declarations }
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.isDocumented() }
            .map { it.toClassModel() }
            .toJsonObject()

        writeToFile(jsonObject)
        return emptyList()
    }

    private fun writeToFile(json: JsonObject) {
        runCatching {
            codeGenerator
                .createNewFileByPath(Dependencies(false), "class-model", "json")
                .write(json.toString().toByteArray())
        }
    }

}