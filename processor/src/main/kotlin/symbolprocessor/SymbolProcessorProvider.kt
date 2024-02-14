package symbolprocessor

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

internal class SymbolProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) =
        SymbolProcessor(
            environment.options,
            environment.codeGenerator,
            environment.logger
        )
}