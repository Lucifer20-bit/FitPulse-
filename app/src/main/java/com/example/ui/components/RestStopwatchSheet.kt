package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun RestStopwatchFloatingBar(
    secondsRemaining: Int,
    totalSeconds: Int,
    isRunning: Boolean,
    onTogglePlay: () -> Unit,
    onAddSeconds: (Int) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = secondsRemaining > 0,
        modifier = modifier
    ) {
        val progress = if (totalSeconds > 0) {
            (secondsRemaining.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)
        } else 0f
        val animatedProgress by animateFloatAsState(targetValue = progress, label = "restProgress")

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("rest_stopwatch_floating_bar"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = PulseSurfaceElevated),
            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Mini circular ring + countdown text
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(46.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(46.dp)) {
                            drawArc(
                                color = PulseBorder,
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawArc(
                                brush = Brush.sweepGradient(
                                    listOf(ElectricCyan, NeonLime, ElectricCyan)
                                ),
                                startAngle = -90f,
                                sweepAngle = animatedProgress * 360f,
                                useCenter = false,
                                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        Text(
                            text = "${secondsRemaining}s",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (secondsRemaining <= 3) NeonOrange else ElectricCyan
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "REST TIMER",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = NeonLime
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Sound cues enabled",
                                tint = NeonLime,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = formatTime(secondsRemaining),
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                            color = PulseTextPrimary
                        )
                    }
                }

                // Action chips (+30s, Pause/Play, Stop)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onAddSeconds(30) }
                            .testTag("rest_add_30s_button"),
                        color = ElectricCyan.copy(alpha = 0.15f),
                        contentColor = ElectricCyan
                    ) {
                        Text(
                            text = "+30s",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    IconButton(
                        onClick = onTogglePlay,
                        modifier = Modifier
                            .size(36.dp)
                            .background(if (isRunning) NeonOrange.copy(alpha = 0.2f) else NeonLime.copy(alpha = 0.2f), CircleShape)
                            .testTag("rest_toggle_play_button")
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isRunning) "Pause timer" else "Resume timer",
                            tint = if (isRunning) NeonOrange else NeonLime,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("rest_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Stop rest timer",
                            tint = PulseTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RestStopwatchModal(
    secondsRemaining: Int,
    totalSeconds: Int,
    isRunning: Boolean,
    onStartTimer: (Int) -> Unit,
    onTogglePlay: () -> Unit,
    onAddSeconds: (Int) -> Unit,
    onClose: () -> Unit
) {
    val progress = if (totalSeconds > 0) {
        (secondsRemaining.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)
    } else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "modalRestProgress")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PulseSurface, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(24.dp)
            .testTag("rest_stopwatch_modal"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ACTIVE REST TIMER",
                    style = MaterialTheme.typography.titleMedium,
                    color = ElectricCyan
                )
                Text(
                    text = "Sound & haptic alert at countdown end",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PulseTextSecondary
                )
            }
            IconButton(onClick = onClose, modifier = Modifier.testTag("close_rest_modal_button")) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = PulseTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Large countdown circle
        Box(
            modifier = Modifier.size(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(180.dp)) {
                drawArc(
                    color = PulseBorder,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(ElectricCyan, NeonLime, NeonOrange, ElectricCyan)
                    ),
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formatTime(secondsRemaining),
                    style = MaterialTheme.typography.displayMedium,
                    color = if (secondsRemaining in 1..3) NeonOrange else PulseTextPrimary
                )
                Text(
                    text = if (isRunning) "RESTING" else "PAUSED",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isRunning) NeonLime else PulseTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Preset Chips: 30s, 60s, 90s, 120s, 180s
        Text(
            text = "PRESET DURATIONS",
            style = MaterialTheme.typography.labelSmall,
            color = PulseTextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(30, 60, 90, 120, 180).forEach { sec ->
                val isSelected = totalSeconds == sec && secondsRemaining > 0
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onStartTimer(sec) }
                        .testTag("rest_preset_${sec}s"),
                    color = if (isSelected) ElectricCyan else PulseSurfaceElevated,
                    contentColor = if (isSelected) PulseBackground else PulseTextPrimary,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${sec}s",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Delta buttons (+15s, -15s, +30s)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            FilledTonalButton(
                onClick = { onAddSeconds(-15) },
                modifier = Modifier.padding(horizontal = 6.dp).testTag("rest_minus_15s_button")
            ) {
                Text("-15s")
            }
            FilledTonalButton(
                onClick = { onAddSeconds(15) },
                modifier = Modifier.padding(horizontal = 6.dp).testTag("rest_plus_15s_button")
            ) {
                Text("+15s")
            }
            FilledTonalButton(
                onClick = { onAddSeconds(30) },
                modifier = Modifier.padding(horizontal = 6.dp).testTag("rest_plus_30s_button")
            ) {
                Text("+30s")
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format(java.util.Locale.US, "%02d:%02d", m, s)
}
