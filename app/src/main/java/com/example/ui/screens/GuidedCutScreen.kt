package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.CueType
import com.example.domain.model.FadeZoneStep
import com.example.ui.components.ClipperTopStatusHeader
import com.example.ui.theme.CardBorderNavy
import com.example.ui.theme.CopperAccent
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GuidedCutScreen(viewModel: MainViewModel) {
    val telemetry by viewModel.telemetry.collectAsStateWithLifecycle()
    val activeCut by viewModel.activeCut.collectAsStateWithLifecycle()

    val currentZone: FadeZoneStep? = activeCut.zones.getOrNull(activeCut.currentZoneIndex)
    val cueColor = Color(telemetry.currentCue.hexColor)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
            .testTag("guided_cut_lazy_column")
    ) {
        // 1. Always Visible Top Status Header
        item {
            ClipperTopStatusHeader(
                telemetry = telemetry,
                onConnectClick = {
                    if (telemetry.isConnected) viewModel.bleSimulator.disconnect()
                    else viewModel.bleSimulator.connect()
                }
            )
        }

        // Active Session Status Bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("session_timer_bar"),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(activeCut.clientName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("${activeCut.fadeType.title} • ${activeCut.mode.displayName}", fontSize = 11.sp, color = CyanAccent)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SurfaceVariantDark
                        ) {
                            Text(
                                text = "${activeCut.elapsedSeconds / 60}:${String.format("%02d", activeCut.elapsedSeconds % 60)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            onClick = { viewModel.pauseCutSession() },
                            shape = CircleShape,
                            color = SurfaceVariantDark
                        ) {
                            Icon(
                                imageVector = if (activeCut.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = "Pause / Resume",
                                tint = CyanAccent,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Active Zone Guidance Focus Card
        item {
            if (currentZone != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("active_zone_card"),
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, cueColor),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = cueColor.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "ACTIVE ZONE ${activeCut.currentZoneIndex + 1} OF ${activeCut.zones.size}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = cueColor,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            Text("Technique Score: ${activeCut.consistencyScore}%", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(currentZone.zoneName, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))

                        // Target Guard vs Lever Banner
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceVariantDark)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("TARGET GUARD", fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                                Text(currentZone.targetGuard, fontSize = 16.sp, color = CopperAccent, fontWeight = FontWeight.ExtraBold)
                            }

                            Box(modifier = Modifier.size(1.dp, 28.dp).background(TextSecondary.copy(alpha = 0.3f)))

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("TARGET LEVER", fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                                Text(currentZone.targetLeverPosition, fontSize = 16.sp, color = CyanAccent, fontWeight = FontWeight.ExtraBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = currentZone.instruction,
                            fontSize = 13.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Recommended stroke: ${currentZone.strokeDirectionText}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Next / Prev Zone Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.previousZoneStep() },
                                enabled = activeCut.currentZoneIndex > 0,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_prev_zone")
                            ) {
                                Icon(imageVector = Icons.Default.FastRewind, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Previous")
                            }

                            Button(
                                onClick = { viewModel.nextZoneStep() },
                                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = DeepNavy),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1.5f)
                                    .testTag("btn_next_zone")
                            ) {
                                Text("Confirm & Next Zone", fontWeight = FontWeight.ExtraBold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(imageVector = Icons.Default.FastForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            } else {
                // Cut Completed Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .testTag("cut_complete_card"),
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Guided Fade Cut Completed!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Session saved to repeatable client history.", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.finishAndSaveCutSession() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = DeepNavy),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_save_session_history")
                        ) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SAVE TO CLIENT HISTORY", fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }

        // 3. Live 6-Axis IMU Technique Radar & Calm Coaching Gauge
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("card_technique_imu_radar"),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("LIVE STROKE & TECHNIQUE RADAR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Pitch & Roll Visual Radar Canvas
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(SurfaceVariantDark)
                                .border(1.dp, CardBorderNavy, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val center = Offset(size.width / 2, size.height / 2)
                                val radius = size.width / 2 - 8

                                drawCircle(
                                    color = Color.Gray.copy(alpha = 0.3f),
                                    radius = radius,
                                    style = Stroke(width = 1.dp.toPx())
                                )
                                drawCircle(
                                    color = Color.Gray.copy(alpha = 0.2f),
                                    radius = radius * 0.5f,
                                    style = Stroke(width = 1.dp.toPx())
                                )

                                // Current angle pointer offset
                                val targetX = center.x + (telemetry.rollAngle / 45f) * (radius * 0.8f)
                                val targetY = center.y - (telemetry.pitchAngle / 45f) * (radius * 0.8f)

                                drawCircle(
                                    color = cueColor,
                                    radius = 7.dp.toPx(),
                                    center = Offset(targetX, targetY)
                                )
                            }
                        }

                        // Angle Telemetry Readouts
                        Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Clipper Pitch:", fontSize = 12.sp, color = TextSecondary)
                                Text("${String.format("%.1f", telemetry.pitchAngle)}°", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Clipper Roll:", fontSize = 12.sp, color = TextSecondary)
                                Text("${String.format("%.1f", telemetry.rollAngle)}°", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Motor RPM Load:", fontSize = 12.sp, color = TextSecondary)
                                Text("${telemetry.motorLoadPercent}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Sensor Confidence:", fontSize = 12.sp, color = TextSecondary)
                                Text("${(telemetry.sensorConfidence * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CopperAccent)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Calm Coaching Advice Box
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceVariantDark,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(activeCut.aiGuidanceTip, fontSize = 11.sp, color = TextPrimary)
                        }
                    }
                }
            }
        }

        // 4. Interactive Clipper Telemetry Simulator Panel
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("card_clipper_simulator_panel"),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("CLIPPER HARDWARE SIMULATOR (BENCH RIG)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CopperAccent, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Simulate Guard Change Buttons
                    Text("Simulate Attach Guard:", fontSize = 11.sp, color = TextSecondary)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("#0 (Skin)", "#0.5", "#1", "#1.5", "#2").forEach { guardId ->
                            Surface(
                                onClick = { viewModel.bleSimulator.setGuard(guardId) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (telemetry.activeGuardId == guardId) CopperAccent else SurfaceVariantDark,
                                modifier = Modifier.testTag("sim_guard_$guardId")
                            ) {
                                Text(
                                    text = guardId,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (telemetry.activeGuardId == guardId) DeepNavy else TextPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Simulate Taper Lever Position Slider
                    Text("Simulate Taper Lever Sensing (${telemetry.leverPositionPercent}%):", fontSize = 11.sp, color = TextSecondary)
                    Slider(
                        value = telemetry.leverPositionPercent.toFloat(),
                        onValueChange = { viewModel.bleSimulator.setLeverPosition(it.toInt()) },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = CyanAccent,
                            activeTrackColor = CyanAccent
                        ),
                        modifier = Modifier.testTag("sim_lever_slider")
                    )

                    // Simulate Pitch / Tilt Slider
                    Text("Simulate Stroke Pitch Angle (${telemetry.pitchAngle.toInt()}°):", fontSize = 11.sp, color = TextSecondary)
                    Slider(
                        value = telemetry.pitchAngle,
                        onValueChange = { viewModel.bleSimulator.setAngles(it, telemetry.rollAngle) },
                        valueRange = -45f..45f,
                        colors = SliderDefaults.colors(
                            thumbColor = CopperAccent,
                            activeTrackColor = CopperAccent
                        ),
                        modifier = Modifier.testTag("sim_pitch_slider")
                    )

                    // Haptic & Light Feedback Triggering
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.bleSimulator.triggerHapticAndLight(
                                    CueType.AMBER_ATTENTION,
                                    "1 Long Haptic Pulse (Angle Correction Needed)"
                                )
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_trigger_haptic_pulse")
                        ) {
                            Icon(imageVector = Icons.Default.Vibration, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test Haptic", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                if (telemetry.motorLoadPercent > 90) viewModel.bleSimulator.resetStall()
                                else viewModel.bleSimulator.triggerStallSimulation()
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_trigger_stall_test")
                        ) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Stall Test", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
