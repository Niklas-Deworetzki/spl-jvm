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
 * This class is used to generate the assembly code for the compiled program.
 *
 * @param options The command line options passed to the compiler
 */
class CodeGenerator(private val options: CommandLineOptions) {

    fun generateCode(program: Program, table: SymbolTable) {
        val bytecodeGenerator = BytecodeGenerator(options, program, table)
        bytecodeGenerator.generateCode()

        generateJar(options.outputFile ?: File("$GENERATED_CLASS_NAME.jar"), bytecodeGenerator)
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
