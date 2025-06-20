package com.catalogizer.utils

import com.catalogizer.utils.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PrinterTest {

    @Test
    fun testMessage() {
        val message = "message"
        val testPrinter = Printer(message)
        assertEquals(testPrinter.message, message)
    }

    @Test
    fun testSerialization() {
        val message = "message"
        val json1 = Json.encodeToString(Printer(message))
        val json2 = Json.encodeToString(Printer(message))
        assertEquals(json1, json2)
    }
}