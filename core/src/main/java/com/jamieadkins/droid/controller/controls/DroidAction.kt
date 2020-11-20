package com.jamieadkins.droid.controller.controls

/**
 * command is hex representation
 */
sealed class DroidAction(val commands: List<String>) {

    constructor(command: String) : this(listOf(command))

    constructor(vararg commands: String) : this(commands.toList())

    object Handshake : DroidAction("22 20 01")

    object ResetSound : DroidAction("27 42 0f 44 44 00 1f 00")

    object BlasterSound : DroidAction("27 42 0f 44 44 00 1f 0a", "27 42 0f 44 44 00 18 00")

    object PlaySound : DroidAction("27 42 0f 44 44 00 18 00")

    object Identify : DroidAction("27 42 0f 44 44 00 1f 00", "27 42 0f 44 44 00 18 02")

    /**
     * execute pre-programmed script X where X is specified by second-to-last byte
     * valid values 1-19; 19 leaves motors on so beware
     * values 1-8 are for R-unit droids , 9-16 are for BB droids
     * 17-18 are delays followed by pairing noise
     * 25 00 0c 42 01 02
     */
    data class Reaction(val number: Int) : DroidAction("25 00 0c 42 %s 02".format(number.to2DigitHexString()))

    /**
     * set volume, last byte controls value of volume, 00 - 0x1f
     */
    data class Volume(val volume: Int) : DroidAction("27 42 0f 44 44 00 0e %s".format(volume.to2DigitHexString()))

    object MinVolume : DroidAction("27 42 0f 44 44 00 0e 00")

    object MaxVolume : DroidAction("27 42 0f 44 44 00 0e 1f")

    data class Forward(val power: Int) : DroidAction(listOf("2942 0546 01${power.to2DigitHexString()} 012C 0000", "2942 0546 00${power.to2DigitHexString()} 012C 0000"))

    data class Backwards(val power: Int) : DroidAction(listOf("2942 0546 81${power.to2DigitHexString()} 012C 0000", "2942 0546 80${power.to2DigitHexString()} 012C 0000"))

    data class Left(val power: Int) : DroidAction(listOf("2942 0546 0$RIGHT_MOTOR${power.to2DigitHexString()} 012C 0000"))

    data class Right(val power: Int) : DroidAction(listOf("2942 0546 0$LEFT_MOTOR${power.to2DigitHexString()} 012C 0000"))

    data class HeadLeft(val power: Int) : DroidAction("2942 0546 0$HEAD_MOTOR${power.to2DigitHexString()} 012C 0000")

    data class HeadRight(val power: Int) : DroidAction("2942 0546 8$HEAD_MOTOR${power.to2DigitHexString()} 012C 0000")

    data class MoveWithJoystick(val joystickOutput: JoystickOutput) : DroidAction(
        listOf(
            "2942 0546 ${joystickOutput.leftMotorDirection}$LEFT_MOTOR${joystickOutput.leftMotorPower.to2DigitHexString()} 012C 0000",
            "2942 0546 ${joystickOutput.rightMotorDirection}$RIGHT_MOTOR${joystickOutput.rightMotorPower.to2DigitHexString()} 012C 0000"
        )
    )

    companion object {
        private const val LEFT_MOTOR = 0
        private const val RIGHT_MOTOR = 1
        private const val HEAD_MOTOR = 2
    }

}