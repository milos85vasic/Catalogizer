package com.catalogizer.application

import com.catalogizer.core.EnvironmentVariables
import java.io.File

fun main() {

    val config = System.getenv(EnvironmentVariables.CATALOGIZER_CONFIGURATION_HOME.variable) ?: ""

    if (config.isEmpty()) {

        error("No configuration provided")
    }

    val configHomePath = File(config)

    if (configHomePath.exists() && configHomePath.isDirectory) {

        println("Configuration home path :: '${configHomePath.absolutePath}'")

        return
    }

    error("Configuration file not found: '$config'")
}
