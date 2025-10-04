package com.catalogizer.core

import kotlin.test.Test
import kotlin.test.assertEquals

internal class EnvironmentVariablesTest {

    @Test
    fun testEnvironmentVariableValues() {
        assertEquals("CATALOGIZER_CONFIGURATION_HOME", EnvironmentVariables.CATALOGIZER_CONFIGURATION_HOME.variable)
    }
}