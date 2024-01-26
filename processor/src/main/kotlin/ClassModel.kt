package tr.com

import com.google.devtools.ksp.symbol.KSPropertyDeclaration

internal class ClassModel(
    val qualifiedName: String,
    val docString: String,
    properties: Sequence<KSPropertyDeclaration> = emptySequence()
) {
    val properties = properties.map { Property(it.simpleName.asString(), it.docString!!) }

    data class Property(val name: String, val doc: String)
}