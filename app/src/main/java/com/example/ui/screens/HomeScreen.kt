package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.model.Lesson
import com.example.model.LessonCategory
import com.example.model.Localization
import com.example.ui.MathViewModel
import com.example.ui.Screen
import com.example.ui.components.ParentPinDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    val student by viewModel.student.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val language by viewModel.selectedLanguage.collectAsState()
    val isDark by viewModel.isDarkTheme.collectAsState()

    val filteredLessons = remember(searchQuery, selectedCategory) {
        viewModel.getFilteredLessons()
    }

    var showParentGate by remember { mutableStateOf(false) }
    var showLangMenu by remember { mutableStateOf(false) }

    if (showParentGate) {
        ParentPinDialog(
            onDismiss = { showParentGate = false },
            onSuccess = {
                showParentGate = false
                viewModel.navigateTo(Screen.ParentDashboard)
            }
        )
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Top header row: Avatar, stats, quick toggles, parent gate
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar & Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { viewModel.navigateTo(Screen.Store) }
                            .testTag("avatar_profile_badge")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🤖", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = student.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                if (student.isVip) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("👑", fontSize = 12.sp)
                                }
                            }
                            Text(
                                text = student.gradeLevel.label,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Stat Pills & Actions
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Coins Pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFEF3C7),
                            modifier = Modifier.clickable { viewModel.navigateTo(Screen.Store) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🪙", fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${student.mathCoins}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF92400E)
                                )
                            }
                        }

                        // Stars Pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEDE9FE)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("⭐", fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${student.totalStars}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF6D28D9)
                                )
                            }
                        }

                        // Dark mode toggle
                        IconButton(
                            onClick = { viewModel.toggleDarkTheme() },
                            modifier = Modifier.size(36.dp).testTag("dark_mode_toggle_button")
                        ) {
                            Text(if (isDark) "☀️" else "🌙", fontSize = 18.sp)
                        }

                        // Language selector
                        Box {
                            IconButton(
                                onClick = { showLangMenu = true },
                                modifier = Modifier.size(36.dp).testTag("language_selector_button")
                            ) {
                                Text(language.flagEmoji, fontSize = 20.sp)
                            }
                            DropdownMenu(
                                expanded = showLangMenu,
                                onDismissRequest = { showLangMenu = false }
                            ) {
                                Language.values().forEach { l ->
                                    DropdownMenuItem(
                                        text = { Text("${l.flagEmoji} ${l.displayName}") },
                                        onClick = {
                                            viewModel.setLanguage(l)
                                            showLangMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        // Parent Zone Button
                        Button(
                            onClick = { showParentGate = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("parent_zone_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Parents",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = Localization.getUiString("search_placeholder", language),
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("math_search_bar")
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Subject Categories Carousel
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { viewModel.selectCategory(null) },
                            label = { Text("All Subjects", fontWeight = FontWeight.SemiBold) },
                            leadingIcon = { Text("📚") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("filter_all_subjects")
                        )
                    }
                    items(LessonCategory.values()) { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectCategory(if (isSelected) null else category) },
                            label = {
                                Text(
                                    text = Localization.getCategoryTitle(category, language),
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            leadingIcon = { Text(category.icon) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("filter_${category.name}")
                        )
                    }
                }
            }

            // Daily Challenge Hero Card
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primary,
                    tonalElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF4F46E5),
                                        Color(0xFF7C3AED)
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "⚡ DAILY SPEED MATH",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "60-Second Math Sprint!",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Race the clock and double your coins today!",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.startSpeedSprint() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFBBF24),
                                        contentColor = Color(0xFF78350F)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("start_sprint_button")
                                ) {
                                    Text("Sprint Now ⚡", fontWeight = FontWeight.Bold)
                                }
                            }
                            Text("⏱️", fontSize = 52.sp, modifier = Modifier.padding(start = 12.dp))
                        }
                    }
                }
            }

            // Arcade Games Hero Banner
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF4F46E5),
                    tonalElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth().clickable { viewModel.navigateTo(Screen.GamesHub) }
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF6366F1),
                                        Color(0xFFEC4899)
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "🎮 NEW INTERACTIVE GAMES",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Math Arcade: 6 Fun Games!",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Balloon Pop, Pizza Fractions, Memory Flip, Seesaw & Times Table Matrix!",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.navigateTo(Screen.GamesHub) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFBBF24),
                                        contentColor = Color(0xFF78350F)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("open_arcade_hub_button")
                                ) {
                                    Text("Play Games 🕹️", fontWeight = FontWeight.Bold)
                                }
                            }
                            Text("🎈", fontSize = 52.sp, modifier = Modifier.padding(start = 12.dp))
                        }
                    }
                }
            }

            // Daily Math Fun Fact & Brain Teaser
            item {
                var factIndex by remember { mutableIntStateOf(0) }
                val funFacts = listOf(
                    "Aryabhata in ancient India invented the mathematical concept of Zero (0), revolutionizing math worldwide! 🇮🇳",
                    "Honeybees are expert mathematicians! Hexagonal honeycomb cells use the least wax to store the most honey! 🐝",
                    "A pizza with radius 'z' and thickness 'a' has volume: V = Pi × z × z × a (Pizza)! 🍕",
                    "Sunflowers pack their seeds in Fibonacci spirals (1, 1, 2, 3, 5, 8...) so every seed gets sunlight! 🌻",
                    "The number '4' is the only number in English with the exact same number of letters as its value! 🔢"
                )

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFFEFF6FF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("💡", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Math Fact of the Day",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF1D4ED8)
                                )
                            }
                            TextButton(
                                onClick = { factIndex = (factIndex + 1) % funFacts.size },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Next Fact 🔄", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = funFacts[factIndex],
                            fontSize = 13.sp,
                            color = Color(0xFF1E3A8A),
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Quick Applet Modules (Arcade, Rewards, VIP, Settings)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "Arcade",
                        subtitle = "5 Games",
                        emoji = "🎮",
                        color = Color(0xFFFCE7F3),
                        modifier = Modifier.weight(1f).testTag("quick_arcade_games")
                    ) {
                        viewModel.navigateTo(Screen.GamesHub)
                    }

                    QuickActionCard(
                        title = "Reward Shop",
                        subtitle = "Dress Avatar",
                        emoji = "🧙‍♂️",
                        color = Color(0xFFE0E7FF),
                        modifier = Modifier.weight(1f).testTag("quick_reward_shop")
                    ) {
                        viewModel.navigateTo(Screen.Store)
                    }

                    QuickActionCard(
                        title = "School News",
                        subtitle = "3 Alerts",
                        emoji = "📢",
                        color = Color(0xFFFEF2F2),
                        modifier = Modifier.weight(1f).testTag("quick_school_news")
                    ) {
                        viewModel.navigateTo(Screen.Updates)
                    }

                    QuickActionCard(
                        title = "Math VIP",
                        subtitle = if (student.isVip) "Active VIP" else "Try Free",
                        emoji = "👑",
                        color = Color(0xFFFEF3C7),
                        modifier = Modifier.weight(1f).testTag("quick_vip_button")
                    ) {
                        viewModel.navigateTo(Screen.Subscription)
                    }
                }
            }

            // Lessons Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Interactive Lessons (${filteredLessons.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    // Offline status chip
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFDCFCE7)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🟢", fontSize = 9.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Offline Ready",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF166534)
                            )
                        }
                    }
                }
            }

            // Lessons List
            items(filteredLessons) { lesson ->
                LessonCard(
                    lesson = lesson,
                    isVip = student.isVip,
                    onStart = {
                        if (lesson.isPremium && !student.isVip) {
                            viewModel.navigateTo(Screen.Subscription)
                        } else {
                            viewModel.startLesson(lesson)
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    emoji: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color,
        modifier = modifier
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color(0xFF475569),
                maxLines = 1
            )
        }
    }
}

@Composable
fun LessonCard(
    lesson: Lesson,
    isVip: Boolean,
    onStart: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onStart)
            .testTag("lesson_card_${lesson.id}")
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon Badge
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(lesson.category.colorHex).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = lesson.category.icon, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = lesson.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    if (lesson.isPremium) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFEF3C7)
                        ) {
                            Text(
                                text = "VIP",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = lesson.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${lesson.questions.size} Questions",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                    Text("•", fontSize = 10.sp, color = Color.Gray)
                    Text(
                        text = lesson.gradeLevel.label,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Start arrow or play button
            Button(
                onClick = onStart,
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                modifier = Modifier.testTag("start_lesson_${lesson.id}")
            ) {
                Text(
                    text = if (lesson.isPremium && !isVip) "Unlock" else "Play",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
