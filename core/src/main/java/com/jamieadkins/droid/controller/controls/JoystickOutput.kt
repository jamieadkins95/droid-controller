package com.jamieadkins.droid.controller.controls

import androidx.annotation.IntRange
import com.jamieadkins.droid.controller.controls.JoystickCalculator.MAX_POWER
import kotlin.math.abs

data class JoystickOutput(
    val leftMotorDirection: Int,
    val rightMotorDirection: Int,
    @IntRange(from = 0, to = MAX_POWER.toLong()) val leftMotorPower: Int,
    @IntRange(from = 0, to = MAX_POWER.toLong()) val rightMotorPower: Int
)

object JoystickCalculator {

    const val MAX_POWER = 255f
    const val POWER_RANGE = MAX_POWER + MAX_POWER

    fun calculate(angle: Int, strength: Int): JoystickOutput {
        val motorPower = strength.toFloat() / 100

        val leftMotor = when {
            angle <= 90 -> MAX_POWER
            angle <= 180 -> {
                val percentage = (180 - angle).toFloat() / 90
                -MAX_POWER + percentage * POWER_RANGE
            }
            angle <= 270 -> -MAX_POWER
            angle <= 360 -> {
                val percentage = (360 - angle).toFloat() / 90
                MAX_POWER - percentage * POWER_RANGE
            }
            else -> throw IllegalArgumentException("Angle > 360")
        }

        val rightMotor = when {
            angle <= 90 -> {
                val percentage = angle.toFloat() / 90
                -MAX_POWER + percentage * POWER_RANGE
            }
            angle <= 180 -> MAX_POWER
            angle <= 270 -> {
                val percentage = (270 - angle).toFloat() / 90
                -MAX_POWER + percentage * POWER_RANGE
            }
            angle <= 360 -> -MAX_POWER
            else -> throw IllegalArgumentException("Angle > 360")
        }

        val adjustedLeft = motorPower * leftMotor
        val adjustedRight = motorPower * rightMotor
        val leftDirection = if (adjustedLeft >= 0) 0 else 8
        val rightDirection = if (adjustedRight >= 0) 0 else 8

        return JoystickOutput(leftDirection, rightDirection, abs(adjustedLeft.toInt()), abs(adjustedRight.toInt()))
    }
}