package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Exercise
import com.example.data.model.Routine
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutinesScreen(
    viewModel: WorkoutViewModel,
    modifier: Modifier = Modifier
) {
    val routines by viewModel.allRoutines.collectAsStateWithLifecycle()
    val allExercises by viewModel.allExercises.collectAsStateWithLifecycle()

    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Push/Pull/Legs", "Upper/Lower", "Full Body", "Cardio", "Custom")

    var showCreateRoutineSheet by remember { mutableStateOf(false) }

    val filteredRoutines = routines.filter { routine ->
        if (selectedCategory == "All") true
        else if (selectedCategory == "Custom") !routine.isTemplate
        else routine.category.equals(selectedCategory, ignoreCase = true)
    }

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("routines_screen"),
        containerColor = PulseBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateRoutineSheet = true },
                containerColor = ElectricCyan,
                contentColor = PulseBackground,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 80.dp).testTag("fab_create_routine")
            ) {
                Row(modifier = Modifier.padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = "Create Routine")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Build Routine", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
        ) {
            item {
                Text(
                    text = "ROUTINE BUILDER",
                    style = MaterialTheme.typography.headlineMedium,
                    color = ElectricCyan
                )
                Text(
                    text = "Pick a battle-tested routine or construct your custom workout",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PulseTextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Categories horizontal bar
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedCategory = cat }
                                .testTag("routine_category_tab_$cat"),
                            color = if (isSelected) ElectricCyan else PulseSurfaceElevated,
                            contentColor = if (isSelected) PulseBackground else PulseTextPrimary,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = cat,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Routines list
            if (filteredRoutines.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                        colors = CardDefaults.cardColors(containerColor = PulseSurface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = PulseTextSecondary, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No routines found in this category", color = PulseTextSecondary)
                        }
                    }
                }
            } else {
                items(filteredRoutines, key = { it.id }) { routine ->
                    RoutineCard(
                        routine = routine,
                        onStart = { viewModel.startRoutine(routine.id) },
                        onDelete = if (!routine.isTemplate) { { viewModel.deleteRoutine(routine) } } else null
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }

    // Full Routine Builder Bottom Sheet
    if (showCreateRoutineSheet) {
        CreateRoutineBottomSheet(
            allExercises = allExercises,
            onSave = { name, category, desc, exercisesList ->
                viewModel.createRoutine(name, category, desc, exercisesList)
                showCreateRoutineSheet = false
            },
            onDismiss = { showCreateRoutineSheet = false }
        )
    }
}

@Composable
fun RoutineCard(
    routine: Routine,
    onStart: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("routine_card_${routine.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PulseSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, PulseBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ElectricCyan.copy(alpha = 0.15f),
                    contentColor = ElectricCyan
                ) {
                    Text(
                        text = routine.category.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = PulseTextSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${routine.estimatedDurationMinutes} mins",
                        style = MaterialTheme.typography.labelSmall,
                        color = PulseTextSecondary
                    )

                    if (onDelete != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete routine", tint = PulseRed.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = routine.name,
                style = MaterialTheme.typography.titleLarge,
                color = PulseTextPrimary
            )

            if (routine.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = routine.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PulseTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onStart,
                colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("start_routine_card_${routine.id}")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = PulseBackground)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Start Workout", fontWeight = FontWeight.Bold, color = PulseBackground)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoutineBottomSheet(
    allExercises: List<Exercise>,
    onSave: (String, String, String, List<Triple<Exercise, Int, String>>) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Push/Pull/Legs") }
    var description by remember { mutableStateOf("") }
    val selectedExercises = remember { mutableStateListOf<Triple<Exercise, Int, String>>() }
    var showExercisePicker by remember { mutableStateOf(false) }

    val categories = listOf("Push/Pull/Legs", "Upper/Lower", "Full Body", "Cardio", "Custom")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = PulseSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
                .testTag("create_routine_sheet")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("BUILD NEW ROUTINE", style = MaterialTheme.typography.titleLarge, color = ElectricCyan)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = PulseTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Routine Name") },
                placeholder = { Text("e.g. Chest & Tricep Hypertrophy") },
                modifier = Modifier.fillMaxWidth().testTag("routine_name_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricCyan,
                    unfocusedBorderColor = PulseBorder,
                    focusedTextColor = PulseTextPrimary,
                    unfocusedTextColor = PulseTextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category selector chips
            Text("Category:", style = MaterialTheme.typography.labelSmall, color = PulseTextSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val isSelected = category == cat
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { category = cat },
                        color = if (isSelected) ElectricCyan else PulseSurfaceElevated,
                        contentColor = if (isSelected) PulseBackground else PulseTextPrimary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = cat,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Focus / Description") },
                placeholder = { Text("Focus on progressive overload on bench press...") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricCyan,
                    unfocusedBorderColor = PulseBorder,
                    focusedTextColor = PulseTextPrimary,
                    unfocusedTextColor = PulseTextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Exercises in Routine
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("EXERCISES (${selectedExercises.size})", style = MaterialTheme.typography.titleMedium, color = PulseTextPrimary)
                Surface(
                    onClick = { showExercisePicker = true },
                    shape = RoundedCornerShape(8.dp),
                    color = ElectricCyan.copy(alpha = 0.15f),
                    contentColor = ElectricCyan,
                    modifier = Modifier.testTag("add_exercise_to_routine_builder")
                ) {
                    Text("+ Add Movement", modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.height(200.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (selectedExercises.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                            Text("No exercises added yet. Tap '+ Add Movement'.", color = PulseTextSecondary)
                        }
                    }
                } else {
                    items(selectedExercises.indices.toList()) { index ->
                        val item = selectedExercises[index]
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = PulseSurfaceElevated),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(item.first.name, style = MaterialTheme.typography.titleSmall, color = PulseTextPrimary)
                                    Text("${item.second} sets • ${item.third} reps", style = MaterialTheme.typography.bodySmall, color = ElectricCyan)
                                }
                                IconButton(onClick = { selectedExercises.removeAt(index) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = PulseRed, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && selectedExercises.isNotEmpty()) {
                        onSave(name, category, description, selectedExercises.toList())
                    }
                },
                enabled = name.isNotBlank() && selectedExercises.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_routine_button"),
                colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Routine", fontWeight = FontWeight.Bold, color = PulseBackground)
            }
        }
    }

    if (showExercisePicker) {
        AddExerciseBottomSheet(
            allExercises = allExercises,
            onSelectExercise = { ex ->
                selectedExercises.add(Triple(ex, 3, "8-12"))
                showExercisePicker = false
            },
            onDismiss = { showExercisePicker = false }
        )
    }
}
