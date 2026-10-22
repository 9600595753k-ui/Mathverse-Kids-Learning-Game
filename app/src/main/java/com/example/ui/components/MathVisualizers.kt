package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VisualData
import kotlin.math.cos
import kotlin.math.sin

/**
 * Interactive Counters Visualizer
 * - Solves the overflow issue (e.g. 9 balloons previously clipped to 7).
 * - Wraps items into rows of 5 max with responsive sizing.
 * - For subtraction: kids can tap balloons/cookies to "POP" or "EAT" them with an animation!
 * - For addition: kids tap each item to reveal its count badge (1, 2, 3...)!
 */
@Composable
fun CountersVisualizer(
    data: VisualData,
    modifier: Modifier = Modifier
) {
    // Track popped/eaten item indices for subtraction
    var poppedIndices by remember(data) { mutableStateOf(setOf<Int>()) }
    // Track counted items for addition (indices in basket 1 and basket 2)
    var countedBasket1 by remember(data) { mutableStateOf(setOf<Int>()) }
    var countedBasket2 by remember(data) { mutableStateOf(setOf<Int>()) }

    val hasTwoBaskets = data.count2 > 0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Interactive instruction banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (hasTwoBaskets) "🧺 Tap items in both baskets to count the sum!"
                else if (data.itemEmoji == "🎈") "🎈 Tap balloons to POP them!"
                else "✨ Tap items to count them!",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (poppedIndices.isNotEmpty() || countedBasket1.isNotEmpty() || countedBasket2.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.clickable {
                        poppedIndices = emptySet()
                        countedBasket1 = emptySet()
                        countedBasket2 = emptySet()
                    }
                ) {
                    Text(
                        text = "Reset 🔄",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (hasTwoBaskets) {
            // TWO BASKETS DISPLAY FOR ADDITION (e.g. 3 apples + 2 apples)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // BASKET 1
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🧺 Basket 1",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        // Items in Basket 1 (wrapped in rows of 3)
                        val b1Items = (0 until data.count1).toList()
                        b1Items.chunked(3).forEach { rowIndices ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(vertical = 3.dp)
                            ) {
                                rowIndices.forEach { index ->
                                    val isCounted = countedBasket1.contains(index)
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isCounted) MaterialTheme.colorScheme.primaryContainer else Color(0xFFFEF3C7)
                                            )
                                            .border(
                                                width = if (isCounted) 2.dp else 1.dp,
                                                color = if (isCounted) MaterialTheme.colorScheme.primary else Color(0xFFF59E0B),
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                countedBasket1 = if (isCounted) countedBasket1 - index else countedBasket1 + index
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            if (isCounted) {
                                                Text(
                                                    text = "${index + 1}",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                            Text(text = data.itemEmoji, fontSize = if (isCounted) 18.sp else 22.sp)
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${data.count1} ${data.itemEmoji}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // PLUS SIGN
                Text(
                    text = "➕",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                // BASKET 2
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🧺 Basket 2",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        // Items in Basket 2 (wrapped in rows of 3)
                        val b2Items = (0 until data.count2).toList()
                        b2Items.chunked(3).forEach { rowIndices ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(vertical = 3.dp)
                            ) {
                                rowIndices.forEach { index ->
                                    val isCounted = countedBasket2.contains(index)
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isCounted) MaterialTheme.colorScheme.secondaryContainer else Color(0xFFE0F2FE)
                                            )
                                            .border(
                                                width = if (isCounted) 2.dp else 1.dp,
                                                color = if (isCounted) MaterialTheme.colorScheme.secondary else Color(0xFF0284C7),
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                countedBasket2 = if (isCounted) countedBasket2 - index else countedBasket2 + index
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            if (isCounted) {
                                                Text(
                                                    text = "${data.count1 + index + 1}",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.secondary
                                                )
                                            }
                                            Text(text = data.itemEmoji, fontSize = if (isCounted) 18.sp else 22.sp)
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${data.count2} ${data.itemEmoji}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sum status pill
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Text(
                    text = "Basket 1 (${data.count1}) + Basket 2 (${data.count2}) = ${data.count1 + data.count2} Total ${data.itemEmoji}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        } else {
            // SINGLE GROUP DISPLAY (e.g. 9 Balloons with tap to pop)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val totalItems = (0 until data.count1).toList()
                // Max 5 per row ensures all 9, 10, or 15 items fit with zero horizontal clipping!
                val chunkedRows = totalItems.chunked(5)

                chunkedRows.forEach { rowIndices ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        rowIndices.forEach { index ->
                            val isPopped = poppedIndices.contains(index)
                            val isCounted = countedBasket1.contains(index)

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isPopped) Color(0xFFFEE2E2)
                                        else if (isCounted) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surface
                                    )
                                    .border(
                                        width = if (isCounted) 2.dp else 1.dp,
                                        color = if (isPopped) Color(0xFFEF4444) else if (isCounted) MaterialTheme.colorScheme.primary else Color(0xFFCBD5E1),
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        if (data.itemEmoji == "🎈" || data.itemEmoji == "🍪") {
                                            poppedIndices = if (isPopped) poppedIndices - index else poppedIndices + index
                                        }
                                        countedBasket1 = if (isCounted) countedBasket1 - index else countedBasket1 + index
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isPopped) {
                                    Text(text = "💥", fontSize = 22.sp)
                                } else {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        if (isCounted) {
                                            Text(
                                                text = "${index + 1}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Text(
                                            text = data.itemEmoji,
                                            fontSize = if (isCounted) 20.sp else 24.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Subtraction live counter
            val activeCount = data.count1 - poppedIndices.size
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Total: ${data.count1} ${data.itemEmoji}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    if (poppedIndices.isNotEmpty()) {
                        Text("•", color = Color.Gray)
                        Text(
                            text = "Popped: ${poppedIndices.size} 💥",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFFDC2626)
                        )
                        Text("•", color = Color.Gray)
                        Text(
                            text = "Left: $activeCount ${data.itemEmoji}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = Color(0xFF166534)
                        )
                    } else if (countedBasket1.isNotEmpty()) {
                        Text("•", color = Color.Gray)
                        Text(
                            text = "Counted: ${countedBasket1.size} / ${data.count1}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Interactive Pizza Fraction Slicer & Eater
 * Tap on slices to eat / highlight them!
 */
@Composable
fun PizzaFractionVisualizer(
    numerator: Int,
    denominator: Int,
    modifier: Modifier = Modifier
) {
    var selectedSlices by remember(numerator, denominator) {
        mutableStateOf((0 until numerator).toSet())
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFFEF3C7))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🍕 Pizza Fractions: ${selectedSlices.size}/$denominator slices",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF92400E)
            )
            Text(
                text = "Tap slice to eat/add",
                fontSize = 11.sp,
                color = Color(0xFFB45309)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .size(170.dp)
                .clickable {
                    // Tap advances selected slices
                    val next = (selectedSlices.size + 1) % (denominator + 1)
                    selectedSlices = (0 until next).toSet()
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = size.minDimension / 2.2f
                val center = Offset(size.width / 2f, size.height / 2f)

                // Outer Crust
                drawCircle(
                    color = Color(0xFFD97706),
                    radius = radius + 6f,
                    center = center
                )

                val sweepAngle = 360f / denominator.coerceAtLeast(1)

                for (i in 0 until denominator) {
                    val startAngle = i * sweepAngle - 90f
                    val isShaded = selectedSlices.contains(i)

                    val sliceColor = if (isShaded) Color(0xFFFBBF24) else Color(0xFFFFF1F2)
                    drawArc(
                        color = sliceColor,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2f, radius * 2f)
                    )

                    // Slice border
                    drawArc(
                        color = Color(0xFF78350F),
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2f, radius * 2f),
                        style = Stroke(width = 3.5f)
                    )

                    // Pepperoni on shaded slice
                    if (isShaded) {
                        val midAngleRad = Math.toRadians((startAngle + sweepAngle / 2f).toDouble())
                        val pepX = (center.x + (radius * 0.6f) * cos(midAngleRad)).toFloat()
                        val pepY = (center.y + (radius * 0.6f) * sin(midAngleRad)).toFloat()
                        drawCircle(
                            color = Color(0xFFDC2626),
                            radius = 8f,
                            center = Offset(pepX, pepY)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${selectedSlices.size} of $denominator equal slices selected",
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = Color(0xFF92400E)
        )
    }
}

/**
 * Interactive Geometric Shape with tap-to-count corners!
 */
@Composable
fun ShapeGeometryVisualizer(
    shapeType: String,
    sides: Int,
    modifier: Modifier = Modifier
) {
    var countedCorners by remember(shapeType) { mutableStateOf(setOf<Int>()) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🔷 Shape: $shapeType",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Tap to count corners!",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .size(160.dp)
                .clickable {
                    // Tap to count next corner
                    if (countedCorners.size < sides) {
                        countedCorners = countedCorners + countedCorners.size
                    } else {
                        countedCorners = emptySet()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.minDimension / 2.5f

                val path = Path()
                val angleStep = (2 * Math.PI) / sides

                val points = mutableListOf<Offset>()
                for (i in 0 until sides) {
                    val angle = i * angleStep - Math.PI / 2
                    val x = (center.x + radius * cos(angle)).toFloat()
                    val y = (center.y + radius * sin(angle)).toFloat()
                    points.add(Offset(x, y))
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()

                // Fill Shape
                drawPath(
                    path = path,
                    color = Color(0xFF60A5FA).copy(alpha = 0.35f)
                )

                // Outline
                drawPath(
                    path = path,
                    color = Color(0xFF2563EB),
                    style = Stroke(width = 5f, cap = StrokeCap.Round)
                )

                // Draw vertex corner dots
                points.forEachIndexed { i, pt ->
                    val isCounted = countedCorners.contains(i)
                    drawCircle(
                        color = if (isCounted) Color(0xFF10B981) else Color(0xFFF59E0B),
                        radius = if (isCounted) 10f else 8f,
                        center = pt
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 4f,
                        center = pt
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Text(
                text = "Corners Counted: ${countedCorners.size} of $sides",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (countedCorners.size == sides) Color(0xFF166534) else MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

/**
 * Interactive Analog Clock Visualizer
 */
@Composable
fun AnalogClockVisualizer(
    hour: Int,
    minute: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFE0F2FE))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "⏰ Analog Clock Face",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color(0xFF0369A1)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.size(160.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.minDimension / 2.2f

                // Outer clock rim
                drawCircle(color = Color(0xFF0284C7), radius = radius, center = center)
                drawCircle(color = Color.White, radius = radius - 6f, center = center)

                // Center hub
                drawCircle(color = Color(0xFF0F172A), radius = 6f, center = center)

                // 12 hour ticks
                for (h in 1..12) {
                    val angleRad = Math.toRadians((h * 30 - 90).toDouble())
                    val tickOuter = Offset(
                        (center.x + (radius - 12f) * cos(angleRad)).toFloat(),
                        (center.y + (radius - 12f) * sin(angleRad)).toFloat()
                    )
                    val tickInner = Offset(
                        (center.x + (radius - 22f) * cos(angleRad)).toFloat(),
                        (center.y + (radius - 22f) * sin(angleRad)).toFloat()
                    )
                    drawLine(
                        color = Color(0xFF0F172A),
                        start = tickInner,
                        end = tickOuter,
                        strokeWidth = if (h % 3 == 0) 4f else 2f
                    )
                }

                // Hour Hand (Shorter, Thicker)
                val hourAngle = Math.toRadians(((hour % 12 + minute / 60f) * 30 - 90).toDouble())
                val hourEnd = Offset(
                    (center.x + (radius * 0.5f) * cos(hourAngle)).toFloat(),
                    (center.y + (radius * 0.5f) * sin(hourAngle)).toFloat()
                )
                drawLine(
                    color = Color(0xFF1E293B),
                    start = center,
                    end = hourEnd,
                    strokeWidth = 6f,
                    cap = StrokeCap.Round
                )

                // Minute Hand (Longer, Red)
                val minAngle = Math.toRadians((minute * 6 - 90).toDouble())
                val minEnd = Offset(
                    (center.x + (radius * 0.75f) * cos(minAngle)).toFloat(),
                    (center.y + (radius * 0.75f) * sin(minAngle)).toFloat()
                )
                drawLine(
                    color = Color(0xFFDC2626),
                    start = center,
                    end = minEnd,
                    strokeWidth = 3.5f,
                    cap = StrokeCap.Round
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        val formattedMin = if (minute < 10) "0$minute" else "$minute"
        Text(
            text = "Time Displayed: $hour:$formattedMin",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFF0369A1)
        )
    }
}

/**
 * Interactive Money & Coins Visualizer
 */
@Composable
fun MoneyCoinsVisualizer(
    summary: String,
    coinValues: List<Int>,
    modifier: Modifier = Modifier
) {
    var savedCoins by remember(coinValues) { mutableStateOf(setOf<Int>()) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFFEF3C7))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🪙 Piggy Bank Coins: $summary",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color(0xFF92400E)
        )
        Text(
            text = "Tap each coin to put in the piggy bank!",
            fontSize = 11.sp,
            color = Color(0xFFB45309)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            coinValues.forEachIndexed { idx, value ->
                val isInBank = savedCoins.contains(idx)
                val (name, sizeDp, coinColor) = when (value) {
                    25 -> Triple("Quarter\n25¢", 58.dp, Color(0xFFE2E8F0))
                    10 -> Triple("Dime\n10¢", 44.dp, Color(0xFFCBD5E1))
                    5 -> Triple("Nickel\n5¢", 50.dp, Color(0xFF94A3B8))
                    else -> Triple("Penny\n1¢", 46.dp, Color(0xFFFDBA74))
                }

                Box(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .size(sizeDp)
                        .clip(CircleShape)
                        .background(if (isInBank) Color(0xFF10B981) else coinColor)
                        .border(2.dp, Color(0xFF78350F), CircleShape)
                        .clickable {
                            savedCoins = if (isInBank) savedCoins - idx else savedCoins + idx
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isInBank) "🐷\n+$value¢" else name,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = if (isInBank) Color.White else Color(0xFF1E293B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        val currentTotal = coinValues.filterIndexed { idx, _ -> savedCoins.contains(idx) }.sum()
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFFFFBEB)
        ) {
            Text(
                text = "Total in Piggy Bank: $currentTotal¢ (Total Value: ${coinValues.sum()}¢)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF92400E),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

/**
 * Interactive Number Pattern Train
 */
@Composable
fun PatternTrainVisualizer(
    pattern: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFEDE9FE))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🚂 Number Pattern Express",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color(0xFF6D28D9)
        )
        Text(
            text = "Find the missing number in the train carriages!",
            fontSize = 11.sp,
            color = Color(0xFF7C3AED)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🚂", fontSize = 32.sp)
            Spacer(modifier = Modifier.width(4.dp))
            pattern.forEach { num ->
                val isMystery = num == "?"
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isMystery) Color(0xFFFBBF24) else Color(0xFF8B5CF6),
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(width = 44.dp, height = 48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = num,
                            color = if (isMystery) Color(0xFF78350F) else Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NumberLineVisualizer(
    data: VisualData,
    modifier: Modifier = Modifier
) {
    var frogPos by remember(data) { mutableStateOf(data.numberLineHop1) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🐸 Frog Jump: ${data.numberLineHop1} + ${data.numberLineHop2} = ?",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Text("Tap to Hop 🐾", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
        }
        Spacer(modifier = Modifier.height(12.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clickable {
                    // Hop frog to destination or back
                    frogPos = if (frogPos == data.numberLineHop1) {
                        data.numberLineHop1 + data.numberLineHop2
                    } else {
                        data.numberLineHop1
                    }
                }
        ) {
            val startX = 40f
            val endX = size.width - 40f
            val y = size.height - 25f
            val totalSteps = (data.numberLineEnd - data.numberLineStart).coerceAtLeast(1)
            val stepWidth = (endX - startX) / totalSteps

            // Base line
            drawLine(
                color = Color(0xFF0284C7),
                start = Offset(startX, y),
                end = Offset(endX, y),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )

            // Tick marks
            for (step in data.numberLineStart..data.numberLineEnd) {
                val tickX = startX + step * stepWidth
                drawLine(
                    color = Color(0xFF0369A1),
                    start = Offset(tickX, y - 10f),
                    end = Offset(tickX, y + 10f),
                    strokeWidth = 2.5f
                )
            }

            // Hop Arc
            val hopStartX = startX + data.numberLineHop1 * stepWidth
            val hopTarget = data.numberLineHop1 + data.numberLineHop2
            val hopEndX = startX + hopTarget * stepWidth
            val arcControlY = y - 55f

            val hopPath = Path().apply {
                moveTo(hopStartX, y)
                quadraticBezierTo((hopStartX + hopEndX) / 2f, arcControlY, hopEndX, y)
            }
            drawPath(
                path = hopPath,
                color = Color(0xFF10B981),
                style = Stroke(width = 4f, cap = StrokeCap.Round)
            )

            // Frog Marker at current pos
            val frogX = startX + frogPos * stepWidth
            drawCircle(color = Color(0xFF10B981), radius = 10f, center = Offset(frogX, y))
            drawCircle(color = Color.White, radius = 5f, center = Offset(frogX, y))
        }
    }
}

@Composable
fun ArrayGridVisualizer(
    data: VisualData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFEDE9FE))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Multiplication Array: ${data.gridRows} rows × ${data.gridCols} columns",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color(0xFF6D28D9)
        )
        Spacer(modifier = Modifier.height(10.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            repeat(data.gridRows) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(data.gridCols) {
                        Text(text = data.itemEmoji, fontSize = 22.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ConfettiCelebration(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val colors = listOf(
            Color(0xFFF59E0B),
            Color(0xFF10B981),
            Color(0xFF3B82F6),
            Color(0xFFEC4899),
            Color(0xFF8B5CF6)
        )

        val randomOffsets = listOf(
            Offset(50f, (offsetY * 1.1f) % size.height),
            Offset(120f, (offsetY * 0.9f + 50) % size.height),
            Offset(200f, (offsetY * 1.3f + 100) % size.height),
            Offset(280f, (offsetY * 0.8f + 150) % size.height),
            Offset(350f, (offsetY * 1.2f + 80) % size.height),
            Offset(420f, (offsetY * 1.0f + 200) % size.height),
            Offset(500f, (offsetY * 1.4f + 30) % size.height)
        )

        randomOffsets.forEachIndexed { index, pos ->
            drawCircle(
                color = colors[index % colors.size],
                radius = 8f,
                center = pos
            )
        }
    }
}
