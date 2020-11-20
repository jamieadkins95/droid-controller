package com.jamieadkins.droid.controller.controls

import org.junit.Assert.*
import org.junit.Test

class JoystickCalculatorTest {


    @Test
    fun `Spin Left`() {
        val expected = JoystickOutput(8, 0, 255, 255)
        val result = JoystickCalculator.calculate(180, 100)
        assertEquals(expected, result)
    }

    @Test
    fun `Spin Right`() {
        val expected = JoystickOutput(0, 8, 255, 255)
        val result = JoystickCalculator.calculate(0, 100)
        assertEquals(expected, result)
    }

    @Test
    fun `Forward`() {
        val expected = JoystickOutput(0, 0, 255, 255)
        val result = JoystickCalculator.calculate(90, 100)
        assertEquals(expected, result)
    }

    @Test
    fun `Forward 50%`() {
        val expected = JoystickOutput(0, 0, 127, 127)
        val result = JoystickCalculator.calculate(90, 50)
        assertEquals(expected, result)
    }

    @Test
    fun `Backwards`() {
        val expected = JoystickOutput(8, 8, 255, 255)
        val result = JoystickCalculator.calculate(270, 100)
        assertEquals(expected, result)
    }

    @Test
    fun `Forward and Right`() {
        val expected = JoystickOutput(0, 0, 255, 0)
        val result = JoystickCalculator.calculate(45, 100)
        assertEquals(expected, result)
    }

    @Test
    fun `Forward and Left`() {
        val expected = JoystickOutput(0, 0, 127, 0)
        val result = JoystickCalculator.calculate(135, 100)
        assertEquals(expected, result)
    }
}