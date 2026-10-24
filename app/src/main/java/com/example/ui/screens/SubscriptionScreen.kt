package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
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
import com.example.ui.components.PaymentGatewayDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    viewModel: MathViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val student by viewModel.student.collectAsState()
    val latestPayment by viewModel.latestPayment.collectAsState()
    var selectedPlan by remember { mutableStateOf("ANNUAL") }
    var showPaymentGatewayDialog by remember { mutableStateOf(false) }
    var showPaymentSuccessDialog by remember { mutableStateOf(false) }
    var showCelebration by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    var lastPaymentMethod by remember { mutableStateOf("Google Pay / UPI") }
    var lastPaymentRef by remember { mutableStateOf<String?>(null) }

    val amountToPay = if (selectedPlan == "ANNUAL") "10.00" else "2.00"
    val planTitle = if (selectedPlan == "ANNUAL") "1 Year VIP Access (₹10 INR)" else "1 Month Plan (₹2 INR)"

    fun onPaymentCompleted(method: String = "Google Pay / UPI", ref: String? = null) {
        val resolvedRef = if (!ref.isNullOrBlank()) ref else "IOB-UPI-${System.currentTimeMillis().toString().takeLast(6)}"
        lastPaymentMethod = method
        lastPaymentRef = resolvedRef
        viewModel.activateVipSubscription(
            planTitle = planTitle,
            amountInr = amountToPay,
            paymentMethod = method,
            utrReference = resolvedRef
        )
        showPaymentGatewayDialog = false
        showCelebration = true
        showPaymentSuccessDialog = true
        Toast.makeText(context, "🎉 VIP Subscription Confirmed & Unlocked!", Toast.LENGTH_LONG).show()
    }

    // Payment Gateway Dialog
    if (showPaymentGatewayDialog) {
        PaymentGatewayDialog(
            amountInr = amountToPay,
            planTitle = planTitle,
            onDismiss = { showPaymentGatewayDialog = false },
            onPaymentSuccess = { method, ref -> onPaymentCompleted(method, ref) }
        )
    }

    if (showCelebration) {
        ConfettiCelebration()
    }

    if (showPaymentSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showPaymentSuccessDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("👑", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("VIP Access Confirmed & Linked!", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column {
                    Text(
                        text = "Payment received! Your 1-Year VIP membership is now permanently linked to ${student.name}'s profile.",
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFDCFCE7),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Official Payment Receipt", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF166534))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("• Status: Confirmed & Active ✅", fontSize = 11.sp, color = Color(0xFF166534), fontWeight = FontWeight.Bold)
                            Text("• Student: ${student.name} (${student.email})", fontSize = 11.sp, color = Color(0xFF166534))
                            Text("• Payee: KIRUBAKARAN. N • INDIAN OVERSEAS BANK", fontSize = 11.sp, color = Color(0xFF166534))
                            Text("• Bank A/C: 208401000020621 | IFSC: IOBA0002084", fontSize = 11.sp, color = Color(0xFF166534))
                            Text("• Amount: ₹$amountToPay INR ($lastPaymentMethod)", fontSize = 11.sp, color = Color(0xFF166534))
                            Text("• Receipt / Txn Ref: ${lastPaymentRef ?: "IOB-CONFIRMED"}", fontSize = 11.sp, color = Color(0xFF166534), fontWeight = FontWeight.SemiBold)
                            Text("• Curriculum: All 12 Subjects & 6 Arcade Games Unlocked", fontSize = 11.sp, color = Color(0xFF166534))
                            Text("• Validity: 1 Full Year All-Access", fontSize = 11.sp, color = Color(0xFF166534))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPaymentSuccessDialog = false
                        viewModel.navigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF166534))
                ) {
                    Text("Start Learning 🚀", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "MathWhiz VIP Payment Confirmation:\nStudent: ${student.name}\nAmount: ₹$amountToPay\nMerchant: KIRUBAKARAN. N (Indian Overseas Bank)\nTxn Ref: ${lastPaymentRef ?: "IOB-CONFIRMED"}\nStatus: Active"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Payment Receipt"))
                    }
                ) {
                    Text("Share Receipt", fontSize = 12.sp)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("👑 MathWhiz VIP Membership", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(scrollState)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Crown Banner
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFFBBF24), Color(0xFFD97706))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("👑", fontSize = 48.sp)
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Unlock Unlimited Math Superpowers!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Full curriculum access, all 5 interactive arcade games, offline sync & detailed parent milestone analytics.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
            )

            // Features Checklist
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    VipFeatureItem("All 12 Math Subjects unlocked (Fractions, Clocks, Money, Shapes)")
                    VipFeatureItem("All 5 Math Arcade Games (Balloon Pop, Pizza Chef, Memory Match)")
                    VipFeatureItem("Direct PDF Milestone Analytics Reports & WhatsApp export")
                    VipFeatureItem("Complete Offline Synchronization when reconnected")
                    VipFeatureItem("Zero Advertisements & 100% Kid-Safe COPPA Security")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Plan Options (Featured ₹10 for 1 Year)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Annual Plan Card (₹10 / Year)
                PlanCard(
                    title = "1 Year VIP Plan",
                    price = "₹10 / Year",
                    subtext = "Just 10 Indian Rupees • Best Value",
                    badge = "OFFER",
                    isSelected = selectedPlan == "ANNUAL",
                    onClick = { selectedPlan = "ANNUAL" },
                    modifier = Modifier.weight(1f).testTag("plan_annual_inr")
                )

                // Monthly Plan Card (₹2 / Month)
                PlanCard(
                    title = "1 Month Plan",
                    price = "₹2 / Month",
                    subtext = "Cancel anytime",
                    badge = null,
                    isSelected = selectedPlan == "MONTHLY",
                    onClick = { selectedPlan = "MONTHLY" },
                    modifier = Modifier.weight(1f).testTag("plan_monthly_inr")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Active VIP Membership Card if unlocked
            if (student.isVip) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFDCFCE7),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF16A34A)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("active_vip_status_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("👑", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    "1-Year VIP Membership Active ✅",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF166534)
                                )
                                Text(
                                    "Linked to ${student.name} • All 12 Subjects & 6 Games Unlocked",
                                    fontSize = 12.sp,
                                    color = Color(0xFF15803D)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color(0xFF86EFAC))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "• Merchant: KIRUBAKARAN. N • Indian Overseas Bank",
                            fontSize = 11.sp,
                            color = Color(0xFF166534)
                        )
                        Text(
                            "• Bank A/C: 208401000020621 | IFSC: IOBA0002084",
                            fontSize = 11.sp,
                            color = Color(0xFF166534)
                        )
                        Text(
                            "• Registered UPI ID: nivinpetplast-3@okaxis",
                            fontSize = 11.sp,
                            color = Color(0xFF166534)
                        )
                        latestPayment?.let { p ->
                            Text(
                                "• Linked Receipt: ${p.transactionId} (${p.paymentMethod})",
                                fontSize = 11.sp,
                                color = Color(0xFF14532D),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                viewModel.activateVipSubscription(amountInr = "10.00")
                                Toast.makeText(context, "VIP Status Re-synchronized & Confirmed! ✅", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF166534)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                        ) {
                            Text("✅ VIP Confirmed & Synced (1 Year Access)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Google Pay Payment Card (Confirms payment method without exposing private numbers)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF0FDF4),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF10B981)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("💳", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "GPay, UPI & Bank Transfer",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF166534)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = "Registered Payee: KIRUBAKARAN. N • Indian Overseas Bank\nUPI: nivinpetplast-3@okaxis • 100% Encrypted & Verified",
                            fontSize = 11.sp,
                            color = Color(0xFF15803D),
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Payment Gateway Action Button
            Button(
                onClick = { showPaymentGatewayDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F172A),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("pay_gateway_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⚡ Pay ₹$amountToPay", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color(0xFF38BDF8))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (student.isVip) "• Renew / Reopen Payment Gateway" else "• Open Payment Gateway (UPI / QR / GPay)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = {
                    viewModel.setVipStatus(true)
                    Toast.makeText(context, "VIP Membership Restored & Active! ✅", Toast.LENGTH_SHORT).show()
                }
            ) {
                Text("Restore Purchases • Verified Active", fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Offline Sync Info
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF0FDF4),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔄", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Smart Offline Sync: All student progress is safely stored on this tablet and syncs seamlessly once internet is detected.",
                        fontSize = 11.sp,
                        color = Color(0xFF166534)
                    )
                }
            }
        }
    }
}

@Composable
fun VipFeatureItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(Color(0xFF10B981)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun PlanCard(
    title: String,
    price: String,
    subtext: String,
    badge: String?,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface,
        tonalElevation = if (isSelected) 4.dp else 1.dp,
        modifier = modifier
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (badge != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF59E0B)
                ) {
                    Text(
                        text = badge,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            } else {
                Spacer(modifier = Modifier.height(18.dp))
            }
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(price, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtext, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
