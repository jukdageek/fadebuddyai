package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.CameraTelemetryPreviewSurface
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DeepNavy
import com.example.ui.viewmodel.LiveTelemetryViewModel

@Composable
fun LiveTelemetryScreen(
    liveTelemetryViewModel: LiveTelemetryViewModel = viewModel()
) {
    val overlayActive by liveTelemetryViewModel.telemetryOverlayActive.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
            .padding(16.dp)
            .testTag("live_telemetry_screen")
    ) {
        Text(
            text = "LIVE TELEMETRY",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = CyanAccent,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            CameraTelemetryPreviewSurface(
                modifier = Modifier.fillMaxSize(),
                overlayContent = {
                    if (overlayActive) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = DeepNavy.copy(alpha = 0.7f),
                                modifier = Modifier.align(Alignment.TopStart)
                            ) {
                                Text(
                                    text = "Telemetry Overlay Active\nTracking position...",
                                    color = CyanAccent,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            )
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { liveTelemetryViewModel.toggleOverlay() },
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = DeepNavy),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = if (overlayActive) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (overlayActive) "Hide Overlay" else "Show Overlay",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
