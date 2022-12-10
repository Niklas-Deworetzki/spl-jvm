package de.thm.mni.compilerbau.utils

import java.util.Properties

object VersionInfo {
    private val buildProperties = Properties()

    init {
        VersionInfo.javaClass.getResourceAsStream("/build/version.properties").use {
            buildProperties.load(it)
        }
    }

    fun getVersion(): String =
        buildProperties.getProperty("version", "UNKNOWN VERSION")
}