package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BodyweightEntry
import com.example.ui.components.BodyweightLineChart
import com.example.ui.components.VolumeProgressionBarChart
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonLime
import com.example.ui.theme.PulseBackground
import com.example.ui.theme.PulseBorder
import com.example.ui.theme.PulseRed
import com.example.ui.theme.PulseSurface
import com.example.ui.theme.PulseSurfaceElevated
import com.example.ui.theme.PulseTextPrimary
import com.example.ui.theme.PulseTextSecondary
import com.example.ui.viewmodel.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProgressionScreen(
    viewModel: WorkoutViewModel,
    modifier: Modifier = Modifier
) {
    val volumeStats by viewModel.volumeProgression.collectAsStateWithLifecycle()
    val bodyweightEntries by viewModel.bodyweightEntries.collectAsStateWithLifecycle()
    val unitLabel = viewModel.weightUnitLabel()

    var showLogWeightDialog by remember { mutableStateOf(false) }
    var weightInput by remember { mutableStateOf("") }
    var noteInput by remember { mutableStateOf("") }

    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PulseBackground)
            .padding(horizontal = 16.dp)
            .testTag("progression_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        item {
            Text(
                text = "PROGRESSION & ANALYTICS",
                style = MaterialTheme.typography.headlineMedium,
                color = NeonLime
            )
            Text(
                text = "Track volume overload and bodyweight trajectory over time",
                style = MaterialTheme.typography.bodyMedium,
                color = PulseTextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Volume Progression Bar Chart
        item {
            VolumeProgressionBarChart(
                stats = volumeStats,
                unitLabel = unitLabel
            )
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Bodyweight Progression Line Chart
        item {
            BodyweightLineChart(
                entries = bodyweightEntries,
                unitLabel = unitLabel,
                onLogWeightClick = { showLogWeightDialog = true }
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Bodyweight Logs History
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BODYWEIGHT LOG HISTORY",
                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
                    color = PulseTextSecondary
                )
                Surface(
                    onClick = { showLogWeightDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    color = NeonLime.copy(alpha = 0.15f),
                    contentColor = NeonLime
                ) {
                    Text("+ Log Entry", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (bodyweightEntries.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PulseSurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("No bodyweight logs yet. Tap '+ Log Entry'.", modifier = Modifier.padding(16.dp), color = PulseTextSecondary)
                }
            }
        } else {
            items(bodyweightEntries.sortedByDescending { it.date }) { entry ->
                BodyweightHistoryRow(
                    entry = entry,
                    unitLabel = unitLabel,
                    displayWeight = viewModel.displayWeight(entry.weightKg),
                    onDelete = { viewModel.deleteBodyweight(entry) },
                    dateLabel = sdf.format(Date(entry.date))
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }

    // Log Bodyweight Dialog
    if (showLogWeightDialog) {
        AlertDialog(
            onDismissRequest = { showLogWeightDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Scale, contentDescription = null, tint = NeonLime, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("LOG BODYWEIGHT", style = MaterialTheme.typography.titleMedium, color = PulseTextPrimary)
                }
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Weight ($unitLabel)") },
                        placeholder = { Text("e.g. 78.5") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth().testTag("bodyweight_input_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonLime,
                            unfocusedBorderColor = PulseBorder,
                            focusedTextColor = PulseTextPrimary,
                            unfocusedTextColor = PulseTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        label = { Text("Notes (optional)") },
                        placeholder = { Text("e.g. Morning fasted, post hydration") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonLime,
                            unfocusedBorderColor = PulseBorder,
                            focusedTextColor = PulseTextPrimary,
                            unfocusedTextColor = PulseTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = weightInput.toDoubleOrNull()
                        if (parsed != null && parsed > 0.0) {
                            viewModel.logBodyweight(parsed, noteInput)
                            weightInput = ""
                            noteInput = ""
                            showLogWeightDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("save_bodyweight_button")
                ) {
                    Text("Save", color = PulseBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogWeightDialog = false }) {
                    Text("Cancel", color = PulseTextSecondary)
                }
            },
            containerColor = PulseSurface,
            shape = RoundedCornerShape(18.dp)
        )
    }
}

@Composable
fun BodyweightHistoryRow(
    entry: BodyweightEntry,
    unitLabel: String,
    displayWeight: String,
    onDelete: () -> Unit,
    dateLabel: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PulseSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, PulseBorder)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "$displayWeight $unitLabel",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = NeonLime
                )
                Text(
                    text = if (entry.note.isNotBlank()) "$dateLabel • ${entry.note}" else dateLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = PulseTextSecondary
                )
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = PulseRed.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
            }
        }
    }
}
