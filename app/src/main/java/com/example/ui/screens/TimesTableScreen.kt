package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MathViewModel
import com.example.ui.components.ConfettiCelebration
import com.example.util.TTSHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimesTableScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val tts = remember { TTSHelper(context) }
    DisposableEffect(Unit) {
        onDispose { tts.shutdown() }
    }

    var selectedMultiplier by remember { mutableIntStateOf(3) }
    var practiceMode by remember { mutableStateOf(false) }
    var currentMultiplierIndex by remember { mutableIntStateOf(1) }
    var practiceScore by remember { mutableIntStateOf(0) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var showCelebration by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("✖️ Times Table Explorer", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            practiceMode = !practiceMode
                            currentMultiplierIndex = 1
                            practiceScore = 0
                            feedbackMessage = null
                        }
                    ) {
                        Text(
                            text = if (practiceMode) "📖 View Table" else "🎯 Practice Quiz",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Header
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF6366F1),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF4F46E5), Color(0xFF06B6D4))
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Master Multiplication!",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Tap numbers 1 to 12 to see dot arrays and hear times tables spoken aloud!",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            Text("✖️", fontSize = 42.sp)
                        }
                    }
                }
            }

            // Multiplier selector row (1 to 12)
            item {
                Text(
                    text = "Select Table Number:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items((1..12).toList()) { num ->
                        val isSelected = num == selectedMultiplier
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .size(44.dp)
                                .clickable {
                                    selectedMultiplier = num
                                    currentMultiplierIndex = 1
                                    feedbackMessage = null
                                    tts.speak("Table of $num")
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "$num",
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            if (!practiceMode) {
                // Table View with Dot Array Visualizer
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Dot Array: $selectedMultiplier × 4 = ${selectedMultiplier * 4}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                IconButton(
                                    onClick = {
                                        tts.speak("$selectedMultiplier times 4 is ${selectedMultiplier * 4}")
                                    }
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Listen")
                                }
                            }

                            // Visual Dot Array Matrix (e.g. Rows of dots)
                            val displayCols = 4
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                for (r in 1..selectedMultiplier.coerceAtMost(8)) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        for (c in 1..displayCols) {
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF3B82F6))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // List of table facts (1x to 12x)
                items((1..12).toList()) { step ->
                    val product = selectedMultiplier * step
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                tts.speak("$selectedMultiplier times $step is $product")
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$selectedMultiplier × $step",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = "= $product",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            IconButton(
                                onClick = {
                                    tts.speak("$selectedMultiplier times $step is $product")
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Speak",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            } else {
                // Interactive Practice Quiz Mode for this Table
                item {
                    val targetProduct = selectedMultiplier * currentMultiplierIndex
                    val wrong1 = (targetProduct + selectedMultiplier).takeIf { it != targetProduct } ?: (targetProduct + 2)
                    val wrong2 = (targetProduct - selectedMultiplier).coerceAtLeast(1).takeIf { it != targetProduct } ?: (targetProduct + 5)
                    val options = remember(selectedMultiplier, currentMultiplierIndex) {
                        listOf(targetProduct, wrong1, wrong2).shuffled()
                    }

                    if (showCelebration) {
                        ConfettiCelebration()
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Question $currentMultiplierIndex of 12",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "$selectedMultiplier × $currentMultiplierIndex = ?",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            options.forEach { opt ->
                                OutlinedButton(
                                    onClick = {
                                        if (opt == targetProduct) {
                                            feedbackMessage = "🎉 Brilliant! $selectedMultiplier × $currentMultiplierIndex = $targetProduct"
                                            practiceScore += 10
                                            tts.speak("Correct! $selectedMultiplier times $currentMultiplierIndex is $targetProduct")
                                            if (currentMultiplierIndex < 12) {
                                                currentMultiplierIndex += 1
                                            } else {
                                                showCelebration = true
                                                viewModel.awardGameReward(practiceScore, 3, "Times Table of $selectedMultiplier")
                                            }
                                        } else {
                                            feedbackMessage = "💡 Try again! Count $selectedMultiplier more."
                                            tts.speak("Not quite, try again!")
                                        }
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "$opt",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                            }

                            feedbackMessage?.let { msg ->
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = msg,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (msg.startsWith("🎉")) Color(0xFF16A34A) else Color(0xFFD97706),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
