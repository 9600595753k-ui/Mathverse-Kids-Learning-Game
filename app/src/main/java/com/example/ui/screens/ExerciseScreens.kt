package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Lesson
import com.example.model.Localization
import com.example.model.VisualType
import com.example.ui.MathViewModel
import com.example.ui.components.*

import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.ui.platform.LocalContext
import com.example.util.TTSHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonExerciseScreen(
    lesson: Lesson,
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

    var showScratchpad by remember { mutableStateOf(false) }

    val quizState by viewModel.quizState.collectAsState()
    val language by viewModel.selectedLanguage.collectAsState()
    val currentQuestion = lesson.questions.getOrNull(quizState.currentQuestionIndex) ?: lesson.questions.first()
    val scrollState = rememberScrollState()

    if (showScratchpad) {
        MathScratchpadDialog(onDismiss = { showScratchpad = false })
    }

    if (quizState.isFinished) {
        LessonCelebrationDialog(
            score = quizState.score,
            total = lesson.questions.size,
            coinsEarned = quizState.totalCoinsEarned,
            onDone = { viewModel.navigateBack() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = lesson.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Question ${quizState.currentQuestionIndex + 1} of ${lesson.questions.size}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("lesson_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Scratchpad rough sheet toggle
                    IconButton(
                        onClick = { showScratchpad = true },
                        modifier = Modifier.testTag("scratchpad_button")
                    ) {
                        Text("✏️", fontSize = 20.sp)
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFEF3C7),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🪙", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+${quizState.totalCoinsEarned}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Linear Progress Indicator
            LinearProgressIndicator(
                progress = { (quizState.currentQuestionIndex + 1).toFloat() / lesson.questions.size.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mascot Speech Card with Voice Audio Read-Aloud
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🤖", fontSize = 32.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = currentQuestion.questionText,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            tts.speak(currentQuestion.questionText, language)
                        },
                        modifier = Modifier.testTag("read_question_tts_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Read Aloud",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Visualizer Component based on type
            when (currentQuestion.visualType) {
                VisualType.COUNTER_DOTS -> {
                    CountersVisualizer(data = currentQuestion.visualData)
                }
                VisualType.PIZZA_FRACTION -> {
                    PizzaFractionVisualizer(
                        numerator = currentQuestion.visualData.fractionNumerator,
                        denominator = currentQuestion.visualData.fractionDenominator
                    )
                }
                VisualType.SHAPE_GEOMETRY -> {
                    ShapeGeometryVisualizer(
                        shapeType = currentQuestion.visualData.shapeType,
                        sides = currentQuestion.visualData.shapeSides
                    )
                }
                VisualType.NUMBER_LINE -> {
                    NumberLineVisualizer(data = currentQuestion.visualData)
                }
                VisualType.ARRAY_GRID -> {
                    ArrayGridVisualizer(data = currentQuestion.visualData)
                }
                VisualType.WORD_STORY -> {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF0FDF4),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("💡", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Story Clue: ${currentQuestion.hint}",
                                fontSize = 13.sp,
                                color = Color(0xFF166534),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                VisualType.ANALOG_CLOCK -> {
                    AnalogClockVisualizer(
                        hour = currentQuestion.visualData.clockHour,
                        minute = currentQuestion.visualData.clockMinute
                    )
                }
                VisualType.MONEY_COINS -> {
                    MoneyCoinsVisualizer(
                        summary = currentQuestion.visualData.coinsSummary,
                        coinValues = currentQuestion.visualData.coinsValues
                    )
                }
                VisualType.PATTERN_TRAIN -> {
                    PatternTrainVisualizer(
                        pattern = currentQuestion.visualData.patternSequence
                    )
                }
                VisualType.BALANCE_SCALE -> {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFEF3C7),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("⚖️", fontSize = 32.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Balance Scale: Left side has ${currentQuestion.visualData.scaleLeftWeight}kg, right side has ${currentQuestion.visualData.scaleRightWeight}kg. Balance them!",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF92400E)
                            )
                        }
                    }
                }
                VisualType.NONE -> {
                    // Clean layout for standard arithmetic
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Interactive Options Grid (Large 48dp+ touch buttons)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                currentQuestion.options.chunked(2).forEach { rowOptions ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowOptions.forEach { option ->
                            val isSelected = quizState.selectedOption == option
                            val buttonColor = if (quizState.isAnswerChecked) {
                                if (option == currentQuestion.correctAnswer) Color(0xFF10B981)
                                else if (isSelected) Color(0xFFEF4444)
                                else MaterialTheme.colorScheme.surfaceVariant
                            } else {
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            }

                            val textColor = if (quizState.isAnswerChecked && (option == currentQuestion.correctAnswer || isSelected)) {
                                Color.White
                            } else {
                                if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            }

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = buttonColor,
                                tonalElevation = if (isSelected) 4.dp else 1.dp,
                                shadowElevation = 2.dp,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(64.dp)
                                    .clickable(enabled = !quizState.isAnswerChecked) {
                                        viewModel.selectQuizOption(option)
                                    }
                                    .testTag("option_$option")
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = option,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Feedback Card
            AnimatedVisibility(visible = quizState.isAnswerChecked) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (quizState.isCorrect) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (quizState.isCorrect) "🌟" else "💡",
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Localization.getEncouragement(quizState.isCorrect, language),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (quizState.isCorrect) Color(0xFF166534) else Color(0xFF991B1B)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentQuestion.explanation,
                            fontSize = 13.sp,
                            color = if (quizState.isCorrect) Color(0xFF15803D) else Color(0xFFB91C1C)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Button (Check Answer / Next Question)
            if (!quizState.isAnswerChecked) {
                Button(
                    onClick = { viewModel.checkAnswer(currentQuestion, lesson) },
                    enabled = quizState.selectedOption != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("check_answer_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Check Answer ✨", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = { viewModel.nextQuestion(lesson) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("next_question_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (quizState.isCorrect) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = if (quizState.currentQuestionIndex + 1 < lesson.questions.size) "Next Question ➡️" else "Finish Lesson 🏆",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun LessonCelebrationDialog(
    score: Int,
    total: Int,
    coinsEarned: Int,
    onDone: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ConfettiCelebration()

        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 10.dp,
            modifier = Modifier.padding(24.dp).fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🎉", fontSize = 56.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Lesson Completed!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "You scored $score out of $total questions correctly!",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stars rating
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val stars = when {
                        score == total -> 3
                        score >= total / 2 -> 2
                        else -> 1
                    }
                    repeat(3) { index ->
                        Text(
                            text = if (index < stars) "⭐" else "☆",
                            fontSize = 32.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFEF3C7)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🪙", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "+$coinsEarned Math Coins Earned!",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDone,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("celebration_done_button")
                ) {
                    Text("Collect Rewards & Continue", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeedSprintScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val sprintState by viewModel.sprintState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("⚡ Speed Math Sprint", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (sprintState.timeLeftSeconds <= 10) Color(0xFFFEE2E2) else Color(0xFFE0F2FE)
                ) {
                    Text(
                        text = "⏱️ ${sprintState.timeLeftSeconds}s",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = if (sprintState.timeLeftSeconds <= 10) Color(0xFFDC2626) else Color(0xFF0369A1),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = "Score: ${sprintState.score} ⚡",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF92400E),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }

            if (!sprintState.isFinished) {
                val q = sprintState.currentQuestion
                if (q != null) {
                    // Question box
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.fillMaxWidth().height(150.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = q.questionText,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // Answer Options (2x2)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        q.options.chunked(2).forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                row.forEach { opt ->
                                    Button(
                                        onClick = { viewModel.answerSprintQuestion(opt) },
                                        shape = RoundedCornerShape(18.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(68.dp)
                                            .testTag("sprint_opt_$opt")
                                    ) {
                                        Text(text = opt, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Game Over Card
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 20.dp)
                ) {
                    Text("🏁", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Time's Up! Sprint Completed",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Final Score: ${sprintState.score} solved in 60s!",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFEF3C7)
                    ) {
                        Text(
                            text = "+${sprintState.score * 5} Math Coins Earned!",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.startSpeedSprint() },
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Text("Sprint Again ⚡", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Text("Back to Home")
                    }
                }
            }

            Spacer(modifier = Modifier.height(1.dp))
        }
    }
}
