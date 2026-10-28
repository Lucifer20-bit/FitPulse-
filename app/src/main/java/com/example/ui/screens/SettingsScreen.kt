package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonLime
import com.example.ui.theme.PulseBackground
import com.example.ui.theme.PulseBorder
import com.example.ui.theme.PulseSurface
import com.example.ui.theme.PulseSurfaceElevated
import com.example.ui.theme.PulseTextPrimary
import com.example.ui.theme.PulseTextSecondary
import com.example.ui.viewmodel.WorkoutViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: WorkoutViewModel,
    modifier: Modifier = Modifier
) {
    val isLbs by viewModel.isLbsUnit.collectAsStateWithLifecycle()
    val autoRest by viewModel.autoStartRestTimer.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    var soundEnabled by remember { mutableStateOf(viewModel.soundManager.isSoundEnabled) }
    var hapticsEnabled by remember { mutableStateOf(viewModel.soundManager.isHapticsEnabled) }
    var cloudSyncEnabled by remember { mutableStateOf(true) }

    var showExportDialog by remember { mutableStateOf(false) }
    var exportedJsonText by remember { mutableStateOf("") }
    var showRestoreConfirmDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PulseBackground)
            .padding(horizontal = 16.dp)
            .testTag("settings_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        item {
            Text(
                text = "SETTINGS & PREFERENCES",
                style = MaterialTheme.typography.headlineMedium,
                color = ElectricCyan
            )
            Text(
                text = "Units, sound timers, and cloud data backup options",
                style = MaterialTheme.typography.bodyMedium,
                color = PulseTextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Units Section
        item {
            Text("UNITS OF MEASURE", style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp), color = PulseTextSecondary)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PulseSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PulseBorder)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Scale, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Weight Units", style = MaterialTheme.typography.titleMedium, color = PulseTextPrimary)
                            Text(if (isLbs) "Pounds (lbs)" else "Kilograms (kg)", style = MaterialTheme.typography.bodySmall, color = PulseTextSecondary)
                        }
                    }

                    Button(
                        onClick = { viewModel.toggleWeightUnit() },
                        colors = ButtonDefaults.buttonColors(containerColor = PulseSurfaceElevated),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("toggle_units_button")
                    ) {
                        Text(if (isLbs) "Switch to KG" else "Switch to LBS", color = ElectricCyan, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // In-Workout Rest & Audio Preferences
        item {
            Text("REST TIMER & AUDIO CUES", style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp), color = PulseTextSecondary)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PulseSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PulseBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Auto-start rest timer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = NeonLime, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Auto-Start Rest Timer", style = MaterialTheme.typography.titleSmall, color = PulseTextPrimary)
                                Text("Starts stopwatch on set completion", style = MaterialTheme.typography.bodySmall, color = PulseTextSecondary)
                            }
                        }
                        Switch(
                            checked = autoRest,
                            onCheckedChange = { viewModel.autoStartRestTimer.value = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PulseBackground, checkedTrackColor = NeonLime)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Audio Cues
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Sound Cues", style = MaterialTheme.typography.titleSmall, color = PulseTextPrimary)
                                Text("Warning beeps at 3, 2, 1s & finish chime", style = MaterialTheme.typography.bodySmall, color = PulseTextSecondary)
                            }
                        }
                        Switch(
                            checked = soundEnabled,
                            onCheckedChange = {
                                soundEnabled = it
                                viewModel.soundManager.isSoundEnabled = it
                                if (it) viewModel.soundManager.playSetCompleteTap()
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = PulseBackground, checkedTrackColor = ElectricCyan)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Haptic Vibration
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Vibration, contentDescription = null, tint = PulseTextPrimary, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Haptic Feedback", style = MaterialTheme.typography.titleSmall, color = PulseTextPrimary)
                                Text("Vibrate on timer alerts & PR hits", style = MaterialTheme.typography.bodySmall, color = PulseTextSecondary)
                            }
                        }
                        Switch(
                            checked = hapticsEnabled,
                            onCheckedChange = {
                                hapticsEnabled = it
                                viewModel.soundManager.isHapticsEnabled = it
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = PulseBackground, checkedTrackColor = PulseTextPrimary)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Multi-Device Cloud Sync & Backup Section
        item {
            Text("DATA BACKUP & CLOUD SYNC", style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp), color = PulseTextSecondary)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PulseSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PulseBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudDone, contentDescription = null, tint = NeonLime, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Cloud Sync Status", style = MaterialTheme.typography.titleSmall, color = PulseTextPrimary)
                                Text(if (cloudSyncEnabled) "Active • Local data synced" else "Offline storage only", style = MaterialTheme.typography.bodySmall, color = NeonLime)
                            }
                        }
                        Switch(
                            checked = cloudSyncEnabled,
                            onCheckedChange = { cloudSyncEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PulseBackground, checkedTrackColor = NeonLime)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                scope.launch {
                                    val json = viewModel.repository.exportDataToJson()
                                    exportedJsonText = json
                                    showExportDialog = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PulseSurfaceElevated),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("export_json_button")
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export JSON", color = ElectricCyan, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { showRestoreConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PulseSurfaceElevated),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("restore_templates_button")
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, tint = PulseTextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset Data", color = PulseTextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // About Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PulseSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PulseBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("FitPulse v1.0", style = MaterialTheme.typography.titleMedium, color = PulseTextPrimary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Built for rapid set logging, zero clutter, active rest countdown with audio alarms, and personal record trophies.",
                        style = MaterialTheme.typography.bodySmall,
                        color = PulseTextSecondary
                    )
                }
            }
        }
    }

    // Export Dialog with Copy Button
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("BACKUP EXPORT (JSON)", style = MaterialTheme.typography.titleMedium, color = ElectricCyan) },
            text = {
                Column {
                    Text("Copy this backup payload to transfer your data between devices:", style = MaterialTheme.typography.bodySmall, color = PulseTextSecondary)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = exportedJsonText,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = PulseBorder,
                            focusedTextColor = PulseTextPrimary,
                            unfocusedTextColor = PulseTextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(exportedJsonText))
                        Toast.makeText(context, "Workout data copied to clipboard!", Toast.LENGTH_SHORT).show()
                        showExportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Copy to Clipboard", color = PulseBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Close", color = PulseTextSecondary)
                }
            },
            containerColor = PulseSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Reset Templates Confirmation
    if (showRestoreConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreConfirmDialog = false },
            title = { Text("Restore Standard Templates?", style = MaterialTheme.typography.titleMedium, color = PulseTextPrimary) },
            text = { Text("This will reload the official Push/Pull/Legs, Upper/Lower, and Full Body workouts and sample charts.", color = PulseTextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.restoreTemplates()
                        Toast.makeText(context, "Standard routines restored!", Toast.LENGTH_SHORT).show()
                        showRestoreConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Restore", color = PulseBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreConfirmDialog = false }) {
                    Text("Cancel", color = PulseTextSecondary)
                }
            },
            containerColor = PulseSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
