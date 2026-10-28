package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BodyweightEntry
import com.example.data.model.VolumeBarStat
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.PulseBorder
import com.example.ui.theme.PulseSurface
import com.example.ui.theme.PulseSurfaceElevated
import com.example.ui.theme.PulseTextPrimary
import com.example.ui.theme.PulseTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VolumeProgressionBarChart(
    stats: List<VolumeBarStat>,
    unitLabel: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("volume_progression_chart"),
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
                Column {
                    Text(
                        text = "WORKOUT VOLUME PROGRESSION",
                        style = MaterialTheme.typography.titleMedium,
                        color = ElectricCyan
                    )
                    Text(
                        text = "Total tonnage lifted per session ($unitLabel)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PulseTextSecondary
                    )
                }
                val latestVol = stats.lastOrNull()?.volumeKg ?: 0.0
                Text(
                    text = "${latestVol.toInt()} $unitLabel",
                    style = MaterialTheme.typography.headlineMedium,
                    color = NeonLime
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (stats.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No completed workouts yet", color = PulseTextSecondary)
                }
            } else {
                val maxVolume = stats.maxOfOrNull { it.volumeKg }?.coerceAtLeast(100.0) ?: 100.0

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height - 30.dp.toPx()
                    val barCount = stats.size
                    val slotWidth = canvasWidth / barCount
                    val barWidth = (slotWidth * 0.55f).coerceAtMost(36.dp.toPx())

                    // Horizontal gridlines (25%, 50%, 75%, 100%)
                    for (i in 1..3) {
                        val y = canvasHeight * (1f - (i * 0.25f))
                        drawLine(
                            color = PulseBorder.copy(alpha = 0.5f),
                            start = Offset(0f, y),
                            end = Offset(canvasWidth, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    stats.forEachIndexed { index, stat ->
                        val barHeight = ((stat.volumeKg / maxVolume).toFloat() * canvasHeight).coerceAtLeast(6.dp.toPx())
                        val x = (index * slotWidth) + (slotWidth - barWidth) / 2f
                        val y = canvasHeight - barHeight

                        // Glowing gradient bar
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(ElectricCyan, NeonLime),
                                startY = y,
                                endY = canvasHeight
                            ),
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )

                        // Date label below
                        drawContext.canvas.nativeCanvas.apply {
                            val paint = android.graphics.Paint().apply {
                                color = android.graphics.Color.parseColor("#94A3B8")
                                textSize = 11.sp.toPx()
                                textAlign = android.graphics.Paint.Align.CENTER
                                isAntiAlias = true
                            }
                            drawText(
                                stat.dayOrWeekLabel,
                                x + (barWidth / 2f),
                                size.height - 6.dp.toPx(),
                                paint
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BodyweightLineChart(
    entries: List<BodyweightEntry>,
    unitLabel: String,
    onLogWeightClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sorted = entries.sortedBy { it.date }
    val latestWeight = sorted.lastOrNull()?.weightKg ?: 0.0
    val firstWeight = sorted.firstOrNull()?.weightKg ?: 0.0
    val weightDelta = latestWeight - firstWeight

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("bodyweight_progression_chart"),
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
                Column {
                    Text(
                        text = "BODYWEIGHT TRACKER",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonLime
                    )
                    Text(
                        text = if (weightDelta >= 0) "+${String.format(Locale.US, "%.1f", weightDelta)} $unitLabel overall"
                        else "${String.format(Locale.US, "%.1f", weightDelta)} $unitLabel overall",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (weightDelta <= 0) NeonLime else NeonOrange
                    )
                }

                Surface(
                    onClick = onLogWeightClick,
                    shape = RoundedCornerShape(8.dp),
                    color = NeonLime.copy(alpha = 0.15f),
                    contentColor = NeonLime,
                    modifier = Modifier.testTag("log_bodyweight_chip")
                ) {
                    Text(
                        text = "+ Log Weight",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (sorted.size < 2) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Log at least 2 entries to display progression trend", color = PulseTextSecondary)
                }
            } else {
                val minWeight = (sorted.minOf { it.weightKg } - 1.0).coerceAtLeast(0.0)
                val maxWeight = sorted.maxOf { it.weightKg } + 1.0
                val range = (maxWeight - minWeight).coerceAtLeast(1.0)
                val sdf = SimpleDateFormat("MMM d", Locale.getDefault())

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height - 24.dp.toPx()
                    val pointCount = sorted.size
                    val stepX = canvasWidth / (pointCount - 1).coerceAtLeast(1)

                    val points = sorted.mapIndexed { index, entry ->
                        val x = index * stepX
                        val normalized = ((entry.weightKg - minWeight) / range).toFloat()
                        val y = canvasHeight * (1f - normalized)
                        Offset(x, y)
                    }

                    // Draw area gradient path under the curve
                    val areaPath = Path().apply {
                        moveTo(points.first().x, canvasHeight)
                        points.forEach { lineTo(it.x, it.y) }
                        lineTo(points.last().x, canvasHeight)
                        close()
                    }

                    drawPath(
                        path = areaPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(NeonLime.copy(alpha = 0.35f), Color.Transparent),
                            startY = 0f,
                            endY = canvasHeight
                        )
                    )

                    // Draw line
                    val linePath = Path().apply {
                        moveTo(points.first().x, points.first().y)
                        for (i in 1 until points.size) {
                            lineTo(points[i].x, points[i].y)
                        }
                    }

                    drawPath(
                        path = linePath,
                        color = NeonLime,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw glowing point circles
                    points.forEachIndexed { i, pt ->
                        drawCircle(color = PulseSurface, radius = 5.dp.toPx(), center = pt)
                        drawCircle(color = NeonLime, radius = 3.dp.toPx(), center = pt)

                        // Date label on first and last point
                        if (i == 0 || i == points.size - 1) {
                            drawContext.canvas.nativeCanvas.apply {
                                val paint = android.graphics.Paint().apply {
                                    color = android.graphics.Color.parseColor("#94A3B8")
                                    textSize = 10.sp.toPx()
                                    textAlign = if (i == 0) android.graphics.Paint.Align.LEFT else android.graphics.Paint.Align.RIGHT
                                    isAntiAlias = true
                                }
                                val label = sdf.format(Date(sorted[i].date))
                                drawText(label, pt.x, size.height - 4.dp.toPx(), paint)
                            }
                        }
                    }
                }
            }
        }
    }
}
