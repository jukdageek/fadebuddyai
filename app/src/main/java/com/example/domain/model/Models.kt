package com.example.domain.model

enum class OperatingMode(val displayName: String, val description: String) {
    PRO("Pro Mode", "Fast barber workflow; minimal cues & status light only"),
    COACH("Coach Mode", "Guided learning; live haptic + color cues for angle/stroke/config"),
    PRACTICE("Practice Mode", "No-blade training with motor disabled for stroke consistency"),
    MAINTENANCE("Maintenance Mode", "Diagnostics, motor test, temperature & battery health")
}

enum class CueType(val hexColor: Long, val title: String) {
    CYAN_READY(0xFF00F2FE, "On-Plan / Optimal"),
    VIOLET_PAIRING(0xFFA855F7, "Pairing / Neutral Transition"),
    AMBER_ATTENTION(0xFFF59E0B, "Technique / Config Attention"),
    RED_FAULT(0xFFEF4444, "Hardware Protection / Safety Stop")
}

enum class FadeType(val title: String, val description: String) {
    LOW_FADE("Low Fade", "Gradual blend starting just above ears & neckline"),
    MID_FADE("Mid Fade", "Balanced blend starting at temple level"),
    HIGH_FADE("High Fade", "High contrast blend starting near parietal ridge"),
    SKIN_FADE("Skin / Bald Fade", "Seamless drop to zero skin at base"),
    TAPER_FADE("Taper Fade", "Targeted fade at sideburns & neckline only"),
    DROP_FADE("Drop Fade", "Arcing blend curve that dips behind the ear")
}

data class ClipperTelemetry(
    val isConnected: Boolean = false,
    val deviceName: String = "FadeBuddy Clipper Gen1",
    val batteryPercent: Int = 88,
    val motorRpm: Int = 7200,
    val targetRpm: Int = 7200,
    val motorLoadPercent: Int = 24,
    val temperatureC: Float = 34.5f,
    val isOverheated: Boolean = false,
    val activeGuardId: String = "#1 (3mm)",
    val leverPositionPercent: Int = 0, // 0 = closed, 100 = open
    val pitchAngle: Float = 12f, // Clipper tilt pitch
    val rollAngle: Float = 4f,   // Clipper roll
    val strokeSpeedRpm: Int = 42,
    val sensorConfidence: Float = 0.94f, // 0.0 to 1.0
    val lastHapticPattern: String = "1 Short (Adjust)",
    val currentCue: CueType = CueType.CYAN_READY
)

data class FadeZoneStep(
    val id: String,
    val zoneName: String,
    val targetGuard: String,
    val targetLeverPosition: String, // "Closed", "Half Open", "Fully Open"
    val instruction: String,
    val strokeDirectionText: String,
    val completed: Boolean = false
)
