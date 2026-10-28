package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WorkoutSet
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

@Composable
fun WorkoutSetRow(
    set: WorkoutSet,
    unitLabel: String,
    displayedWeight: String,
    onWeightDelta: (Double) -> Unit,
    onRepsDelta: (Int) -> Unit,
    onSetTypeChange: (String) -> Unit,
    onToggleCompleted: () -> Unit,
    onDeleteSet: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showTypeMenu by remember { mutableStateOf(false) }

    val checkmarkBg by animateColorAsState(
        targetValue = if (set.isCompleted) NeonLime else PulseSurfaceElevated,
        label = "setCheckColor"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("workout_set_row_${set.setNumber}"),
        shape = RoundedCornerShape(12.dp),
        color = if (set.isCompleted) NeonLime.copy(alpha = 0.08f) else PulseSurface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (set.isCompleted) NeonLime.copy(alpha = 0.4f) else PulseBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Set number & type badge
            Box {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showTypeMenu = true }
                        .testTag("set_type_badge_${set.setNumber}"),
                    color = when (set.setType) {
                        "Warmup" -> NeonOrange.copy(alpha = 0.2f)
                        "Drop" -> ElectricCyan.copy(alpha = 0.2f)
                        "Failure" -> PulseRed.copy(alpha = 0.2f)
                        else -> PulseSurfaceElevated
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "#${set.setNumber}",
                            style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
                            color = PulseTextPrimary
                        )
                        if (set.setType != "Normal") {
                            Text(
                                text = set.setType.take(1).uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = when (set.setType) {
                                    "Warmup" -> NeonOrange
                                    "Drop" -> ElectricCyan
                                    "Failure" -> PulseRed
                                    else -> PulseTextSecondary
                                }
                            )
                        }
                    }
                }

                DropdownMenu(
                    expanded = showTypeMenu,
                    onDismissRequest = { showTypeMenu = false },
                    modifier = Modifier.background(PulseSurfaceElevated)
                ) {
                    listOf("Normal", "Warmup", "Drop", "Failure").forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type, color = PulseTextPrimary) },
                            onClick = {
                                onSetTypeChange(type)
                                showTypeMenu = false
                            }
                        )
                    }
                }
            }

            // Weight counter with quick tap +/- buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                IconButton(
                    onClick = { onWeightDelta(-2.5) },
                    modifier = Modifier.size(32.dp).testTag("weight_minus_${set.setNumber}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease weight",
                        tint = ElectricCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(62.dp)
                ) {
                    Text(
                        text = displayedWeight,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PulseTextPrimary
                    )
                    Text(
                        text = unitLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = PulseTextSecondary
                    )
                }

                IconButton(
                    onClick = { onWeightDelta(2.5) },
                    modifier = Modifier.size(32.dp).testTag("weight_plus_${set.setNumber}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase weight",
                        tint = ElectricCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Reps counter with quick tap +/- buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                IconButton(
                    onClick = { onRepsDelta(-1) },
                    modifier = Modifier.size(32.dp).testTag("reps_minus_${set.setNumber}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease reps",
                        tint = NeonLime,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(44.dp)
                ) {
                    Text(
                        text = "${set.reps}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PulseTextPrimary
                    )
                    Text(
                        text = "reps",
                        style = MaterialTheme.typography.labelSmall,
                        color = PulseTextSecondary
                    )
                }

                IconButton(
                    onClick = { onRepsDelta(1) },
                    modifier = Modifier.size(32.dp).testTag("reps_plus_${set.setNumber}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase reps",
                        tint = NeonLime,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Complete checkmark button or PR Trophy badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (set.isPr) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Personal Record",
                        tint = TrophyGold,
                        modifier = Modifier.size(20.dp).padding(end = 4.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(checkmarkBg)
                        .border(1.dp, if (set.isCompleted) NeonLime else PulseBorder, RoundedCornerShape(10.dp))
                        .clickable { onToggleCompleted() }
                        .testTag("toggle_set_complete_${set.setNumber}"),
                    contentAlignment = Alignment.Center
                ) {
                    if (set.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed set",
                            tint = PulseBackground,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Text(
                            text = "✓",
                            color = PulseTextSecondary.copy(alpha = 0.5f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(
                    onClick = onDeleteSet,
                    modifier = Modifier.size(32.dp).testTag("delete_set_${set.setNumber}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete set",
                        tint = PulseTextSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
