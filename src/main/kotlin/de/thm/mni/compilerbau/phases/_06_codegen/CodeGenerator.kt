package de.thm.mni.compilerbau.phases._06_codegen

import de.thm.mni.compilerbau.CommandLineOptions
import de.thm.mni.compilerbau.absyn.Program
import de.thm.mni.compilerbau.jvm.SplJvmDefinitions.GENERATED_CLASS_NAME
import de.thm.mni.compilerbau.jvm.SplJvmDefinitions.LIBRARY_CLASS_NAME
import de.thm.mni.compilerbau.jvm.SplJvmDefinitions.REFERENCE_INTEGER_CLASS_NAME
import de.thm.mni.compilerbau.table.SymbolTable
import java.io.File
import java.io.FileOutputStream
import java.util.jar.Attributes
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import java.util.jar.Manifest

/**
 * This class handles the code generation and output file creation.
 */
class CodeGenerator(private val options: CommandLineOptions) {

    fun generateCode(program: Program, table: SymbolTable) {
        val bytecodeGenerator = BytecodeGenerator(options, program, table)
        bytecodeGenerator.generateCode()

        if (options.generateClass) {
            val outputFile = options.outputFile ?: File("$GENERATED_CLASS_NAME.class")
            generateClass(outputFile, bytecodeGenerator)
        } else {
            val outputFile = options.outputFile ?: File("$GENERATED_CLASS_NAME.jar")
            generateJar(outputFile, bytecodeGenerator)
        }
    }

    private fun generateClass(file: File, bytecodeGenerator: BytecodeGenerator) {
        FileOutputStream(file).use {
            it.write(bytecodeGenerator.classWriter.toByteArray())
        }
    }

    private fun generateJar(file: File, bytecodeGenerator: BytecodeGenerator) {
        val manifest = createManifest()
        JarOutputStream(FileOutputStream(file), manifest).use {

            it.putNextEntry(JarEntry("$GENERATED_CLASS_NAME.class"))
            it.write(bytecodeGenerator.classWriter.toByteArray())
            it.closeEntry()

            includeBuiltinResource(it, LIBRARY_CLASS_NAME)
            includeBuiltinResource(it, REFERENCE_INTEGER_CLASS_NAME)
        }
    }

    private fun createManifest(): Manifest {
        val manifest = Manifest()
        manifest.mainAttributes[Attributes.Name.MANIFEST_VERSION] = "1.0"
        manifest.mainAttributes[Attributes.Name.MAIN_CLASS] = LIBRARY_CLASS_NAME
        return manifest
    }

    private fun includeBuiltinResource(jarOutputStream: JarOutputStream, name: String) {
        val resource = javaClass.getResourceAsStream("/spllib/$name.class")
        if (resource == null) {
            System.err.println("Resource $name not found.")
            return
        }

        jarOutputStream.putNextEntry(JarEntry("$name.class"))
        resource.use { it.transferTo(jarOutputStream) }
        jarOutputStream.closeEntry()
    }

}
