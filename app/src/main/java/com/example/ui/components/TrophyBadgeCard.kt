package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExercisePrSummary
import com.example.data.model.MilestoneHistoryItem
import com.example.data.model.PersonalRecord
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PrCelebrationDialog(
    pr: PersonalRecord?,
    onDismiss: () -> Unit
) {
    if (pr == null) return

    val infiniteTransition = rememberInfiniteTransition(label = "trophyPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "trophyScale"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = TrophyGold),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("pr_celebrate_confirm_button")
            ) {
                Text(
                    text = "AWESOME! KEEP CRUSHING IT",
                    fontWeight = FontWeight.Bold,
                    color = PulseBackground
                )
            }
        },
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .scale(scale)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(TrophyGold.copy(alpha = 0.35f), Color.Transparent)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Trophy",
                        tint = when (pr.badgeTier) {
                            "Diamond" -> TrophyDiamond
                            "Gold" -> TrophyGold
                            "Silver" -> TrophySilver
                            else -> TrophyBronze
                        },
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "NEW PERSONAL RECORD!",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = TrophyGold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PulseSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, PulseBorder)
                ) {
                    Text(
                        text = "${pr.badgeTier.uppercase()} TIER BADGE UNLOCKED",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = ElectricCyan
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "NEW BEST",
                            style = MaterialTheme.typography.labelSmall,
                            color = PulseTextSecondary
                        )
                        Text(
                            text = "${pr.value} kg",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = NeonLime
                        )
                        Text(
                            text = "${pr.reps} reps",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PulseTextSecondary
                        )
                    }

                    if (pr.previousValue > 0) {
                        Spacer(modifier = Modifier.width(24.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "PREVIOUS",
                                style = MaterialTheme.typography.labelSmall,
                                color = PulseTextSecondary
                            )
                            Text(
                                text = "${pr.previousValue} kg",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = PulseTextSecondary
                            )
                            val gain = pr.value - pr.previousValue
                            Text(
                                text = "+${String.format(Locale.US, "%.1f", gain)} kg gain",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = NeonOrange
                            )
                        }
                    }
                }
            }
        },
        containerColor = PulseSurface,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun ExercisePrCard(
    summary: ExercisePrSummary,
    unitLabel: String,
    modifier: Modifier = Modifier
) {
    val tierColor = when (summary.badgeTier) {
        "Diamond" -> TrophyDiamond
        "Gold" -> TrophyGold
        "Silver" -> TrophySilver
        else -> TrophyBronze
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("exercise_pr_card_${summary.exercise.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PulseSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, tierColor.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(tierColor.copy(alpha = 0.15f), CircleShape)
                        .border(1.dp, tierColor.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (summary.badgeTier) {
                            "Diamond" -> Icons.Default.AutoAwesome
                            "Gold" -> Icons.Default.EmojiEvents
                            "Silver" -> Icons.Default.WorkspacePremium
                            else -> Icons.Default.MilitaryTech
                        },
                        contentDescription = summary.badgeTier,
                        tint = tierColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = summary.exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = PulseTextPrimary
                    )
                    Text(
                        text = "${summary.exercise.category} • ${summary.badgeTier} Trophy",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PulseTextSecondary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${summary.bestWeight} $unitLabel",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = NeonLime
                )
                Text(
                    text = "1RM est: ${summary.best1RM.toInt()} $unitLabel",
                    style = MaterialTheme.typography.labelSmall,
                    color = ElectricCyan
                )
            }
        }
    }
}

@Composable
fun MilestoneTimelineCard(
    milestone: MilestoneHistoryItem,
    unitLabel: String,
    modifier: Modifier = Modifier
) {
    val tierColor = when (milestone.pr.badgeTier) {
        "Diamond" -> TrophyDiamond
        "Gold" -> TrophyGold
        "Silver" -> TrophySilver
        else -> TrophyBronze
    }
    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PulseSurfaceElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, PulseBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Milestone",
                    tint = tierColor,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = milestone.exerciseName,
                        style = MaterialTheme.typography.titleMedium,
                        color = PulseTextPrimary
                    )
                    Text(
                        text = "${sdf.format(Date(milestone.pr.date))} • ${milestone.category}",
                        style = MaterialTheme.typography.labelSmall,
                        color = PulseTextSecondary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${milestone.pr.value} $unitLabel",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = tierColor
                )
                if (milestone.pr.previousValue > 0) {
                    val gain = milestone.pr.value - milestone.pr.previousValue
                    Text(
                        text = "+${String.format(Locale.US, "%.1f", gain)} $unitLabel PR",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = NeonLime
                    )
                }
            }
        }
    }
}
