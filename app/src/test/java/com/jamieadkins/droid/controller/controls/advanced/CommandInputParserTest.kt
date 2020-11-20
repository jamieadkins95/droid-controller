package com.jamieadkins.droid.controller.controls.advanced

import org.junit.Test
import kotlin.test.assertEquals

class CommandInputParserTest {

    val inputParser = CommandInputParser()

    @Test
    fun `One input line, one command`() {
        assertEquals(listOf(DroidActionWithDelay("1234", 100)), inputParser.parseInput("1234,100"))
    }

    @Test
    fun `No input`() {
        assertEquals(listOf(), inputParser.parseInput(""))
    }

    @Test
    fun `No delay, default to 0`() {
        assertEquals(listOf(DroidActionWithDelay("1234", 0)), inputParser.parseInput("1234"))
    }

    @Test
    fun `No delay with comma, default to 0`() {
        assertEquals(listOf(DroidActionWithDelay("1234", 0)), inputParser.parseInput("1234,"))
    }

    @Test
    fun `Space in input`() {
        assertEquals(listOf(DroidActionWithDelay("1234", 100)), inputParser.parseInput("1234, 100"))
    }

    @Test
    fun `Spaces in command`() {
        assertEquals(listOf(DroidActionWithDelay("1234", 100)), inputParser.parseInput("12 34, 100"))
    }

    @Test
    fun `Colons in command`() {
        assertEquals(listOf(DroidActionWithDelay("1234", 100)), inputParser.parseInput("12:34, 100"))
    }

    @Test
    fun `Multiple lines`() {
        assertEquals(
            listOf(
                DroidActionWithDelay("1234", 100),
                DroidActionWithDelay("4321", 200)
            ),
            inputParser.parseInput("1234, 100\n4321, 200")
        )
    }

    @Test
    fun `3 lines, one has delay missing`() {
        assertEquals(
            listOf(
                DroidActionWithDelay("1234", 100),
                DroidActionWithDelay("4321", 0),
                DroidActionWithDelay("5678", 200)
            ),
            inputParser.parseInput("1234, 100\n4321\n5678,200")
        )
    }

    @Test
    fun `3 lines, one is comment`() {
        assertEquals(
            listOf(
                DroidActionWithDelay("1234", 100),
                DroidActionWithDelay("5678", 200)
            ),
            inputParser.parseInput("1234, 100\n// do not read\n5678,200")
        )
    }

    @Test
    fun `Too many commas`() {
        assertEquals(
            listOf(DroidActionWithDelay("1234", 100)),
            inputParser.parseInput("1234, 100, 200")
        )
    }
}