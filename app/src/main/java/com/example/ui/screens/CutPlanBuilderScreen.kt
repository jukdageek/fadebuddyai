package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.FadeType
import com.example.domain.model.OperatingMode
import com.example.ui.theme.CopperAccent
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel

@Composable
fun CutPlanBuilderScreen(
    viewModel: MainViewModel,
    onStartGuidedCut: () -> Unit
) {
    val clients by viewModel.clientProfiles.collectAsStateWithLifecycle()
    val currentMode by viewModel.currentMode.collectAsStateWithLifecycle()

    var selectedClientName by remember { mutableStateOf("Guest Client") }
    var selectedFadeType by remember { mutableStateOf(FadeType.MID_FADE) }
    var newClientNameInput by remember { mutableStateOf("") }
    var showAddClientField by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
            .padding(16.dp)
            .testTag("cut_plan_builder_lazy_column")
    ) {
        item {
            Text("FADE PLAN BUILDER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
            Text("Zone & Guard Sequence Engine", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Section 1: Client Selection
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .testTag("card_client_selection"),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("1. SELECT CLIENT PROFILE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                        Surface(
                            onClick = { showAddClientField = !showAddClientField },
                            shape = RoundedCornerShape(8.dp),
                            color = SurfaceVariantDark
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = CopperAccent, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Client", fontSize = 11.sp, color = CopperAccent, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (showAddClientField) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newClientNameInput,
                            onValueChange = { newClientNameInput = it },
                            label = { Text("Client Name") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = TextSecondary,
                                focusedLabelColor = CyanAccent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_new_client_name")
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = {
                                if (newClientNameInput.isNotBlank()) {
                                    viewModel.saveClientProfile(
                                        name = newClientNameInput,
                                        texture = "Wavy",
                                        density = "Medium",
                                        fadeType = selectedFadeType.title,
                                        notes = "Added from plan builder"
                                    )
                                    selectedClientName = newClientNameInput
                                    newClientNameInput = ""
                                    showAddClientField = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = DeepNavy),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Save Client", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val allNames = listOf("Guest Client") + clients.map { it.name }
                        allNames.take(4).forEach { name ->
                            val isSelected = selectedClientName == name
                            Surface(
                                onClick = { selectedClientName = name },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) CyanAccent else SurfaceVariantDark,
                                modifier = Modifier.testTag("client_chip_$name")
                            ) {
                                Text(
                                    text = name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) DeepNavy else TextPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Fade Type Selection
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .testTag("card_fade_type_selection"),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("2. CHOOSE FADE TECHNIQUE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                    Spacer(modifier = Modifier.height(10.dp))

                    FadeType.values().forEach { fade ->
                        val isSelected = selectedFadeType == fade
                        Surface(
                            onClick = { selectedFadeType = fade },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) SurfaceVariantDark else Color.Transparent,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, CyanAccent) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .testTag("fade_option_${fade.name}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(fade.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text(fade.description, fontSize = 11.sp, color = TextSecondary)
                                }
                                if (isSelected) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = CyanAccent)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Guard & Lever Timeline Sequence Preview
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .testTag("card_guard_sequence_preview"),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("3. GENERATED GUARD & LEVER TIMELINE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CopperAccent)
                    Spacer(modifier = Modifier.height(8.dp))

                    val previewZones = when (selectedFadeType) {
                        FadeType.LOW_FADE -> listOf(
                            "Zone 1 (Nape Base): #0 Closed (0%)",
                            "Zone 2 (Low Blend): #0.5 Half Open (50%)",
                            "Zone 3 (Mid Ridge): #1 Open (100%)",
                            "Zone 4 (Crown): #1.5 Closed (0%)"
                        )
                        FadeType.MID_FADE -> listOf(
                            "Zone 1 (Mid Base): #0 Closed (0%)",
                            "Zone 2 (Mid Blend): #0.5 Half Open (50%)",
                            "Zone 3 (Ridge Blend): #1 Open (100%)",
                            "Zone 4 (Crown Curve): #2 Half Open (50%)"
                        )
                        FadeType.HIGH_FADE -> listOf(
                            "Zone 1 (High Base): #0 Closed (0%)",
                            "Zone 2 (High Transition): #1 Closed (0%)",
                            "Zone 3 (Top Connection): #2 Open (100%)"
                        )
                        else -> listOf(
                            "Zone 1 (Base Line): #0 Closed (0%)",
                            "Zone 2 (Softening): #0.5 Half Open (50%)",
                            "Zone 3 (Crown Transition): #1.5 Open (100%)"
                        )
                    }

                    previewZones.forEachIndexed { idx, zoneText ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SurfaceVariantDark
                            ) {
                                Text(
                                    text = "Step ${idx + 1}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanAccent,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(zoneText, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // CTA: Start Guided Cut
        item {
            Button(
                onClick = {
                    viewModel.startCutSession(
                        clientName = selectedClientName,
                        fadeType = selectedFadeType,
                        mode = currentMode
                    )
                    onStartGuidedCut()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = DeepNavy),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_launch_guided_session")
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("LAUNCH LIVE GUIDED CUT", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
