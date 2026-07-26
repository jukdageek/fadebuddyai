package com.example.data.ble

import com.example.domain.model.ClipperTelemetry
import com.example.domain.model.CueType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class ClipperBleSimulator {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var telemetryJob: Job? = null

    private val _telemetry = MutableStateFlow(ClipperTelemetry())
    val telemetry: StateFlow<ClipperTelemetry> = _telemetry.asStateFlow()

    fun connect() {
        _telemetry.update { 
            it.copy(
                isConnected = true,
                currentCue = CueType.CYAN_READY,
                lastHapticPattern = "Connected & Calibrated"
            ) 
        }
        startTelemetryLoop()
    }

    fun disconnect() {
        telemetryJob?.cancel()
        _telemetry.update { 
            it.copy(
                isConnected = false,
                currentCue = CueType.VIOLET_PAIRING,
                lastHapticPattern = "Disconnected"
            ) 
        }
    }

    private fun startTelemetryLoop() {
        telemetryJob?.cancel()
        telemetryJob = scope.launch {
            while (true) {
                delay(100) // 10Hz telemetry update loop
                _telemetry.update { current ->
                    if (!current.isConnected) return@update current

                    // Slight natural IMU jitter & motor load variations
                    val pitchJitter = (Random.nextFloat() - 0.5f) * 1.5f
                    val rollJitter = (Random.nextFloat() - 0.5f) * 1.2f
                    val loadJitter = Random.nextInt(-3, 4)

                    val newPitch = (current.pitchAngle + pitchJitter).coerceIn(-45f, 45f)
                    val newRoll = (current.rollAngle + rollJitter).coerceIn(-30f, 30f)
                    val newLoad = (current.motorLoadPercent + loadJitter).coerceIn(10, 95)

                    // Auto temperature calculation based on motor run
                    val newTemp = if (newLoad > 70) {
                        (current.temperatureC + 0.05f).coerceAtMost(52f)
                    } else {
                        (current.temperatureC - 0.02f).coerceAtLeast(31f)
                    }

                    val isOverheated = newTemp > 45f
                    val cue = when {
                        isOverheated -> CueType.RED_FAULT
                        newLoad > 80 -> CueType.AMBER_ATTENTION
                        else -> current.currentCue
                    }

                    current.copy(
                        pitchAngle = newPitch,
                        rollAngle = newRoll,
                        motorLoadPercent = newLoad,
                        temperatureC = newTemp,
                        isOverheated = isOverheated,
                        currentCue = cue
                    )
                }
            }
        }
    }

    fun setGuard(guardId: String) {
        _telemetry.update {
            it.copy(
                activeGuardId = guardId,
                lastHapticPattern = "2 Short (Guard Detected: $guardId)",
                currentCue = CueType.CYAN_READY
            )
        }
    }

    fun setLeverPosition(percent: Int) {
        val clamped = percent.coerceIn(0, 100)
        _telemetry.update {
            it.copy(
                leverPositionPercent = clamped,
                lastHapticPattern = "1 Short (Lever: $clamped%)"
            )
        }
    }

    fun setAngles(pitch: Float, roll: Float) {
        _telemetry.update {
            val angleExtreme = kotlin.math.abs(pitch) > 30f || kotlin.math.abs(roll) > 20f
            val cue = if (angleExtreme) CueType.AMBER_ATTENTION else CueType.CYAN_READY
            val haptic = if (angleExtreme) "1 Long (Adjust Pitch/Roll Angle)" else it.lastHapticPattern
            it.copy(
                pitchAngle = pitch,
                rollAngle = roll,
                currentCue = cue,
                lastHapticPattern = haptic
            )
        }
    }

    fun triggerStallSimulation() {
        _telemetry.update {
            it.copy(
                motorLoadPercent = 98,
                motorRpm = 0,
                currentCue = CueType.RED_FAULT,
                lastHapticPattern = "Long Continuous Pulse (BLDC Stall Protected)"
            )
        }
    }

    fun resetStall() {
        _telemetry.update {
            it.copy(
                motorLoadPercent = 25,
                motorRpm = it.targetRpm,
                currentCue = CueType.CYAN_READY,
                lastHapticPattern = "1 Short (Motor Restored)"
            )
        }
    }

    fun triggerHapticAndLight(cue: CueType, pattern: String) {
        _telemetry.update {
            it.copy(
                currentCue = cue,
                lastHapticPattern = pattern
            )
        }
    }
}
