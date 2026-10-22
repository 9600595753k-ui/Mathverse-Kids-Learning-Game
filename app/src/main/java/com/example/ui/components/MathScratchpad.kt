package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class DoodleStroke(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float,
    val isEraser: Boolean = false
)

@Composable
fun MathScratchpadDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var strokes by remember { mutableStateOf(listOf<DoodleStroke>()) }
    var currentPoints by remember { mutableStateOf(listOf<Offset>()) }
    var selectedColor by remember { mutableStateOf(Color(0xFF2563EB)) } // Default Blue
    var strokeWidth by remember { mutableFloatStateOf(8f) }
    var isEraser by remember { mutableStateOf(false) }

    val colorPalette = listOf(
        Color(0xFF2563EB), // Blue
        Color(0xFFEA580C), // Orange
        Color(0xFF16A34A), // Green
        Color(0xFF9333EA), // Purple
        Color(0xFFE11D48), // Pink
        Color(0xFF1F2937)  // Charcoal
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.85f)
                .testTag("scratchpad_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("✏️", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Math Scratchpad & Rough Sheet",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Doodle, carry over numbers, or draw shapes with your finger!",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_scratchpad_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close Scratchpad")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Toolbar: Colors, Eraser, Undo, Clear
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Colors
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            colorPalette.forEach { color ->
                                val isSelected = !isEraser && selectedColor == color
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .then(
                                            if (isSelected) {
                                                Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                            } else Modifier
                                        )
                                        .clickable {
                                            selectedColor = color
                                            isEraser = false
                                        }
                                )
                            }

                            // Eraser Button
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isEraser) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                modifier = Modifier
                                    .clickable { isEraser = true }
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "🧹 Eraser",
                                    fontSize = 12.sp,
                                    fontWeight = if (isEraser) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                        }

                        // Undo and Clear
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    if (strokes.isNotEmpty()) {
                                        strokes = strokes.dropLast(1)
                                    }
                                },
                                enabled = strokes.isNotEmpty()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Undo,
                                    contentDescription = "Undo",
                                    tint = if (strokes.isNotEmpty()) MaterialTheme.colorScheme.primary else Color.Gray
                                )
                            }

                            IconButton(
                                onClick = {
                                    strokes = emptyList()
                                    currentPoints = emptyList()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Clear All",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Canvas Area (simulates graph paper / math grid paper)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFAF9F6)) // Warm off-white graph paper
                        .border(1.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(selectedColor, isEraser, strokeWidth) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        currentPoints = listOf(offset)
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        currentPoints = currentPoints + change.position
                                    },
                                    onDragEnd = {
                                        if (currentPoints.isNotEmpty()) {
                                            strokes = strokes + DoodleStroke(
                                                points = currentPoints,
                                                color = if (isEraser) Color(0xFFFAF9F6) else selectedColor,
                                                strokeWidth = if (isEraser) 32f else strokeWidth,
                                                isEraser = isEraser
                                            )
                                            currentPoints = emptyList()
                                        }
                                    },
                                    onDragCancel = {
                                        currentPoints = emptyList()
                                    }
                                )
                            }
                    ) {
                        // Draw faint graph paper grid lines for easy math alignment
                        val step = 32.dp.toPx()
                        for (x in 0..(size.width / step).toInt()) {
                            drawLine(
                                color = Color(0xFFE2E8F0).copy(alpha = 0.5f),
                                start = Offset(x * step, 0f),
                                end = Offset(x * step, size.height),
                                strokeWidth = 1f
                            )
                        }
                        for (y in 0..(size.height / step).toInt()) {
                            drawLine(
                                color = Color(0xFFE2E8F0).copy(alpha = 0.5f),
                                start = Offset(0f, y * step),
                                end = Offset(size.width, y * step),
                                strokeWidth = 1f
                            )
                        }

                        // Draw completed strokes
                        strokes.forEach { stroke ->
                            if (stroke.points.size > 1) {
                                val path = Path().apply {
                                    moveTo(stroke.points.first().x, stroke.points.first().y)
                                    for (i in 1 until stroke.points.size) {
                                        lineTo(stroke.points[i].x, stroke.points[i].y)
                                    }
                                }
                                drawPath(
                                    path = path,
                                    color = stroke.color,
                                    style = Stroke(
                                        width = stroke.strokeWidth,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }
                        }

                        // Draw live active stroke
                        if (currentPoints.size > 1) {
                            val activePath = Path().apply {
                                moveTo(currentPoints.first().x, currentPoints.first().y)
                                for (i in 1 until currentPoints.size) {
                                    lineTo(currentPoints[i].x, currentPoints[i].y)
                                }
                            }
                            drawPath(
                                path = activePath,
                                color = if (isEraser) Color(0xFFFAF9F6) else selectedColor,
                                style = Stroke(
                                    width = if (isEraser) 32f else strokeWidth,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }

                    // Watermark / Helper instruction when empty
                    if (strokes.isEmpty() && currentPoints.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "✍️ Write, carry-over, or draw here",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "e.g. 24 + 18 = 42",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
