package com.example.ui.screens

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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Routine
import com.example.data.model.WorkoutSession
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonLime
import com.example.ui.theme.PulseBackground
import com.example.ui.theme.PulseBorder
import com.example.ui.theme.PulseSurface
import com.example.ui.theme.PulseSurfaceElevated
import com.example.ui.theme.PulseTextPrimary
import com.example.ui.theme.PulseTextSecondary
import com.example.ui.theme.TrophyGold
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: WorkoutViewModel,
    modifier: Modifier = Modifier
) {
    val activeSession by viewModel.activeSession.collectAsStateWithLifecycle()
    val completedWorkouts by viewModel.completedWorkouts.collectAsStateWithLifecycle()
    val routines by viewModel.allRoutines.collectAsStateWithLifecycle()
    val prSummaries by viewModel.prSummaries.collectAsStateWithLifecycle()
    val unitLabel = viewModel.weightUnitLabel()
    val workoutDuration by viewModel.workoutDurationSeconds.collectAsStateWithLifecycle()

    val totalVolumeAllTime = completedWorkouts.sumOf { it.totalVolumeKg }
    val totalWorkoutsCount = completedWorkouts.size
    val totalPrCount = prSummaries.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PulseBackground)
            .padding(horizontal = 16.dp)
            .testTag("home_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // App Header with Athletic Pulse Branding
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "FIT",
                            style = MaterialTheme.typography.headlineLarge,
                            color = ElectricCyan
                        )
                        Text(
                            text = "PULSE",
                            style = MaterialTheme.typography.headlineLarge,
                            color = NeonLime
                        )
                    }
                    Text(
                        text = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = PulseTextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = PulseSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, PulseBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(NeonLime, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "READY",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = NeonLime
                        )
                    }
                }
            }
        }

        // Active Workout Resume Banner (if workout is in progress)
        if (activeSession != null) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openWorkoutScreen() }
                        .testTag("active_workout_banner"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = PulseSurfaceElevated),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonLime)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(NeonLime.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Active Workout",
                                    tint = NeonLime,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "WORKOUT IN PROGRESS",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = NeonLime
                                )
                                Text(
                                    text = activeSession?.title ?: "Quick Workout",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = PulseTextPrimary
                                )
                                Text(
                                    text = formatDuration(workoutDuration),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = ElectricCyan
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.openWorkoutScreen() },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("resume_workout_button")
                        ) {
                            Text(
                                text = "Resume",
                                fontWeight = FontWeight.Bold,
                                color = PulseBackground
                            )
                        }
                    }
                }
            }
        }

        // Quick Start Action Card
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_start_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = PulseSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PulseBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(ElectricCyan.copy(alpha = 0.12f), PulseSurface)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "LOG A WORKOUT",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = ElectricCyan
                                )
                                Text(
                                    text = "Start from scratch or select a routine",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = PulseTextSecondary
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { viewModel.startQuickWorkout("Quick Workout") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("start_quick_workout_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = PulseBackground)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Empty Workout", fontWeight = FontWeight.Bold, color = PulseBackground)
                            }

                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { viewModel.selectTab(MainTab.ROUTINES) }
                                    .border(1.dp, ElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .testTag("browse_routines_button"),
                                color = PulseSurfaceElevated,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Routines",
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = ElectricCyan
                                )
                            }
                        }
                    }
                }
            }
        }

        // Stats Summary Grid
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "TRAINING METRICS",
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
                color = PulseTextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricStatCard(
                    title = "Workouts",
                    value = "$totalWorkoutsCount",
                    icon = Icons.Default.Schedule,
                    accentColor = ElectricCyan,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "Volume",
                    value = "${totalVolumeAllTime.toInt()} $unitLabel",
                    icon = Icons.Default.TrendingUp,
                    accentColor = NeonLime,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "PR Badges",
                    value = "$totalPrCount",
                    icon = Icons.Default.EmojiEvents,
                    accentColor = TrophyGold,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Recommended Routine Templates
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FEATURED ROUTINES",
                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
                    color = PulseTextSecondary
                )
                Text(
                    text = "See All",
                    style = MaterialTheme.typography.labelMedium,
                    color = ElectricCyan,
                    modifier = Modifier
                        .clickable { viewModel.selectTab(MainTab.ROUTINES) }
                        .padding(4.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(routines.take(4)) { routine ->
                    RoutineHomeItem(
                        routine = routine,
                        onStart = { viewModel.startRoutine(routine.id) }
                    )
                }
            }
        }

        // Recent Workouts Log
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "RECENT WORKOUTS",
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
                color = PulseTextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (completedWorkouts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PulseSurface),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No completed workouts yet. Log your first session!", color = PulseTextSecondary)
                    }
                }
            }
        } else {
            items(completedWorkouts.take(5)) { workout ->
                RecentWorkoutCard(
                    workout = workout,
                    unitLabel = unitLabel
                )
            }
        }
    }
}

@Composable
fun MetricStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PulseSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, PulseBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge.copy(fontSize = 17.sp), color = PulseTextPrimary)
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = PulseTextSecondary)
        }
    }
}

@Composable
fun RoutineHomeItem(
    routine: Routine,
    onStart: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .testTag("routine_home_item_${routine.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PulseSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, PulseBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = routine.name,
                style = MaterialTheme.typography.titleMedium,
                color = PulseTextPrimary,
                maxLines = 1
            )
            Text(
                text = "${routine.estimatedDurationMinutes} mins estimated",
                style = MaterialTheme.typography.bodySmall,
                color = PulseTextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onStart,
                colors = ButtonDefaults.buttonColors(containerColor = PulseSurfaceElevated),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("start_routine_${routine.id}")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = NeonLime, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Start Routine", color = PulseTextPrimary, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun RecentWorkoutCard(
    workout: WorkoutSession,
    unitLabel: String
) {
    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PulseSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, PulseBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = workout.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = PulseTextPrimary
                )
                Text(
                    text = "${sdf.format(Date(workout.startTime))} • ${workout.totalSets} completed sets",
                    style = MaterialTheme.typography.bodySmall,
                    color = PulseTextSecondary
                )
                if (workout.notes.isNotBlank()) {
                    Text(
                        text = "\"${workout.notes}\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = ElectricCyan,
                        maxLines = 1
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${workout.totalVolumeKg.toInt()} $unitLabel",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = NeonLime
                )
                Text(
                    text = "Total Volume",
                    style = MaterialTheme.typography.labelSmall,
                    color = PulseTextSecondary
                )
            }
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
