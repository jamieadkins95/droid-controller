package com.jamieadkins.droid.controller.controls.advanced

import javax.inject.Inject

class CommandInputParser @Inject constructor() {

    fun parseInput(input: String): List<DroidActionWithDelay> {
        return input.split("\n")
            .filter { line -> line.isNotBlank() && !line.startsWith("//") }
            .map { line ->
                val commandInput = line.split(",").firstOrNull() ?: ""
                val delayInput = line.split(",").getOrNull(1) ?: ""

                val command = commandInput.replace(" ", "").replace(":", "").trim()
                val delay = delayInput.trim().toLongOrNull() ?: 0
                DroidActionWithDelay(command, delay)
            }
    }
}