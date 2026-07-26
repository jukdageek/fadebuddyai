package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.domain.model.OperatingMode
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

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onStartPlanClick: () -> Unit,
    onStartLiveCutClick: () -> Unit,
    onOpenDiagnosticsClick: () -> Unit,
    onOpenAiAdvisorClick: () -> Unit
) {
    val telemetry by viewModel.telemetry.collectAsStateWithLifecycle()
    val activeCut by viewModel.activeCut.collectAsStateWithLifecycle()
    val currentMode by viewModel.currentMode.collectAsStateWithLifecycle()
    val cutHistory by viewModel.cutHistory.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
            .testTag("home_screen_lazy_column")
    ) {
        // Top Status Telemetry Header
        item {
            ClipperTopStatusHeader(
                telemetry = telemetry,
                onConnectClick = {
                    if (telemetry.isConnected) {
                        viewModel.bleSimulator.disconnect()
                    } else {
                        viewModel.bleSimulator.connect()
                    }
                }
            )
        }

        // Hero Blueprint Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("hero_blueprint_card"),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_clipper_blueprint_1785052780057),
                        contentDescription = "Clipper Blueprint",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        DeepNavy.copy(alpha = 0.85f),
                                        DeepNavy
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "FadeBuddyAI Gen 1",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Smart Sensing Clipper • Non-blocking Coaching • Zero Autonomy Control",
                            fontSize = 11.sp,
                            color = CyanAccent,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Operating Mode Selector
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "OPERATING MODE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OperatingMode.values().forEach { mode ->
                        val isSelected = currentMode == mode
                        Surface(
                            onClick = { viewModel.setOperatingMode(mode) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) CyanAccent else SurfaceVariantDark,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("mode_chip_${mode.name}")
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = mode.displayName.replace(" Mode", ""),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) DeepNavy else TextPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = currentMode.description,
                    fontSize = 11.sp,
                    color = CopperAccent,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        // Quick Action Buttons
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Primary Action: Start Live Guided Cut
                Button(
                    onClick = onStartLiveCutClick,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = DeepNavy),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("btn_start_live_cut")
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Live Coach Cut", fontWeight = FontWeight.ExtraBold)
                }

                // Secondary Action: Build Plan
                OutlinedButton(
                    onClick = onStartPlanClick,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CopperAccent),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CopperAccent),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("btn_build_cut_plan")
                ) {
                    Icon(imageVector = Icons.Default.ContentCut, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Build Plan", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Feature Grid Cards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // AI Hair Strategy Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenAiAdvisorClick() }
                        .testTag("card_ai_advisor"),
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = CyanAccent)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("AI Hair Strategy", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Gemini texture analysis & guard recipes", fontSize = 11.sp, color = TextSecondary)
                    }
                }

                // Clipper Health Diagnostics Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenDiagnosticsClick() }
                        .testTag("card_diagnostics"),
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(imageVector = Icons.Default.SettingsSuggest, contentDescription = null, tint = CopperAccent)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Diagnostics", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Thermal, RPM load & GATT telemetry", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }
        }

        // Recent Saved Cut History
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECENT CUT HISTORY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.History, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${cutHistory.size} Sessions", fontSize = 11.sp, color = CyanAccent, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        if (cutHistory.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.ContentCut, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No Cut Sessions Recorded Yet", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text("Complete a guided fade session to record repeatable client recipes.", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }
        } else {
            items(cutHistory.take(5)) { session ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .testTag("session_item_${session.id}"),
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(session.clientName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("${session.fadeType} • ${session.operatingMode}", fontSize = 11.sp, color = CyanAccent)
                            Text("Guards: ${session.guardsUsedSummary}", fontSize = 10.sp, color = TextSecondary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SurfaceVariantDark
                            ) {
                                Text(
                                    text = "${session.consistencyScore}% Score",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CopperAccent,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text("${session.durationSeconds / 60}m ${session.durationSeconds % 60}s", fontSize = 10.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
