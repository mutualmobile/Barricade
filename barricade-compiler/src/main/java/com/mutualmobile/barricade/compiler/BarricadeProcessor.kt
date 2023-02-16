package com.mutualmobile.barricade.compiler

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.validate
import com.mutualmobile.barricade.annotation.Barricade
import com.mutualmobile.barricade.compiler.utils.writeToFile
import com.mutualmobile.barricade.compiler.visitors.BarricadeVisitor
import java.io.OutputStream

class BarricadeProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {
    companion object {
        const val PACKAGE_NAME = "com.mutualmobile.barricade"
        private const val GENERATED_FILE_NAME = "BarricadeConfig"
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver
            .getSymbolsWithAnnotation(Barricade::class.java.name)
            .filterIsInstance<KSFunctionDeclaration>()

        if (!symbols.iterator().hasNext()) return emptyList()

        val file: OutputStream = codeGenerator.createNewFile(
            dependencies = Dependencies(
                aggregating = false,
                *resolver.getAllFiles().toList().toTypedArray()
            ),
            packageName = PACKAGE_NAME,
            fileName = GENERATED_FILE_NAME
        )

        file writeToFile "package $PACKAGE_NAME\n\n"
        file writeToFile "class $GENERATED_FILE_NAME private constructor() : $PACKAGE_NAME.IBarricadeConfig {\n\n"

        file writeToFile "\tobject Endpoints {\n"
        file writeToFile "\t\tconst val RANDOM = \"random\"\n"
        file writeToFile "\t}\n\n"
        file writeToFile "\tobject Responses {\n"
        file writeToFile "\t\tconst val SUCCESS = 0\n"
        file writeToFile "\t\tconst val FAILURE = 1\n"
        file writeToFile "\t}\n\n"

        file writeToFile "\tcompanion object {"
        file writeToFile "\n\t\tprivate var instance: BarricadeConfig? = null\n"
        file writeToFile "\t\tfun getInstance(): BarricadeConfig {\n"
        file writeToFile "\t\t\tinstance?.let { nnInstance ->\n"
        file writeToFile "\t\t\t\treturn nnInstance\n"
        file writeToFile "\t\t\t} ?: run {\n"
        file writeToFile "\t\t\t\tinstance = BarricadeConfig()\n"
        file writeToFile "\t\t\t\treturn instance as BarricadeConfig\n"
        file writeToFile "\t\t\t}\n"
        file writeToFile "\t\t}\n"
        file writeToFile "\t}\n"

        file writeToFile "\tprivate val configs = hashMapOf<String, $PACKAGE_NAME.response.BarricadeResponseSet>()\n\n"
        file writeToFile "\tinit {\n"
        file writeToFile "\t\tval barricadeResponsesForRandom = mutableListOf<com.mutualmobile.barricade.response.BarricadeResponse>()\n"
        symbols.forEach { symbol ->
            symbol.accept(BarricadeVisitor(file, logger), Unit)
        }
        file writeToFile "\t\tconfigs.put(\"random\", com.mutualmobile.barricade.response.BarricadeResponseSet(barricadeResponsesForRandom, 0))\n"
        file writeToFile "\t}\n\n"
        file writeToFile "\toverride fun getConfigs(): HashMap<String, $PACKAGE_NAME.response.BarricadeResponseSet> = configs\n"
        file writeToFile "\toverride fun getResponseForEndpoint(endpoint: String): $PACKAGE_NAME.response.BarricadeResponse? {\n"
        file writeToFile "\t\tval responseSet = configs[endpoint]\n"
        file writeToFile "\t\tresponseSet?.let { nnResponseSet ->\n"
        file writeToFile "\t\t\treturn nnResponseSet.responses[nnResponseSet.defaultIndex]\n"
        file writeToFile "\t\t}\n"
        file writeToFile "\t\treturn null\n"
        file writeToFile "\t}\n"
        file writeToFile "}\n"

        file.close()

        val unableToProcess = symbols.filterNot { symbol -> symbol.validate() }.toList()
        return unableToProcess
    }
}
