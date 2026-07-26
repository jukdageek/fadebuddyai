package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LiveTelemetryViewModel : ViewModel() {
    private val _telemetryOverlayActive = MutableStateFlow(true)
    val telemetryOverlayActive: StateFlow<Boolean> = _telemetryOverlayActive.asStateFlow()

    fun toggleOverlay() {
        _telemetryOverlayActive.value = !_telemetryOverlayActive.value
    }
}
