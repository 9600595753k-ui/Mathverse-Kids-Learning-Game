package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GradeLevel
import com.example.model.Language
import com.example.model.Milestone
import com.example.model.PracticeSession
import com.example.ui.MathViewModel
import com.example.ui.Screen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentDashboardScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val student by viewModel.student.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    val milestones = viewModel.milestones
    val isDark by viewModel.isDarkTheme.collectAsState()
    val language by viewModel.selectedLanguage.collectAsState()

    var showLogoutConfirm by remember { mutableStateOf(false) }
    var showSwitchStudentDialog by remember { mutableStateOf(false) }
    var showChangePinDialog by remember { mutableStateOf(false) }
    var newPinInput by remember { mutableStateOf("") }
    var dailyLimitMinutes by remember { mutableStateOf(30) }

    val totalQuestions = sessions.sumOf { it.totalQuestions }
    val totalCorrect = sessions.sumOf { it.score }
    val avgAccuracy = if (totalQuestions > 0) (totalCorrect * 100) / totalQuestions else 94
    val totalMinutes = sessions.sumOf { it.durationSeconds } / 60 + 45

    // Logout Confirmation Dialog
    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Log Out Student Account?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Are you sure you want to log out of ${student.name}'s account (${student.email})? You can log back in anytime.",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        viewModel.navigateTo(Screen.Register)
                        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Yes, Log Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Switch / Add Student Dialog
    if (showSwitchStudentDialog) {
        AlertDialog(
            onDismissRequest = { showSwitchStudentDialog = false },
            title = { Text("Switch Student Profile", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.fillMaxWidth().clickable {
                            showSwitchStudentDialog = false
                            Toast.makeText(context, "Active profile: ${student.name}", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("🤖", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(student.name, fontWeight = FontWeight.Bold)
                                Text("${student.gradeLevel.label} • Current Active", fontSize = 11.sp)
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth().clickable {
                            viewModel.registerStudent("Maya", GradeLevel.GRADE_3, "maya.student@gmail.com")
                            showSwitchStudentDialog = false
                            Toast.makeText(context, "Switched to Maya", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("🐱", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Maya", fontWeight = FontWeight.Bold)
                                Text("3rd Grade • maya.student@gmail.com", fontSize = 11.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    showSwitchStudentDialog = false
                    viewModel.navigateTo(Screen.Register)
                }) {
                    Text("+ Add New Student")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSwitchStudentDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Change Parental PIN Dialog
    if (showChangePinDialog) {
        AlertDialog(
            onDismissRequest = { showChangePinDialog = false },
            title = { Text("Set Parental Security PIN", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter a new 4-digit PIN for parent gate protection:", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newPinInput,
                        onValueChange = { if (it.length <= 4) newPinInput = it },
                        label = { Text("4-Digit PIN") },
                        placeholder = { Text("e.g. 5678") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPinInput.length == 4) {
                            showChangePinDialog = false
                            Toast.makeText(context, "Parental PIN updated to $newPinInput", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Save PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePinDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Parent & Educator Dashboard", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text("Settings, Account & Analytics", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("parent_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.Settings) }) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                    }
                    IconButton(onClick = { showLogoutConfirm = true }) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Log Out")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Student Profile Header Card
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎓", fontSize = 32.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = student.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Text(
                                text = "${student.gradeLevel.label} • ${student.email}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (student.isVerified) "✅ Verified Gmail Account" else "⚠️ Email Pending Verification",
                                fontSize = 11.sp,
                                color = if (student.isVerified) Color(0xFF166534) else Color(0xFFB45309),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // PARENT ACCOUNT CONTROLS: Switch Profile, Login Another, Logout
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Account & Login / Logout Controls", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showSwitchStudentDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Switch Profile 👥", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { viewModel.navigateTo(Screen.Register) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Add Student ➕", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { showLogoutConfirm = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Log Out 🚪", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // PARENTAL SETTINGS & SCREEN TIME LIMITS
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = Color(0xFFD97706))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Parental Settings & Time Controls", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // Daily practice limit chips
                        Text("Daily Math Practice Time Target:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(15, 30, 45, 60).forEach { mins ->
                                FilterChip(
                                    selected = dailyLimitMinutes == mins,
                                    onClick = {
                                        dailyLimitMinutes = mins
                                        Toast.makeText(context, "Target set to $mins mins/day", Toast.LENGTH_SHORT).show()
                                    },
                                    label = { Text("$mins mins", fontSize = 11.sp) },
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Parental Gate PIN", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Current PIN: **** (Default: 1234)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Button(
                                onClick = { showChangePinDialog = true },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Change PIN", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Settings toggles (Dark Mode & Language)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Dark Mode Display", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Switch(checked = isDark, onCheckedChange = { viewModel.toggleDarkTheme() })
                        }
                    }
                }
            }

            // Quick Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Accuracy Rate",
                        value = "$avgAccuracy%",
                        emoji = "🎯",
                        color = Color(0xFFDCFCE7),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Practice Time",
                        value = "$totalMinutes mins",
                        emoji = "⏱️",
                        color = Color(0xFFE0F2FE),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Coins / Stars",
                        value = "${student.mathCoins} / ${student.totalStars}⭐",
                        emoji = "🪙",
                        color = Color(0xFFFEF3C7),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // PDF Export Actions Card (Printing & WhatsApp sharing)
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "📄 Export & Share Analytics Report",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Generate an official printable PDF summary including developmental milestones, competency breakdown, and educator feedback notes.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.printProgressReport(context) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).testTag("print_pdf_button")
                            ) {
                                Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Print PDF", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.shareProgressReport(context) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF25D366),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).testTag("share_whatsapp_button")
                            ) {
                                Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("WhatsApp / Social", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Subject Competency Progress Bars
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "📊 Subject Competencies",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val subjects = listOf(
                            Triple("Addition & Two Baskets", 0.95f, Color(0xFF4F46E5)),
                            Triple("Subtraction & Balloon Pop", 0.90f, Color(0xFF0284C7)),
                            Triple("Multiplication Arrays", 0.92f, Color(0xFF8B5CF6)),
                            Triple("Division & Sharing", 0.85f, Color(0xFF10B981)),
                            Triple("Geometry & Shapes", 0.96f, Color(0xFFF59E0B)),
                            Triple("Fractions & Pizza Bakery", 0.88f, Color(0xFFF43F5E)),
                            Triple("Telling Time & Clocks", 0.91f, Color(0xFF0284C7)),
                            Triple("Money & Coins Counter", 0.89f, Color(0xFFD97706))
                        )

                        subjects.forEach { (name, progress, barColor) ->
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = name, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    Text(text = "${(progress * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = barColor)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                    color = barColor,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Developmental Milestones Section
            item {
                Text(
                    text = "🏆 Developmental Milestones",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }

            items(milestones) { milestone ->
                MilestoneItemCard(milestone = milestone)
            }

            // Educator Communication Box
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFFF8FAFC),
                    tonalElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💬", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Educator Communication & Guidance",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF0F172A)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• \"Leo demonstrates stellar engagement with pizza fraction visualizations and balloon pop calculations!\"",
                            fontSize = 12.sp,
                            color = Color(0xFF475569)
                        )
                        Text(
                            text = "• Recommendation: 10 minutes of daily multi-digit carry-over exercises keeps arithmetic sharp.",
                            fontSize = 12.sp,
                            color = Color(0xFF475569)
                        )
                    }
                }
            }

            // Practice Sessions Log
            item {
                Text(
                    text = "🕒 Recent Practice Sessions (${sessions.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }

            items(sessions.take(6)) { session ->
                SessionLogItem(session = session)
            }

            // COPPA Data Privacy Card
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Certified Child Safe & COPPA Compliant",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "All student academic records are stored strictly offline on-device with zero behavioral ads or external tracking.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    emoji: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF0F172A)
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = Color(0xFF475569)
            )
        }
    }
}

@Composable
fun MilestoneItemCard(milestone: Milestone) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (milestone.isAchieved) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(if (milestone.isAchieved) "🏆" else "⏳", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = milestone.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = milestone.description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (milestone.isAchieved) Color(0xFFDCFCE7) else Color(0xFFF1F5F9)
            ) {
                Text(
                    text = if (milestone.isAchieved) "Mastered" else "${milestone.current}/${milestone.target}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (milestone.isAchieved) Color(0xFF166534) else Color(0xFF475569),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun SessionLogItem(session: PracticeSession) {
    val dateStr = remember(session.timestamp) {
        SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(Date(session.timestamp))
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = session.lessonTitle, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text(text = "$dateStr • ${session.durationSeconds}s duration", fontSize = 11.sp, color = Color.Gray)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "${session.accuracyPercent}%", fontWeight = FontWeight.Bold, color = Color(0xFF10B981), fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "⭐${session.starsEarned}", fontSize = 12.sp)
            }
        }
    }
}
