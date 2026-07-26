package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ClipperTopStatusHeader
import com.example.ui.theme.CopperAccent
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel

@Composable
fun DiagnosticsScreen(viewModel: MainViewModel) {
    val telemetry by viewModel.telemetry.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
            .testTag("diagnostics_lazy_column")
    ) {
        item {
            ClipperTopStatusHeader(
                telemetry = telemetry,
                onConnectClick = {
                    if (telemetry.isConnected) viewModel.bleSimulator.disconnect()
                    else viewModel.bleSimulator.connect()
                }
            )
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text("MAINTENANCE & HARDWARE TELEMETRY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
                Text("On-Device Health & GATT Console", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
            }
        }

        // Section 1: Motor & Thermal Gauges
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Thermal Gauge
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("card_thermal_diagnostics"),
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(imageVector = Icons.Default.Thermostat, contentDescription = null, tint = if (telemetry.isOverheated) Color(0xFFEF4444) else CyanAccent)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Blade & Grip Temp", fontSize = 11.sp, color = TextSecondary)
                        Text("${String.format("%.1f", telemetry.temperatureC)} °C", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                        Text(if (telemetry.temperatureC < 43f) "Normal Duty (<43°C Target)" else "Thermal Throttling Active", fontSize = 10.sp, color = if (telemetry.temperatureC < 43f) CyanAccent else Color(0xFFEF4444))
                    }
                }

                // BLDC Motor Gauge
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("card_motor_diagnostics"),
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(imageVector = Icons.Default.Speed, contentDescription = null, tint = CopperAccent)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("BLDC Motor Speed", fontSize = 11.sp, color = TextSecondary)
                        Text("${telemetry.motorRpm} RPM", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                        Text("Closed-loop regulation: OK", fontSize = 10.sp, color = CopperAccent)
                    }
                }
            }
        }

        // Section 2: Sensor Brow & Lever Calibration
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("card_sensor_health"),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SENSOR BROW & LEVER CALIBRATION", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Multizone ToF Proximity:", fontSize = 12.sp, color = TextSecondary)
                            Text("6-Axis IMU (BMI270):", fontSize = 12.sp, color = TextSecondary)
                            Text("Lever Hall Sensor:", fontSize = 12.sp, color = TextSecondary)
                            Text("Guard Hall Insert:", fontSize = 12.sp, color = TextSecondary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Calibrated (Hair scatter filtered)", fontSize = 12.sp, color = CyanAccent, fontWeight = FontWeight.Bold)
                            Text("Active (Vibration rejected)", fontSize = 12.sp, color = CyanAccent, fontWeight = FontWeight.Bold)
                            Text("${telemetry.leverPositionPercent}% Position", fontSize = 12.sp, color = CopperAccent, fontWeight = FontWeight.Bold)
                            Text(telemetry.activeGuardId, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.bleSimulator.triggerHapticAndLight(
                                cue = com.example.domain.model.CueType.CYAN_READY,
                                pattern = "90s Guided Calibration Routine Passed"
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark, contentColor = CyanAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_run_60s_calibration")
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("RUN 60s GUIDED SENSOR RE-CALIBRATION", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section 3: GATT Characteristics Debug Console
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("card_gatt_console"),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.DeveloperMode, contentDescription = null, tint = CopperAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("BLUETOOTH GATT CHARACTERISTICS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    val gattList = listOf(
                        "0x2A00 Device Status: CONNECTED_IDLE",
                        "0x2A01 Motion Telemetry: 20-50Hz Active (Pitch=${telemetry.pitchAngle.toInt()}°, Roll=${telemetry.rollAngle.toInt()}°)",
                        "0x2A02 Tool Config: Guard=${telemetry.activeGuardId}, Lever=${telemetry.leverPositionPercent}%",
                        "0x2A03 Motor Telemetry: RPM=${telemetry.motorRpm}, Load=${telemetry.motorLoadPercent}%",
                        "0x2A04 Guidance Command: Cue=${telemetry.currentCue.title}",
                        "0x2A05 Firmware DFU: Signed Boot v1.0.4 - Secured"
                    )

                    gattList.forEach { line ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DeepNavy,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Text(
                                text = line,
                                fontSize = 10.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = CyanAccent,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Factory Reset & Maintenance Reset CTA
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                OutlinedButton(
                    onClick = { viewModel.bleSimulator.resetStall() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("btn_factory_reset_clipper")
                ) {
                    Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("FACTORY RESET CLIPPER PAIRINGS & CALIBRATION", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
