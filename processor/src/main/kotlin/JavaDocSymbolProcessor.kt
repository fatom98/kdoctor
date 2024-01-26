package tr.com

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

class JavaDocSymbolProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {

        val packageToScan = options["packageToScan"]

        val models = resolver
            .getAllFiles()
            .filter { it.packageName.asString() == packageToScan }
            .flatMap { it.declarations }
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.isDocumented() }
            .map(::createModel)

        val json = createJson(models)
        writeJsonToFile(json)

        return emptyList()
    }

    private fun KSClassDeclaration.isDocumented(): Boolean = !this.docString.isNullOrBlank()
    private fun KSPropertyDeclaration.isDocumented(): Boolean = !this.docString.isNullOrBlank()

    private fun createModel(`class`: KSClassDeclaration): ClassModel {

        val qualifiedName = `class`.qualifiedName ?: `class`.simpleName
        val docString = `class`.docString!!
        val properties = `class`.getAllProperties().filter { it.isDocumented() }

        return ClassModel(
            qualifiedName = qualifiedName.asString(),
            docString = docString,
            properties = properties
        )
    }

    private fun createJson(models: Sequence<ClassModel>) = buildJsonObject {
        models.forEach { model ->
            putJsonObject(model.qualifiedName) {
                put("doc", model.docString)
                putJsonObject("properties") {
                    model.properties.forEach { put(it.name, it.doc) }
                }
            }
        }
    }

    private fun writeJsonToFile(json: JsonObject) {
        runCatching {
            codeGenerator
                .createNewFileByPath(Dependencies(false), "class-model", "json")
                .write(json.toString().toByteArray())
        }
    }

}
