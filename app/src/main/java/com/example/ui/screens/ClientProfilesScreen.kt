package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.db.ClientProfileEntity
import com.example.ui.theme.CopperAccent
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ClientProfilesScreen(
    viewModel: MainViewModel,
    onSelectClientForPlan: (ClientProfileEntity) -> Unit
) {
    val clients by viewModel.clientProfiles.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf("") }
    var textureInput by remember { mutableStateOf("Coily") }
    var densityInput by remember { mutableStateOf("Dense") }
    var fadeTypeInput by remember { mutableStateOf("Low Skin Fade") }
    var notesInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
            .padding(16.dp)
            .testTag("client_profiles_lazy_column")
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("CLIENT DIRECTORY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
                    Text("Repeatable Fade Profiles", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                }

                Button(
                    onClick = { showAddDialog = !showAddDialog },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = DeepNavy),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_toggle_add_client")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Client", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (showAddDialog) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .testTag("card_add_client_form"),
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("NEW CLIENT FADE PROFILE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Full Name") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanAccent),
                            modifier = Modifier.fillMaxWidth().testTag("input_client_name")
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Hair Texture:", fontSize = 11.sp, color = TextSecondary)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                            listOf("Straight", "Wavy", "Curly", "Coily").forEach { text ->
                                Surface(
                                    onClick = { textureInput = text },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (textureInput == text) CyanAccent else SurfaceVariantDark
                                ) {
                                    Text(
                                        text = text,
                                        fontSize = 11.sp,
                                        color = if (textureInput == text) DeepNavy else TextPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = fadeTypeInput,
                            onValueChange = { fadeTypeInput = it },
                            label = { Text("Preferred Fade Style") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanAccent),
                            modifier = Modifier.fillMaxWidth().testTag("input_preferred_fade")
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = notesInput,
                            onValueChange = { notesInput = it },
                            label = { Text("Guard & Scalp Notes") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanAccent),
                            modifier = Modifier.fillMaxWidth().testTag("input_client_notes")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (nameInput.isNotBlank()) {
                                    viewModel.saveClientProfile(
                                        name = nameInput,
                                        texture = textureInput,
                                        density = densityInput,
                                        fadeType = fadeTypeInput,
                                        notes = notesInput
                                    )
                                    nameInput = ""
                                    notesInput = ""
                                    showAddDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = DeepNavy),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("btn_save_client_profile")
                        ) {
                            Text("SAVE PROFILE TO ROOM DB", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (clients.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Group, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No Clients Saved Yet", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }
        } else {
            items(clients) { client ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .testTag("client_card_${client.id}"),
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(client.name, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("${client.preferredFadeType} • ${client.hairTexture} (${client.hairDensity})", fontSize = 12.sp, color = CyanAccent)
                            if (client.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(client.notes, fontSize = 11.sp, color = TextSecondary)
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(
                                onClick = { onSelectClientForPlan(client) },
                                colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark, contentColor = CopperAccent),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("btn_select_client_${client.id}")
                            ) {
                                Text("New Cut Plan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            IconButton(onClick = { viewModel.deleteClientProfile(client.id) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Client", tint = Color(0xFFEF4444))
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
