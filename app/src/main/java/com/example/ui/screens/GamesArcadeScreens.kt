package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MathViewModel
import com.example.ui.Screen
import com.example.ui.components.ConfettiCelebration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

// --- 1. ARCADE HUB SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesArcadeHubScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎮 Math Arcade & Games", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
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
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF4F46E5),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF6366F1), Color(0xFFEC4899))
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
                                    text = "Interactive Math Arcade!",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Play hands-on games to pop balloons, slice pizza fractions, match memory cards & balance seesaws!",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            Text("🕹️", fontSize = 48.sp, modifier = Modifier.padding(start = 10.dp))
                        }
                    }
                }
            }

            // Game 1: Balloon Pop Mania
            item {
                ArcadeGameCard(
                    title = "🎈 Balloon Pop Mania",
                    subtitle = "Interactive Balloon Tapping!",
                    description = "Watch balloons float across the screen and tap to pop the exact answer! Features combo streaks and popping sound effects.",
                    badge = "MOST POPULAR",
                    badgeColor = Color(0xFFEF4444),
                    buttonColor = Color(0xFFEF4444),
                    onClick = { viewModel.navigateTo(Screen.BalloonPopGame) }
                )
            }

            // Game 2: Pizza Chef Fraction Bakery
            item {
                ArcadeGameCard(
                    title = "🍕 Pizza Chef Fraction Bakery",
                    subtitle = "Slice, Top & Serve Fractions!",
                    description = "Customers order 1/2, 3/4 or 5/8 of a pizza. Slice the pizza and tap slices to serve hungry animal friends!",
                    badge = "CREATIVE",
                    badgeColor = Color(0xFFF59E0B),
                    buttonColor = Color(0xFFD97706),
                    onClick = { viewModel.navigateTo(Screen.PizzaChefGame) }
                )
            }

            // Game 3: Memory Match Pairs
            item {
                ArcadeGameCard(
                    title = "🃏 Memory Math Flip & Match",
                    subtitle = "Card Matching Challenge!",
                    description = "Flip cards over and pair math equations with their solutions (e.g. 4 × 3 with 12). Tests memory and arithmetic fluency!",
                    badge = "BRAIN POWER",
                    badgeColor = Color(0xFF8B5CF6),
                    buttonColor = Color(0xFF7C3AED),
                    onClick = { viewModel.navigateTo(Screen.MemoryMatchGame) }
                )
            }

            // Game 4: Monster Balance Seesaw
            item {
                ArcadeGameCard(
                    title = "⚖️ Monster Balance Seesaw",
                    subtitle = "Physics Weight Balancing!",
                    description = "The friendly monster sits on the left pan. Drag and drop weight blocks on the right pan until the scale is level!",
                    badge = "PHYSICS",
                    badgeColor = Color(0xFF10B981),
                    buttonColor = Color(0xFF059669),
                    onClick = { viewModel.navigateTo(Screen.BalanceScaleGame) }
                )
            }

            // Game 5: Speed Math Sprint
            item {
                ArcadeGameCard(
                    title = "⚡ 60-Second Lightning Sprint",
                    subtitle = "Rapid-Fire Clock Challenge!",
                    description = "Race against the clock to solve as many equations as you can in 60 seconds with combo multiplier coins!",
                    badge = "SPEED",
                    badgeColor = Color(0xFF0284C7),
                    buttonColor = Color(0xFF0284C7),
                    onClick = { viewModel.startSpeedSprint() }
                )
            }

            // Game 6: Times Table Matrix Explorer
            item {
                ArcadeGameCard(
                    title = "✖️ Times Table Matrix & Explorer",
                    subtitle = "Dot Array & Flashcards 1 to 12!",
                    description = "Tap numbers 1 through 12, view interactive dot array matrices, and challenge yourself with instant multiplication flashcards!",
                    badge = "ESSENTIAL",
                    badgeColor = Color(0xFF06B6D4),
                    buttonColor = Color(0xFF0891B2),
                    onClick = { viewModel.navigateTo(Screen.TimesTable) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun ArcadeGameCard(
    title: String,
    subtitle: String,
    description: String,
    badge: String,
    badgeColor: Color,
    buttonColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = badge,
                        color = badgeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Text("🪙 Earn Coins & Stars", fontSize = 11.sp, color = Color(0xFFB45309), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = subtitle, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = buttonColor)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 17.sp)
            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Play Game Now 🎮", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

// --- 2. BALLOON POP MANIA GAME ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalloonPopGameScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    var score by remember { mutableStateOf(0) }
    var streak by remember { mutableStateOf(0) }
    var poppedCount by remember { mutableStateOf(0) }
    var celebrationVisible by remember { mutableStateOf(false) }

    // Current equation target
    var targetText by remember { mutableStateOf("4 + 5") }
    var targetAnswer by remember { mutableStateOf("9") }
    var balloons by remember {
        mutableStateOf(listOf("7", "9", "8", "6", "10").shuffled())
    }

    fun generateNewRound() {
        val a = (2..12).random()
        val b = (2..12).random()
        val ans = a + b
        targetText = "$a + $b"
        targetAnswer = ans.toString()
        val distractors = listOf(ans - 2, ans - 1, ans + 1, ans + 2).filter { it > 0 }
        balloons = (distractors.take(4) + ans).shuffled().map { it.toString() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎈 Balloon Pop Mania", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFEF3C7),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = "Score: $score 🎈",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = modifier.fillMaxSize().padding(innerPadding)) {
            if (celebrationVisible) {
                ConfettiCelebration()
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Target Equation Card
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFE0F2FE),
                    tonalElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "POP THE BALLOON WITH ANSWER:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0369A1)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "$targetText = ?",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0C4A6E)
                        )
                        if (streak > 1) {
                            Text(
                                text = "🔥 $streak COMBO STREAK!",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFEA580C)
                            )
                        }
                    }
                }

                // Interactive Floating Balloons Area
                Text(
                    text = "Tap on the balloon with the correct number to pop it!",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val balloonColors = listOf(
                    Color(0xFFEF4444), Color(0xFF3B82F6), Color(0xFF10B981),
                    Color(0xFFF59E0B), Color(0xFF8B5CF6)
                )

                // 5 Large Interactive Balloons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    balloons.take(3).forEachIndexed { i, valStr ->
                        BalloonItem(
                            number = valStr,
                            color = balloonColors[i % balloonColors.size],
                            onPop = {
                                if (valStr == targetAnswer) {
                                    score += 15
                                    streak += 1
                                    poppedCount += 1
                                    celebrationVisible = true
                                    viewModel.awardGameReward(15, 1, "Balloon Pop Mania")
                                    generateNewRound()
                                } else {
                                    streak = 0
                                }
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    balloons.drop(3).take(2).forEachIndexed { i, valStr ->
                        BalloonItem(
                            number = valStr,
                            color = balloonColors[(i + 3) % balloonColors.size],
                            onPop = {
                                if (valStr == targetAnswer) {
                                    score += 15
                                    streak += 1
                                    poppedCount += 1
                                    celebrationVisible = true
                                    viewModel.awardGameReward(15, 1, "Balloon Pop Mania")
                                    generateNewRound()
                                } else {
                                    streak = 0
                                }
                            }
                        )
                    }
                }

                Button(
                    onClick = { generateNewRound() },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Skip to Next Equation", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun BalloonItem(
    number: String,
    color: Color,
    onPop: () -> Unit
) {
    var isPopped by remember(number) { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                isPopped = true
                onPop()
            }
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(if (isPopped) Color(0xFFFEE2E2) else color)
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isPopped) {
                Text("💥", fontSize = 28.sp)
            } else {
                Text(
                    text = number,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
        // Balloon knot & string
        Text("🎈", fontSize = 14.sp)
    }
}

// --- 3. PIZZA CHEF FRACTION BAKERY GAME ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PizzaChefGameScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    var totalSlices by remember { mutableStateOf(4) }
    var selectedSlices by remember { mutableStateOf(setOf<Int>()) }
    var requestedNumerator by remember { mutableStateOf(3) }
    var requestedDenominator by remember { mutableStateOf(4) }
    var customerName by remember { mutableStateOf("Leo the Lion 🦁") }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var celebration by remember { mutableStateOf(false) }

    fun nextCustomer() {
        val customers = listOf("Leo the Lion 🦁", "Luna the Bunny 🐰", "Benny the Bear 🐻", "Robo-Max 🤖")
        customerName = customers.random()
        val dens = listOf(2, 3, 4, 6)
        requestedDenominator = dens.random()
        totalSlices = requestedDenominator
        requestedNumerator = (1 until requestedDenominator).random()
        selectedSlices = emptySet()
        feedbackMessage = null
        celebration = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🍕 Pizza Chef Fractions", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = modifier.fillMaxSize().padding(innerPadding)) {
            if (celebration) ConfettiCelebration()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Customer Request Bubble
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFFEF3C7),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🧑‍🍳", fontSize = 42.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = customerName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF78350F))
                            Text(
                                text = "\"Please serve me exactly $requestedNumerator/$requestedDenominator of this delicious pizza!\"",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF92400E)
                            )
                        }
                    }
                }

                // Interactive Pizza Canvas
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Tap slices to select them (${selectedSlices.size}/$totalSlices selected)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clickable {
                                // Cycle selected slices
                                val next = (selectedSlices.size + 1) % (totalSlices + 1)
                                selectedSlices = (0 until next).toSet()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val radius = size.minDimension / 2.2f
                            val center = Offset(size.width / 2f, size.height / 2f)

                            // Crust
                            drawCircle(color = Color(0xFFD97706), radius = radius + 8f, center = center)

                            val sweep = 360f / totalSlices
                            for (i in 0 until totalSlices) {
                                val start = i * sweep - 90f
                                val isSelected = selectedSlices.contains(i)
                                val col = if (isSelected) Color(0xFFFBBF24) else Color(0xFFFFF1F2)

                                drawArc(
                                    color = col,
                                    startAngle = start,
                                    sweepAngle = sweep,
                                    useCenter = true,
                                    topLeft = Offset(center.x - radius, center.y - radius),
                                    size = Size(radius * 2f, radius * 2f)
                                )
                                drawArc(
                                    color = Color(0xFF78350F),
                                    startAngle = start,
                                    sweepAngle = sweep,
                                    useCenter = true,
                                    topLeft = Offset(center.x - radius, center.y - radius),
                                    size = Size(radius * 2f, radius * 2f),
                                    style = Stroke(width = 3.5f)
                                )

                                if (isSelected) {
                                    val midRad = Math.toRadians((start + sweep / 2f).toDouble())
                                    val px = (center.x + (radius * 0.6f) * cos(midRad)).toFloat()
                                    val py = (center.y + (radius * 0.6f) * sin(midRad)).toFloat()
                                    drawCircle(color = Color(0xFFDC2626), radius = 10f, center = Offset(px, py))
                                }
                            }
                        }
                    }
                }

                if (feedbackMessage != null) {
                    Text(
                        text = feedbackMessage ?: "",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (celebration) Color(0xFF166534) else Color(0xFFDC2626)
                    )
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { nextCustomer() },
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Text("Next Customer")
                    }

                    Button(
                        onClick = {
                            if (selectedSlices.size == requestedNumerator && totalSlices == requestedDenominator) {
                                feedbackMessage = "🎉 PERFECT ORDER! $customerName loved it! (+20 Coins)"
                                celebration = true
                                viewModel.awardGameReward(20, 1, "Pizza Chef Fractions")
                            } else {
                                feedbackMessage = "Almost! Customer asked for $requestedNumerator/$requestedDenominator slices!"
                                celebration = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Text("Serve Order 🍽️", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// --- 4. MEMORY MATCH CARDS GAME ---
data class MemoryCard(
    val id: Int,
    val pairId: Int,
    val text: String,
    val isFlipped: Boolean = false,
    val isMatched: Boolean = false
)

private fun generateMemoryCardDeck(): List<MemoryCard> {
    val pairs = listOf(
        (1 to "3 + 4") to (1 to "7"),
        (2 to "5 × 2") to (2 to "10"),
        (3 to "12 - 4") to (3 to "8"),
        (4 to "6 ÷ 2") to (4 to "3"),
        (5 to "9 + 6") to (5 to "15"),
        (6 to "4 × 4") to (6 to "16")
    )
    val cardList = mutableListOf<MemoryCard>()
    var id = 0
    pairs.forEach { (a, b) ->
        cardList.add(MemoryCard(id++, a.first, a.second))
        cardList.add(MemoryCard(id++, b.first, b.second))
    }
    return cardList.shuffled()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryMatchGameScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val scope = rememberCoroutineScope()
    var cards by remember { mutableStateOf(generateMemoryCardDeck()) }
    var firstSelectedId by remember { mutableStateOf<Int?>(null) }
    var secondSelectedId by remember { mutableStateOf<Int?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var matchesFound by remember { mutableStateOf(0) }
    var celebration by remember { mutableStateOf(false) }

    fun restartGame() {
        cards = generateMemoryCardDeck()
        firstSelectedId = null
        secondSelectedId = null
        isProcessing = false
        matchesFound = 0
        celebration = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🃏 Memory Math Match", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { restartGame() }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Restart")
                    }
                    Text(
                        text = "Pairs: $matchesFound/6 ⭐",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6D28D9),
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = modifier.fillMaxSize().padding(innerPadding)) {
            if (celebration) ConfettiCelebration()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFEDE9FE),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💡 Tap 2 cards to match equations!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6D28D9)
                        )
                        TextButton(onClick = { restartGame() }) {
                            Text("New Cards 🔄", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(cards, key = { it.id }) { card ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = when {
                                card.isMatched -> Color(0xFFDCFCE7)
                                card.isFlipped -> Color(0xFFE0E7FF)
                                else -> Color(0xFF4F46E5)
                            },
                            border = if (card.isMatched) {
                                androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF10B981))
                            } else null,
                            tonalElevation = if (card.isFlipped || card.isMatched) 6.dp else 2.dp,
                            modifier = Modifier
                                .height(88.dp)
                                .clickable(enabled = !card.isMatched && !card.isFlipped && !isProcessing) {
                                    if (firstSelectedId == null) {
                                        firstSelectedId = card.id
                                        cards = cards.map {
                                            if (it.id == card.id) it.copy(isFlipped = true) else it
                                        }
                                    } else if (secondSelectedId == null && card.id != firstSelectedId) {
                                        val firstId = firstSelectedId!!
                                        secondSelectedId = card.id
                                        cards = cards.map {
                                            if (it.id == card.id) it.copy(isFlipped = true) else it
                                        }
                                        isProcessing = true

                                        val firstCard = cards.find { it.id == firstId }
                                        val secondCard = card

                                        scope.launch {
                                            if (firstCard != null && firstCard.pairId == secondCard.pairId) {
                                                // MATCH! Fast 200ms reaction
                                                delay(200)
                                                cards = cards.map {
                                                    if (it.pairId == firstCard.pairId) {
                                                        it.copy(isMatched = true, isFlipped = true)
                                                    } else it
                                                }
                                                matchesFound++
                                                if (matchesFound >= 6) {
                                                    celebration = true
                                                    viewModel.awardGameReward(30, 3, "Memory Math Match")
                                                }
                                            } else {
                                                // NO MATCH! Snappy 400ms feedback
                                                delay(400)
                                                cards = cards.map {
                                                    if (it.id == firstId || it.id == secondCard.id) {
                                                        it.copy(isFlipped = false)
                                                    } else it
                                                }
                                            }
                                            firstSelectedId = null
                                            secondSelectedId = null
                                            isProcessing = false
                                        }
                                    }
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (card.isFlipped || card.isMatched) {
                                    Text(
                                        text = card.text,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = if (card.isMatched) Color(0xFF166534) else Color(0xFF1E1B4B)
                                    )
                                } else {
                                    Text("⭐", fontSize = 24.sp)
                                }
                            }
                        }
                    }
                }

                if (matchesFound >= 6) {
                    Button(
                        onClick = { restartGame() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Play Another Round 🏆 (+30 Coins)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// --- 5. MONSTER BALANCE SEESAW GAME ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalanceScaleGameScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    var targetWeight by remember { mutableStateOf(10) }
    var currentRightWeight by remember { mutableStateOf(4) }
    var celebration by remember { mutableStateOf(false) }

    val tiltAngle by animateFloatAsState(
        targetValue = when {
            targetWeight > currentRightWeight -> -12f
            targetWeight < currentRightWeight -> 12f
            else -> 0f
        },
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "tilt"
    )

    fun resetNewPuzzle() {
        targetWeight = (6..15).random()
        currentRightWeight = (1 until targetWeight).random()
        celebration = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("⚖️ Monster Balance Seesaw", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = modifier.fillMaxSize().padding(innerPadding)) {
            if (celebration) ConfettiCelebration()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFDCFCE7),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("TARGET: Balance Both Sides Evenly!", fontWeight = FontWeight.Bold, color = Color(0xFF166534), fontSize = 14.sp)
                        Text("Left Monster: $targetWeight kg | Right Pan: $currentRightWeight kg", fontSize = 13.sp)
                    }
                }

                // Seesaw Visualizer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Tilting beam
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .rotate(tiltAngle),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(0.9f),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Left Pan
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFFBBF24),
                                    modifier = Modifier.size(70.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text("👾", fontSize = 24.sp)
                                        Text("${targetWeight}kg", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }

                                // Beam Bar
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(8.dp)
                                        .background(Color(0xFF64748B), RoundedCornerShape(4.dp))
                                )

                                // Right Pan
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (targetWeight == currentRightWeight) Color(0xFF10B981) else Color(0xFF38BDF8),
                                    modifier = Modifier.size(70.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text("📦", fontSize = 24.sp)
                                        Text("${currentRightWeight}kg", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                    }
                                }
                            }
                        }

                        // Fulcrum / Pivot
                        Text("▲", fontSize = 38.sp, color = Color(0xFF475569))
                    }
                }

                // Balance Status
                if (targetWeight == currentRightWeight) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFDCFCE7)
                    ) {
                        Text(
                            text = "🎉 PERFECT BALANCE! (+25 Coins)",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                } else {
                    val needed = targetWeight - currentRightWeight
                    Text(
                        text = if (needed > 0) "Need +${needed}kg on the right to balance!" else "Too heavy on right! Remove weight.",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Add Weight Blocks
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Tap to add weight blocks to the right pan:", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(1, 2, 3, 5).forEach { w ->
                            Button(
                                onClick = {
                                    currentRightWeight += w
                                    if (currentRightWeight == targetWeight) {
                                        celebration = true
                                        viewModel.awardGameReward(25, 1, "Monster Balance Seesaw")
                                    }
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("+$w kg", fontWeight = FontWeight.Bold)
                            }
                        }
                        OutlinedButton(
                            onClick = { currentRightWeight = 0 },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Clear")
                        }
                    }
                }

                Button(
                    onClick = { resetNewPuzzle() },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("New Balance Puzzle ⚖️", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
