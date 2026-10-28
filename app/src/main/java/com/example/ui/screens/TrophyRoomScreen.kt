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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.repository.WorkoutRepository
import com.example.ui.components.ExercisePrCard
import com.example.ui.components.MilestoneTimelineCard
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.PulseBackground
import com.example.ui.theme.PulseBorder
import com.example.ui.theme.PulseSurface
import com.example.ui.theme.PulseSurfaceElevated
import com.example.ui.theme.PulseTextPrimary
import com.example.ui.theme.PulseTextSecondary
import com.example.ui.theme.TrophyBronze
import com.example.ui.theme.TrophyDiamond
import com.example.ui.theme.TrophyGold
import com.example.ui.theme.TrophySilver
import com.example.ui.viewmodel.WorkoutViewModel

@Composable
fun TrophyRoomScreen(
    viewModel: WorkoutViewModel,
    modifier: Modifier = Modifier
) {
    val prSummaries by viewModel.prSummaries.collectAsStateWithLifecycle()
    val milestoneHistory by viewModel.milestoneHistory.collectAsStateWithLifecycle()
    val unitLabel = viewModel.weightUnitLabel()

    var activeTab by remember { mutableStateOf("PRs") } // "PRs", "Milestones", "1RM Calculator"

    // 1RM Calculator interactive state
    var calcWeight by remember { mutableStateOf("100") }
    var calcReps by remember { mutableStateOf("5") }

    val diamondCount = prSummaries.count { it.badgeTier == "Diamond" }
    val goldCount = prSummaries.count { it.badgeTier == "Gold" }
    val silverCount = prSummaries.count { it.badgeTier == "Silver" }
    val bronzeCount = prSummaries.count { it.badgeTier == "Bronze" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PulseBackground)
            .padding(horizontal = 16.dp)
            .testTag("trophy_room_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        item {
            Text(
                text = "PR TROPHY ROOM",
                style = MaterialTheme.typography.headlineMedium,
                color = TrophyGold
            )
            Text(
                text = "Personal records, badge milestones, and 1RM calculations",
                style = MaterialTheme.typography.bodyMedium,
                color = PulseTextSecondary
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Trophy Badge Counters Grid
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PulseSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PulseBorder)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TrophyBadgeCounter("DIAMOND", "$diamondCount", TrophyDiamond)
                    TrophyBadgeCounter("GOLD", "$goldCount", TrophyGold)
                    TrophyBadgeCounter("SILVER", "$silverCount", TrophySilver)
                    TrophyBadgeCounter("BRONZE", "$bronzeCount", TrophyBronze)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Section Tabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("PRs", "Milestones", "1RM Calculator").forEach { tab ->
                    val isSelected = activeTab == tab
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { activeTab = tab }
                            .testTag("trophy_tab_$tab"),
                        color = if (isSelected) TrophyGold else PulseSurfaceElevated,
                        contentColor = if (isSelected) PulseBackground else PulseTextPrimary,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                            Text(tab, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        when (activeTab) {
            "PRs" -> {
                if (prSummaries.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                            colors = CardDefaults.cardColors(containerColor = PulseSurface),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("Complete a workout to establish your first Personal Record!", color = PulseTextSecondary)
                            }
                        }
                    }
                } else {
                    items(prSummaries, key = { it.exercise.id }) { summary ->
                        ExercisePrCard(
                            summary = summary,
                            unitLabel = unitLabel
                        )
                    }
                }
            }

            "Milestones" -> {
                if (milestoneHistory.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                            colors = CardDefaults.cardColors(containerColor = PulseSurface),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("No milestones recorded yet.", color = PulseTextSecondary)
                            }
                        }
                    }
                } else {
                    items(milestoneHistory, key = { it.pr.id }) { milestone ->
                        MilestoneTimelineCard(
                            milestone = milestone,
                            unitLabel = unitLabel
                        )
                    }
                }
            }

            "1RM Calculator" -> {
                item {
                    OneRepMaxCalculator(
                        weight = calcWeight,
                        reps = calcReps,
                        unitLabel = unitLabel,
                        onWeightChange = { calcWeight = it },
                        onRepsChange = { calcReps = it }
                    )
                }
            }
        }
    }
}

@Composable
fun TrophyBadgeCounter(
    tier: String,
    count: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(color.copy(alpha = 0.15f), CircleShape)
                .border(1.dp, color.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.EmojiEvents, contentDescription = tier, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(count, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = PulseTextPrimary)
        Text(tier, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = color)
    }
}

@Composable
fun OneRepMaxCalculator(
    weight: String,
    reps: String,
    unitLabel: String,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit
) {
    val weightNum = weight.toDoubleOrNull() ?: 0.0
    val repsNum = reps.toIntOrNull() ?: 1

    val epley1RM = WorkoutRepository.calculateEstimated1RM(weightNum, repsNum)
    // Brzycki formula: weight / (1.0278 - 0.0278 * reps)
    val brzycki1RM = if (repsNum in 1..36) {
        val denom = 1.0278 - (0.0278 * repsNum)
        if (denom > 0) Math.round((weightNum / denom) * 10.0) / 10.0 else epley1RM
    } else epley1RM

    Card(
        modifier = Modifier.fillMaxWidth().testTag("one_rep_max_calculator"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PulseSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, PulseBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Calculate, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("ESTIMATED 1-REP MAX CALCULATOR", style = MaterialTheme.typography.titleMedium, color = ElectricCyan)
            }
            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = weight,
                    onValueChange = onWeightChange,
                    label = { Text("Weight ($unitLabel)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f).testTag("calc_weight_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = PulseBorder,
                        focusedTextColor = PulseTextPrimary,
                        unfocusedTextColor = PulseTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = reps,
                    onValueChange = onRepsChange,
                    label = { Text("Reps Completed") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag("calc_reps_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = PulseBorder,
                        focusedTextColor = PulseTextPrimary,
                        unfocusedTextColor = PulseTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Calculated 1RM Showcase
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PulseSurfaceElevated)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("EPLEY FORMULA", style = MaterialTheme.typography.labelSmall, color = PulseTextSecondary)
                        Text("${epley1RM.toInt()} $unitLabel", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = NeonLime)
                        Text("Standard Powerlifting", style = MaterialTheme.typography.labelSmall, color = PulseTextSecondary)
                    }

                    Box(modifier = Modifier.width(1.dp).height(40.dp).background(PulseBorder))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("BRZYCKI FORMULA", style = MaterialTheme.typography.labelSmall, color = PulseTextSecondary)
                        Text("${brzycki1RM.toInt()} $unitLabel", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = ElectricCyan)
                        Text("High Accuracy (1-10 reps)", style = MaterialTheme.typography.labelSmall, color = PulseTextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Training Load Percentage Table (95%, 90%, 85%, 80%, 75%, 70%)
            Text("TARGET TRAINING LOADS", style = MaterialTheme.typography.labelSmall, color = PulseTextSecondary)
            Spacer(modifier = Modifier.height(8.dp))

            listOf(95 to "1-2 reps", 90 to "3-4 reps", 85 to "5-6 reps", 80 to "7-8 reps", 75 to "9-10 reps", 70 to "12+ reps").forEach { (pct, targetReps) ->
                val load = Math.round((epley1RM * (pct / 100.0)) * 10.0) / 10.0
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("$pct% 1RM", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = PulseTextPrimary)
                    Text(targetReps, style = MaterialTheme.typography.bodySmall, color = PulseTextSecondary)
                    Text("$load $unitLabel", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = NeonLime)
                }
            }
        }
    }
}
