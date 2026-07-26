package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ble.ClipperBleSimulator
import com.example.data.db.ClientProfileEntity
import com.example.data.db.CutSessionEntity
import com.example.data.db.FadeBuddyDatabase
import com.example.data.repository.FadeBuddyRepository
import com.example.domain.model.ClipperTelemetry
import com.example.domain.model.CueType
import com.example.domain.model.FadeType
import com.example.domain.model.FadeZoneStep
import com.example.domain.model.OperatingMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ActiveCutState(
    val isActive: Boolean = false,
    val isPaused: Boolean = false,
    val clientName: String = "Guest Client",
    val fadeType: FadeType = FadeType.MID_FADE,
    val mode: OperatingMode = OperatingMode.COACH,
    val zones: List<FadeZoneStep> = emptyList(),
    val currentZoneIndex: Int = 0,
    val elapsedSeconds: Long = 0,
    val consistencyScore: Int = 94,
    val aiGuidanceTip: String = "Maintain 15° pitch angle. C-motion stroke recommended for transition zone."
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FadeBuddyDatabase.getDatabase(application)
    private val repository = FadeBuddyRepository(db.fadeBuddyDao())

    val bleSimulator = ClipperBleSimulator()
    val telemetry: StateFlow<ClipperTelemetry> = bleSimulator.telemetry

    val clientProfiles: StateFlow<List<ClientProfileEntity>> = repository.clients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cutHistory: StateFlow<List<CutSessionEntity>> = repository.sessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeCut = MutableStateFlow(ActiveCutState())
    val activeCut: StateFlow<ActiveCutState> = _activeCut.asStateFlow()

    private val _currentMode = MutableStateFlow(OperatingMode.COACH)
    val currentMode: StateFlow<OperatingMode> = _currentMode.asStateFlow()

    private val _aiRecommendation = MutableStateFlow<String?>(null)
    val aiRecommendation: StateFlow<String?> = _aiRecommendation.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private var timerJob: Job? = null

    init {
        // Pre-populate standard sample client if DB is empty
        viewModelScope.launch {
            repository.clients.collect { list ->
                if (list.isEmpty()) {
                    repository.saveClient(
                        ClientProfileEntity(
                            name = "Marcus Vance",
                            hairTexture = "Coily (4B)",
                            hairDensity = "Dense",
                            preferredFadeType = "Low Skin Fade",
                            notes = "Sensitive scalp near parietal ridge; prefers #0.5 guard transition."
                        )
                    )
                    repository.saveClient(
                        ClientProfileEntity(
                            name = "Leo Sterling",
                            hairTexture = "Wavy (2B)",
                            hairDensity = "Medium",
                            preferredFadeType = "Mid Drop Fade",
                            notes = "Likes clean drop curve around back of ears."
                        )
                    )
                }
            }
        }
    }

    fun setOperatingMode(mode: OperatingMode) {
        _currentMode.value = mode
        if (mode == OperatingMode.PRACTICE) {
            bleSimulator.triggerHapticAndLight(
                cue = CueType.VIOLET_PAIRING,
                pattern = "Practice Mode Active (Motor Disabled - Sensors Active)"
            )
        } else if (mode == OperatingMode.PRO) {
            bleSimulator.triggerHapticAndLight(
                cue = CueType.CYAN_READY,
                pattern = "Pro Mode (Status Light Only)"
            )
        }
    }

    fun startCutSession(
        clientName: String,
        fadeType: FadeType,
        mode: OperatingMode
    ) {
        val zones = generateDefaultZones(fadeType)
        _activeCut.value = ActiveCutState(
            isActive = true,
            isPaused = false,
            clientName = clientName,
            fadeType = fadeType,
            mode = mode,
            zones = zones,
            currentZoneIndex = 0,
            elapsedSeconds = 0,
            consistencyScore = 96,
            aiGuidanceTip = "Zone 1 Target: Guard ${zones.firstOrNull()?.targetGuard ?: "#0"} with Lever ${zones.firstOrNull()?.targetLeverPosition ?: "Closed"}."
        )

        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _activeCut.update { state ->
                    if (state.isActive && !state.isPaused) {
                        state.copy(elapsedSeconds = state.elapsedSeconds + 1)
                    } else {
                        state
                    }
                }
            }
        }
    }

    fun pauseCutSession() {
        _activeCut.update { it.copy(isPaused = !it.isPaused) }
    }

    fun nextZoneStep() {
        _activeCut.update { state ->
            val nextIdx = state.currentZoneIndex + 1
            if (nextIdx < state.zones.size) {
                val updatedZones = state.zones.mapIndexed { idx, step ->
                    if (idx == state.currentZoneIndex) step.copy(completed = true) else step
                }
                val nextStep = updatedZones[nextIdx]
                // Send BLE cue
                bleSimulator.setGuard(nextStep.targetGuard)
                bleSimulator.triggerHapticAndLight(
                    cue = CueType.CYAN_READY,
                    pattern = "2 Short (Switch to Guard ${nextStep.targetGuard} - Lever ${nextStep.targetLeverPosition})"
                )

                state.copy(
                    zones = updatedZones,
                    currentZoneIndex = nextIdx,
                    aiGuidanceTip = "Switch to Guard ${nextStep.targetGuard}, Lever ${nextStep.targetLeverPosition}. ${nextStep.instruction}"
                )
            } else {
                // Last zone completed
                state
            }
        }
    }

    fun previousZoneStep() {
        _activeCut.update { state ->
            if (state.currentZoneIndex > 0) {
                val prevIdx = state.currentZoneIndex - 1
                val step = state.zones[prevIdx]
                state.copy(
                    currentZoneIndex = prevIdx,
                    aiGuidanceTip = "Revisiting Zone ${prevIdx + 1}: Guard ${step.targetGuard}, Lever ${step.targetLeverPosition}."
                )
            } else state
        }
    }

    fun finishAndSaveCutSession() {
        val current = _activeCut.value
        if (!current.isActive) return

        viewModelScope.launch {
            val guardsUsed = current.zones.joinToString(" -> ") { "${it.targetGuard} (${it.targetLeverPosition})" }
            repository.saveSession(
                CutSessionEntity(
                    clientName = current.clientName,
                    fadeType = current.fadeType.title,
                    operatingMode = current.mode.displayName,
                    durationSeconds = current.elapsedSeconds,
                    consistencyScore = current.consistencyScore,
                    guardsUsedSummary = guardsUsed,
                    notes = "Session recorded via ${current.mode.displayName}. Repeatable profile saved."
                )
            )

            _activeCut.value = ActiveCutState(isActive = false)
            timerJob?.cancel()
        }
    }

    fun saveClientProfile(name: String, texture: String, density: String, fadeType: String, notes: String) {
        viewModelScope.launch {
            repository.saveClient(
                ClientProfileEntity(
                    name = name,
                    hairTexture = texture,
                    hairDensity = density,
                    preferredFadeType = fadeType,
                    notes = notes
                )
            )
        }
    }

    fun deleteClientProfile(id: Long) {
        viewModelScope.launch {
            repository.deleteClient(id)
        }
    }

    fun deleteSession(id: Long) {
        viewModelScope.launch {
            repository.deleteSession(id)
        }
    }

    fun analyzeHairAndRecommendFade(hairTexture: String, density: String, desiredStyle: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            delay(1200) // Realistic AI inference pulse
            _aiRecommendation.value = "FadeBuddy AI Strategy for $hairTexture ($density density) - $desiredStyle:\n\n" +
                    "1. Zone 1 Base: Guard #0 Closed (0%) at neck & ears. Establish clear guideline.\n" +
                    "2. Zone 2 Transition: Guard #0.5 Lever Half Open (50%) with outward C-stroke flick to erase weight line.\n" +
                    "3. Zone 3 Parietal Blend: Guard #1 Open (100%) vertical parallel passes into crown bulk.\n" +
                    "4. Zone 4 Crown Connection: Guard #1.5 or #2 over comb for seamless crown transition."
            _isAiLoading.value = false
        }
    }

    private fun generateDefaultZones(fadeType: FadeType): List<FadeZoneStep> {
        return when (fadeType) {
            FadeType.LOW_FADE -> listOf(
                FadeZoneStep("z1", "Zone 1: Lower Nape Baseline", "#0 (Skin)", "Closed (0%)", "Establish clean baseline 1/2 inch above nape", "Short upward flick"),
                FadeZoneStep("z2", "Zone 2: Low Soft Blend Line", "#0.5 Guard", "Half Open (50%)", "Erasing the baseline with gentle C-motion strokes", "Outward C-motion"),
                FadeZoneStep("z3", "Zone 3: Mid parietal Transition", "#1 Guard", "Fully Open (100%)", "Blend into bulk without moving guideline higher", "Smooth vertical pass"),
                FadeZoneStep("z4", "Zone 4: Top Connection", "#1.5 Guard", "Closed (0%)", "Connect lower fade into crown volume", "Over-comb blend")
            )
            FadeType.MID_FADE -> listOf(
                FadeZoneStep("z1", "Zone 1: Mid Baseline", "#0 (Skin)", "Closed (0%)", "Set guideline at temple & upper nape level", "Upward stroke"),
                FadeZoneStep("z2", "Zone 2: Primary Blend Zone", "#0.5 Guard", "Half Open (50%)", "Flick upward 1/2 inch above guideline", "Flick outward"),
                FadeZoneStep("z3", "Zone 3: Parietal Ridge Softening", "#1 Guard", "Open (100%)", "Smooth out dark contrast transition", "Vertical parallel pass"),
                FadeZoneStep("z4", "Zone 4: Crown Weight Eraser", "#2 Guard", "Half Open (50%)", "Flick out at crown curve to preserve top length", "Combing flick")
            )
            FadeType.HIGH_FADE -> listOf(
                FadeZoneStep("z1", "Zone 1: High Contrast Base", "#0 (Skin)", "Closed (0%)", "Clear sides high up to parietal ridge", "High vertical pass"),
                FadeZoneStep("z2", "Zone 2: High Transition Line", "#1 Guard", "Closed (0%)", "Flick out right at parietal edge", "C-stroke flick"),
                FadeZoneStep("z3", "Zone 3: Top Blending", "#2 Guard", "Fully Open (100%)", "Blend directly into top length", "Flat comb pass")
            )
            else -> listOf(
                FadeZoneStep("z1", "Zone 1: Lower Perimeter", "#0 (Skin)", "Closed (0%)", "Establish initial baseline", "Upward flick"),
                FadeZoneStep("z2", "Zone 2: Blend & Soften", "#0.5 Guard", "Half Open (50%)", "Feather out guideline", "C-motion"),
                FadeZoneStep("z3", "Zone 3: Upper Transition", "#1.5 Guard", "Fully Open (100%)", "Connect to crown bulk", "Parallel pass")
            )
        }
    }
}
