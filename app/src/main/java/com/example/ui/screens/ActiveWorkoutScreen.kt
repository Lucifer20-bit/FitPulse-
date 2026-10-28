package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ActiveWorkoutExercise
import com.example.data.model.Exercise
import com.example.ui.components.PrCelebrationDialog
import com.example.ui.components.RestStopwatchFloatingBar
import com.example.ui.components.RestStopwatchModal
import com.example.ui.components.WorkoutSetRow
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.PulseBackground
import com.example.ui.theme.PulseBorder
import com.example.ui.theme.PulseRed
import com.example.ui.theme.PulseSurface
import com.example.ui.theme.PulseSurfaceElevated
import com.example.ui.theme.PulseTextPrimary
import com.example.ui.theme.PulseTextSecondary
import com.example.ui.theme.TrophyGold
import com.example.ui.viewmodel.WorkoutViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    viewModel: WorkoutViewModel,
    modifier: Modifier = Modifier
) {
    // Intercept hardware back button to minimize rather than exit app
    BackHandler {
        viewModel.closeWorkoutScreen()
    }

    val activeSession by viewModel.activeSession.collectAsStateWithLifecycle()
    val exercisesWithSets by viewModel.activeWorkoutExercises.collectAsStateWithLifecycle()
    val allExercises by viewModel.allExercises.collectAsStateWithLifecycle()
    val workoutDuration by viewModel.workoutDurationSeconds.collectAsStateWithLifecycle()
    val unitLabel = viewModel.weightUnitLabel()

    // Rest stopwatch state
    val restRemaining by viewModel.restSecondsRemaining.collectAsStateWithLifecycle()
    val restTotal by viewModel.restTotalSeconds.collectAsStateWithLifecycle()
    val isRestRunning by viewModel.isRestTimerRunning.collectAsStateWithLifecycle()
    var showRestModal by remember { mutableStateOf(false) }

    // Dialogs
    var showFinishDialog by remember { mutableStateOf(false) }
    var showDiscardConfirmDialog by remember { mutableStateOf(false) }
    var showAddExerciseSheet by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }
    var workoutNotes by remember { mutableStateOf("") }

    // PR Celebration
    val celebratingPr by viewModel.celebratingPr.collectAsStateWithLifecycle()

    if (activeSession == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(PulseBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("No workout in progress", color = PulseTextSecondary)
        }
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("active_workout_screen"),
        containerColor = PulseBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = activeSession?.title ?: "Workout",
                            style = MaterialTheme.typography.titleMedium,
                            color = PulseTextPrimary,
                            maxLines = 1
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).background(NeonLime, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = formatDuration(workoutDuration),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = NeonLime
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.closeWorkoutScreen() },
                        modifier = Modifier.testTag("minimize_workout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Minimize workout",
                            tint = PulseTextPrimary
                        )
                    }
                },
                actions = {
                    // Stopwatch trigger button
                    IconButton(
                        onClick = { showRestModal = true },
                        modifier = Modifier.testTag("open_rest_modal_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Rest timer",
                            tint = if (restRemaining > 0) NeonOrange else ElectricCyan
                        )
                    }

                    // Finish Workout primary button
                    Button(
                        onClick = { showFinishDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.padding(end = 4.dp).testTag("finish_workout_top_button")
                    ) {
                        Text("Finish", fontWeight = FontWeight.Bold, color = PulseBackground, fontSize = 13.sp)
                    }

                    // Options menu (Discard)
                    Box {
                        IconButton(onClick = { showOptionsMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options", tint = PulseTextSecondary)
                        }
                        DropdownMenu(
                            expanded = showOptionsMenu,
                            onDismissRequest = { showOptionsMenu = false },
                            modifier = Modifier.background(PulseSurfaceElevated)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Discard Workout", color = PulseRed) },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = PulseRed) },
                                onClick = {
                                    showOptionsMenu = false
                                    showDiscardConfirmDialog = true
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PulseSurface)
            )
        },
        bottomBar = {
            // Floating Rest Stopwatch Banner if running
            RestStopwatchFloatingBar(
                secondsRemaining = restRemaining,
                totalSeconds = restTotal,
                isRunning = isRestRunning,
                onTogglePlay = {
                    if (isRestRunning) viewModel.pauseRestTimer() else viewModel.resumeRestTimer()
                },
                onAddSeconds = { viewModel.adjustRestSeconds(it) },
                onClose = { viewModel.stopRestTimer() }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp)
        ) {
            // Summary Stats Bar
            item {
                ActiveWorkoutStatsBar(
                    exercisesCount = exercisesWithSets.size,
                    totalSetsCompleted = exercisesWithSets.sumOf { it.sets.count { s -> s.isCompleted } },
                    totalVolumeKg = exercisesWithSets.sumOf { ex ->
                        ex.sets.filter { it.isCompleted }.sumOf { it.weightKg * it.reps }
                    },
                    unitLabel = unitLabel
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Exercise Cards
            if (exercisesWithSets.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = PulseSurface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("No exercises added yet", style = MaterialTheme.typography.titleMedium, color = PulseTextPrimary)
                            Text("Tap the button below to add your first movement", style = MaterialTheme.typography.bodyMedium, color = PulseTextSecondary)
                        }
                    }
                }
            } else {
                items(exercisesWithSets, key = { it.exercise.id }) { item ->
                    ActiveExerciseCard(
                        activeExercise = item,
                        unitLabel = unitLabel,
                        viewModel = viewModel
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // "+ Add Exercise" Button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showAddExerciseSheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("add_exercise_to_workout_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PulseSurfaceElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = ElectricCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Exercise", style = MaterialTheme.typography.labelLarge, color = ElectricCyan)
                }
            }
        }
    }

    // Modal Rest Stopwatch Sheet
    if (showRestModal) {
        ModalBottomSheet(
            onDismissRequest = { showRestModal = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = PulseSurface
        ) {
            RestStopwatchModal(
                secondsRemaining = restRemaining,
                totalSeconds = restTotal,
                isRunning = isRestRunning,
                onStartTimer = { viewModel.startRestTimer(it) },
                onTogglePlay = {
                    if (isRestRunning) viewModel.pauseRestTimer() else viewModel.resumeRestTimer()
                },
                onAddSeconds = { viewModel.adjustRestSeconds(it) },
                onClose = { showRestModal = false }
            )
        }
    }

    // Add Exercise Bottom Sheet Picker
    if (showAddExerciseSheet) {
        AddExerciseBottomSheet(
            allExercises = allExercises,
            onSelectExercise = { ex ->
                viewModel.addExerciseToWorkout(ex)
                showAddExerciseSheet = false
            },
            onDismiss = { showAddExerciseSheet = false }
        )
    }

    // Finish Workout Confirmation & Summary Dialog
    if (showFinishDialog) {
        val completedSets = exercisesWithSets.sumOf { it.sets.count { s -> s.isCompleted } }
        val totalVolume = exercisesWithSets.sumOf { ex ->
            ex.sets.filter { it.isCompleted }.sumOf { it.weightKg * it.reps }
        }
        val prCount = exercisesWithSets.sumOf { it.sets.count { s -> s.isCompleted && s.isPr } }

        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = TrophyGold, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("FINISH WORKOUT", style = MaterialTheme.typography.titleLarge, color = PulseTextPrimary)
                }
            },
            text = {
                Column {
                    Text("Great session! Here is your training recap:", style = MaterialTheme.typography.bodyMedium, color = PulseTextSecondary)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        RecapStatBadge("TIME", formatDuration(workoutDuration), ElectricCyan)
                        RecapStatBadge("VOLUME", "${totalVolume.toInt()} $unitLabel", NeonLime)
                        RecapStatBadge("SETS", "$completedSets", PulseTextPrimary)
                        if (prCount > 0) {
                            RecapStatBadge("PRs", "$prCount 🏆", TrophyGold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = workoutNotes,
                        onValueChange = { workoutNotes = it },
                        label = { Text("Workout Notes (optional)") },
                        placeholder = { Text("Felt energetic, increased bench weight...") },
                        modifier = Modifier.fillMaxWidth().testTag("workout_notes_input"),
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
                        viewModel.finishActiveWorkout(workoutNotes)
                        showFinishDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("confirm_finish_workout_button")
                ) {
                    Text("Complete & Save", color = PulseBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text("Cancel", color = PulseTextSecondary)
                }
            },
            containerColor = PulseSurface,
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Discard Confirmation Dialog
    if (showDiscardConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardConfirmDialog = false },
            title = { Text("Discard Workout?", style = MaterialTheme.typography.titleMedium, color = PulseRed) },
            text = { Text("Are you sure? All logged sets for this session will be permanently deleted.", color = PulseTextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.discardActiveWorkout()
                        showDiscardConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PulseRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("confirm_discard_workout_button")
                ) {
                    Text("Discard", color = PulseTextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardConfirmDialog = false }) {
                    Text("Cancel", color = PulseTextSecondary)
                }
            },
            containerColor = PulseSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // PR Celebration Modal
    PrCelebrationDialog(
        pr = celebratingPr,
        onDismiss = { viewModel.dismissPrCelebration() }
    )
}

@Composable
fun ActiveWorkoutStatsBar(
    exercisesCount: Int,
    totalSetsCompleted: Int,
    totalVolumeKg: Double,
    unitLabel: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PulseSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, PulseBorder)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("EXERCISES", style = MaterialTheme.typography.labelSmall, color = PulseTextSecondary)
                Text("$exercisesCount", style = MaterialTheme.typography.titleMedium, color = PulseTextPrimary)
            }
            Column {
                Text("SETS DONE", style = MaterialTheme.typography.labelSmall, color = PulseTextSecondary)
                Text("$totalSetsCompleted", style = MaterialTheme.typography.titleMedium, color = ElectricCyan)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("TOTAL VOLUME", style = MaterialTheme.typography.labelSmall, color = PulseTextSecondary)
                Text("${totalVolumeKg.toInt()} $unitLabel", style = MaterialTheme.typography.titleMedium, color = NeonLime)
            }
        }
    }
}

@Composable
fun ActiveExerciseCard(
    activeExercise: ActiveWorkoutExercise,
    unitLabel: String,
    viewModel: WorkoutViewModel
) {
    val exercise = activeExercise.exercise

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("active_exercise_card_${exercise.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PulseSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, PulseBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Exercise Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = ElectricCyan
                    )
                    Text(
                        text = "${exercise.category} • ${exercise.primaryMuscle}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PulseTextSecondary
                    )
                }

                Surface(
                    onClick = { viewModel.startRestTimer(exercise.defaultRestSeconds) },
                    shape = RoundedCornerShape(8.dp),
                    color = PulseSurfaceElevated,
                    contentColor = NeonOrange,
                    border = androidx.compose.foundation.BorderStroke(1.dp, PulseBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = NeonOrange, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${exercise.defaultRestSeconds}s rest",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Set List Rows
            activeExercise.sets.forEach { set ->
                WorkoutSetRow(
                    set = set,
                    unitLabel = unitLabel,
                    displayedWeight = viewModel.displayWeight(set.weightKg),
                    onWeightDelta = { delta -> viewModel.incrementWeight(set, delta) },
                    onRepsDelta = { delta -> viewModel.incrementReps(set, delta) },
                    onSetTypeChange = { type -> viewModel.updateSetType(set, type) },
                    onToggleCompleted = { viewModel.toggleSetCompleted(set, exercise.defaultRestSeconds) },
                    onDeleteSet = { viewModel.removeSet(set) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // "+ Add Set" button
            TextButton(
                onClick = { viewModel.addSet(exercise.id) },
                modifier = Modifier.fillMaxWidth().testTag("add_set_to_${exercise.id}")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = NeonLime, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("+ Add Set", color = NeonLime, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExerciseBottomSheet(
    allExercises: List<Exercise>,
    onSelectExercise: (Exercise) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Chest", "Back", "Legs", "Shoulders", "Arms", "Core", "Cardio")

    val filtered = allExercises.filter { ex ->
        (selectedCategory == "All" || ex.category.equals(selectedCategory, ignoreCase = true)) &&
            (searchQuery.isBlank() || ex.name.contains(searchQuery, ignoreCase = true) || ex.primaryMuscle.contains(searchQuery, ignoreCase = true))
    }

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
                .testTag("add_exercise_bottom_sheet")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("SELECT EXERCISE", style = MaterialTheme.typography.titleMedium, color = ElectricCyan)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = PulseTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name or muscle...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PulseTextSecondary) },
                modifier = Modifier.fillMaxWidth().testTag("exercise_search_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricCyan,
                    unfocusedBorderColor = PulseBorder,
                    focusedTextColor = PulseTextPrimary,
                    unfocusedTextColor = PulseTextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedCategory = cat },
                        color = if (isSelected) ElectricCyan else PulseSurfaceElevated,
                        contentColor = if (isSelected) PulseBackground else PulseTextPrimary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = cat,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier.height(340.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filtered, key = { it.id }) { ex ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectExercise(ex) }
                            .testTag("select_exercise_${ex.id}"),
                        colors = CardDefaults.cardColors(containerColor = PulseSurfaceElevated),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(ex.name, style = MaterialTheme.typography.titleSmall, color = PulseTextPrimary)
                                Text("${ex.category} • ${ex.primaryMuscle}", style = MaterialTheme.typography.bodySmall, color = PulseTextSecondary)
                            }
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = ElectricCyan)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecapStatBadge(title: String, value: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = PulseSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, PulseBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = PulseTextSecondary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold), color = color)
        }
    }
}

private fun formatDuration(seconds: Int): String {
    val hrs = seconds / 3600
    val mins = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hrs > 0) String.format(Locale.US, "%02d:%02d:%02d", hrs, mins, secs)
    else String.format(Locale.US, "%02d:%02d", mins, secs)
}
