package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CameraTelemetryPreviewSurface
import com.example.ui.theme.CopperAccent
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel

@Composable
fun HairAnalyzerScreen(
    viewModel: MainViewModel,
    onApplyAiRecipeToPlan: () -> Unit
) {
    val aiRecommendation by viewModel.aiRecommendation.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()

    var selectedTexture by remember { mutableStateOf("Coily (4A-4C)") }
    var selectedDensity by remember { mutableStateOf("Dense") }
    var desiredStyle by remember { mutableStateOf("Low Skin Fade with C-stroke transition") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
            .padding(16.dp)
            .testTag("hair_analyzer_lazy_column")
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = CyanAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI FADE STRATEGY ADVISOR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
            }
            Text("Gemini Hair Texture & Guard Sequence AI", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Live Camera Telemetry Surface with overlay HUD
        item {
            CameraTelemetryPreviewSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp)
                    .testTag("card_camera_scanner"),
                overlayContent = {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DeepNavy.copy(alpha = 0.85f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = androidx.compose.foundation.shape.CircleShape,
                                    color = CyanAccent,
                                    modifier = Modifier.size(8.dp)
                                ) {}
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "LIVE CAMERA FEED • DENSITY OPTICS ACTIVE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanAccent
                                )
                            }
                        }

                        Text(
                            text = "Point camera at client's crown or parietal ridge to assist AI texture detection",
                            fontSize = 11.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .background(DeepNavy.copy(alpha = 0.75f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            )
        }

        // Form Parameters
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .testTag("card_analyzer_form"),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("HAIR TEXTURE CLASSIFICATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Straight (1)", "Wavy (2)", "Curly (3)", "Coily (4A-4C)").forEach { texture ->
                            val selected = selectedTexture == texture
                            Surface(
                                onClick = { selectedTexture = texture },
                                shape = RoundedCornerShape(8.dp),
                                color = if (selected) CyanAccent else SurfaceVariantDark
                            ) {
                                Text(
                                    text = texture,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) DeepNavy else TextPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("HAIR DENSITY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Light", "Medium", "Dense", "Fine").forEach { den ->
                            val selected = selectedDensity == den
                            Surface(
                                onClick = { selectedDensity = den },
                                shape = RoundedCornerShape(8.dp),
                                color = if (selected) CopperAccent else SurfaceVariantDark
                            ) {
                                Text(
                                    text = den,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) DeepNavy else TextPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = desiredStyle,
                        onValueChange = { desiredStyle = it },
                        label = { Text("Desired Fade Style / Cut Goal") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanAccent),
                        modifier = Modifier.fillMaxWidth().testTag("input_desired_style")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            viewModel.analyzeHairAndRecommendFade(selectedTexture, selectedDensity, desiredStyle)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = DeepNavy),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isAiLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_generate_ai_strategy")
                    ) {
                        if (isAiLoading) {
                            CircularProgressIndicator(color = DeepNavy, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analyzing with Gemini AI...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(imageVector = Icons.Default.Psychology, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("GENERATE AI FADE STRATEGY", fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }

        // AI Result Card
        item {
            AnimatedVisibility(visible = aiRecommendation != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .testTag("card_ai_result"),
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = CopperAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("RECOMMENDED GUARD SEQUENCE & COACHING", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CopperAccent)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = aiRecommendation ?: "",
                            fontSize = 13.sp,
                            color = TextPrimary,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onApplyAiRecipeToPlan,
                            colors = ButtonDefaults.buttonColors(containerColor = CopperAccent, contentColor = DeepNavy),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("btn_apply_ai_recipe")
                        ) {
                            Icon(imageVector = Icons.Default.ContentCut, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("APPLY AI RECIPE TO FADE PLANNER", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
