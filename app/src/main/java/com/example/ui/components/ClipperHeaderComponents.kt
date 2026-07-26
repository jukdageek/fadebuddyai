package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.ClipperTelemetry
import com.example.domain.model.CueType
import com.example.ui.theme.CopperAccent
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ClipperTopStatusHeader(
    telemetry: ClipperTelemetry,
    onConnectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cueColor = Color(telemetry.currentCue.hexColor)
    val animatedCueColor by animateColorAsState(targetValue = cueColor, animationSpec = tween(400), label = "cue")

    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val alphaPulse by pulseTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(1000), repeatMode = RepeatMode.Reverse),
        label = "alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("top_status_header"),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Row 1: Connection & Battery & Cue Light
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // BLE Connection Status Pill
                Surface(
                    onClick = onConnectClick,
                    shape = RoundedCornerShape(20.dp),
                    color = if (telemetry.isConnected) SurfaceVariantDark else Color(0xFF331B2A),
                    modifier = Modifier.testTag("ble_status_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (telemetry.isConnected) Icons.Default.BluetoothConnected else Icons.Default.BluetoothDisabled,
                            contentDescription = "BLE Connection",
                            tint = if (telemetry.isConnected) CyanAccent else Color(0xFFEF4444),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (telemetry.isConnected) "Clipper Paired" else "Tap to Pair BLE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }

                // RGB Cue Light Bar
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(animatedCueColor.copy(alpha = if (telemetry.isConnected) alphaPulse else 0.3f))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = telemetry.currentCue.title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = animatedCueColor
                    )
                }

                // Battery Indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BatteryChargingFull,
                        contentDescription = "Battery Level",
                        tint = if (telemetry.batteryPercent < 20) Color(0xFFEF4444) else CyanAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${telemetry.batteryPercent}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: Guard & Lever Position Telemetry
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Active Guard Telemetry Item
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ContentCut,
                        contentDescription = "Guard",
                        tint = CopperAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("DETECTED GUARD", fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        Text(telemetry.activeGuardId, fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.ExtraBold)
                    }
                }

                // Lever Position Telemetry Item
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Lever",
                        tint = CyanAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("LEVER SENSING", fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        Text(
                            text = when {
                                telemetry.leverPositionPercent < 15 -> "CLOSED (0%)"
                                telemetry.leverPositionPercent > 85 -> "OPEN (100%)"
                                else -> "HALF (${telemetry.leverPositionPercent}%)"
                            },
                            fontSize = 13.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // Motor RPM Telemetry Item
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Motor RPM",
                        tint = Color(0xFFA855F7),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("MOTOR BLDC", fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        Text("${telemetry.motorRpm} RPM", fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}
