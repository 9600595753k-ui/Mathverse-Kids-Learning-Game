package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GradeLevel
import com.example.ui.MathViewModel
import com.example.ui.Screen
import com.example.ui.components.EmailVerificationDialog

@Composable
fun OnboardingScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    var currentSlide by remember { mutableStateOf(0) }

    val slides = listOf(
        Triple(
            "🌟 Welcome to MathWhiz Kids!",
            "Learn elementary mathematics through playful games, pizza fractions, and friendly visual counters. Designed for curious young minds!",
            "🧮"
        ),
        Triple(
            "🎮 Play, Earn & Dress-Up!",
            "Solve math questions to earn gold stars and math coins. Unlock cool hats, astronaut helmets, and hero capes for your mascot!",
            "🚀"
        ),
        Triple(
            "📊 Parent & Educator Insights",
            "Detailed milestone tracking for parents, offline lesson packs, and one-tap PDF report exports to print or share with teachers!",
            "📈"
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top skip button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { viewModel.navigateTo(Screen.Home) },
                modifier = Modifier.testTag("skip_onboarding_button")
            ) {
                Text("Skip", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        // Center carousel content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(text = slides[currentSlide].third, fontSize = 64.sp)
            }
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = slides[currentSlide].first,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = slides[currentSlide].second,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))
            // Dots
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                slides.indices.forEach { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == currentSlide) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == currentSlide)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            )
                    )
                }
            }
        }

        // Bottom Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentSlide > 0) {
                OutlinedButton(
                    onClick = { currentSlide-- },
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Back")
                }
            } else {
                Spacer(modifier = Modifier.width(60.dp))
            }

            Button(
                onClick = {
                    if (currentSlide < slides.size - 1) {
                        currentSlide++
                    } else {
                        viewModel.navigateTo(Screen.Register)
                    }
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("onboarding_next_button")
            ) {
                Text(
                    text = if (currentSlide == slides.size - 1) "Create Student Profile" else "Next",
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun RegistrationScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    var studentName by remember { mutableStateOf("Leo") }
    var selectedGrade by remember { mutableStateOf(GradeLevel.GRADE_2) }
    var emailInput by remember { mutableStateOf("leo.student@gmail.com") }
    var showVerifyDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    if (showVerifyDialog) {
        EmailVerificationDialog(
            studentEmail = emailInput,
            onDismiss = {
                showVerifyDialog = false
                viewModel.navigateTo(Screen.Home)
            },
            onVerified = {
                viewModel.verifyStudentEmail()
                showVerifyDialog = false
                viewModel.navigateTo(Screen.Home)
            }
        )
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
                Text("Student Registration", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                TextButton(onClick = { viewModel.navigateTo(Screen.Home) }) {
                    Text("Skip")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text("🎒", fontSize = 42.sp)
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Welcome Little Math Hero!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Set up your student profile to track progress, save badges, and sync offline lessons.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            // Name field
            OutlinedTextField(
                value = studentName,
                onValueChange = { studentName = it },
                label = { Text("Student Name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("student_name_input"),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Grade Picker
            Text(
                text = "Select Grade Level",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(GradeLevel.values()) { grade ->
                    val isSelected = grade == selectedGrade
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = if (isSelected) 4.dp else 0.dp,
                        modifier = Modifier
                            .clickable { selectedGrade = grade }
                            .testTag("grade_${grade.name}")
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = grade.label,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = grade.ageGroup,
                                fontSize = 11.sp,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Gmail ID field
            OutlinedTextField(
                value = emailInput,
                onValueChange = { emailInput = it },
                label = { Text("Parent or Student Gmail ID") },
                placeholder = { Text("name@gmail.com") },
                supportingText = {
                    Text("We will send an email confirmation code with login credentials for account verification.")
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("student_email_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    viewModel.registerStudent(studentName.trim(), selectedGrade, emailInput.trim())
                    showVerifyDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("register_student_submit"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Register & Verify Account", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = { viewModel.navigateTo(Screen.Home) },
                modifier = Modifier.testTag("continue_offline_guest")
            ) {
                Text("Continue as Offline Guest")
            }
        }
    }
}
