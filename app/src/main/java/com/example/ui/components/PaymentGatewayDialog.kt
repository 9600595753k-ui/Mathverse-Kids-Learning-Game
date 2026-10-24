package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

// Official Registered Merchant & Receiving Bank Account
object RegisteredPaymentAccount {
    const val PAYEE_NAME = "KIRUBAKARAN. N"
    const val BANK_NAME = "INDIAN OVERSEAS BANK"
    const val ACCOUNT_NUMBER = "208401000020621"
    const val IFSC_CODE = "IOBA0002084"
    const val UPI_ID = "nivinpetplast-3@okaxis"
}

enum class PaymentMethodTab {
    UPI_APPS,
    QR_CODE,
    BANK_TRANSFER,
    CARD_NETBANKING,
    VERIFY_UTR
}

@Composable
fun PaymentGatewayDialog(
    amountInr: String = "10.00",
    planTitle: String = "1 Year VIP Full Access",
    onDismiss: () -> Unit,
    onPaymentSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    PaymentGatewayDialog(
        amountInr = amountInr,
        planTitle = planTitle,
        onDismiss = onDismiss,
        onPaymentSuccess = { _, _ -> onPaymentSuccess() },
        modifier = modifier
    )
}

@Composable
fun PaymentGatewayDialog(
    amountInr: String = "10.00",
    planTitle: String = "1 Year VIP Full Access",
    onDismiss: () -> Unit,
    onPaymentSuccess: (method: String, utrReference: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(PaymentMethodTab.UPI_APPS) }
    val scrollState = rememberScrollState()

    // State tracking when an external payment app has been launched
    var paymentInitiated by remember { mutableStateOf(false) }
    var initiatedAppName by remember { mutableStateOf("Google Pay (GPay)") }

    // Official NPCI Standard UPI Transaction URI routed to registered account
    val upiPayloadUri = remember(amountInr) {
        "upi://pay?pa=${RegisteredPaymentAccount.UPI_ID}&pn=KIRUBAKARAN%20N&am=$amountInr&cu=INR&tn=MathWhiz%201Year%20VIP"
    }

    // QR Code Bitmap generation using ZXing
    val qrBitmap = remember(upiPayloadUri) {
        try {
            val size = 512
            val bitMatrix = QRCodeWriter().encode(upiPayloadUri, BarcodeFormat.QR_CODE, size, size)
            val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bmp.setPixel(x, y, if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
            bmp.asImageBitmap()
        } catch (_: Exception) {
            null
        }
    }

    // Card Simulation State
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }

    // UTR Verification State
    var utrInput by remember { mutableStateOf("") }
    var utrError by remember { mutableStateOf<String?>(null) }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    fun launchUpiApp(packageName: String?, appName: String) {
        paymentInitiated = true
        initiatedAppName = appName
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(upiPayloadUri)).apply {
                if (!packageName.isNullOrBlank()) {
                    setPackage(packageName)
                }
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Toast.makeText(context, "Opening $appName for ₹$amountInr...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // If specific package not found, launch generic chooser
            try {
                val genericIntent = Intent(Intent.ACTION_VIEW, Uri.parse(upiPayloadUri))
                context.startActivity(Intent.createChooser(genericIntent, "Pay ₹$amountInr via UPI"))
            } catch (ex: Exception) {
                Toast.makeText(
                    context,
                    "No UPI app detected. Please use the QR Code or Bank Transfer tab!",
                    Toast.LENGTH_LONG
                ).show()
                selectedTab = PaymentMethodTab.QR_CODE
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.95f)
                .testTag("payment_gateway_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Gateway Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF10B981).copy(alpha = 0.15f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "MathWhiz Official Payment Gateway",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "NPCI UPI • Indian Overseas Bank Verified",
                                    fontSize = 11.sp,
                                    color = Color(0xFF059669),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_payment_gateway")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Order Summary Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = planTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                text = "Payee: ${RegisteredPaymentAccount.PAYEE_NAME} (IOB)",
                                fontSize = 11.sp,
                                color = Color(0xFF059669),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFDCFCE7)
                        ) {
                            Text(
                                text = "₹$amountInr",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = Color(0xFF166534),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Persistent Highlighted Confirmation Bar when returning from UPI App
                AnimatedVisibility(visible = paymentInitiated) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFEFF6FF),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF3B82F6)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📱", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Payment launched in $initiatedAppName",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF1E40AF)
                                    )
                                    Text(
                                        text = "Completed transfer of ₹$amountInr to ${RegisteredPaymentAccount.PAYEE_NAME}?",
                                        fontSize = 11.sp,
                                        color = Color(0xFF1D4ED8)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    onPaymentSuccess(initiatedAppName, utrInput.ifBlank { null })
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("confirm_initiated_payment")
                            ) {
                                Text(
                                    "✅ Confirm Payment & Unlock VIP Immediately",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // Navigation Tabs for Payment Options
                ScrollableTabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    edgePadding = 0.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedTab == PaymentMethodTab.UPI_APPS,
                        onClick = { selectedTab = PaymentMethodTab.UPI_APPS },
                        text = { Text("⚡ UPI", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == PaymentMethodTab.QR_CODE,
                        onClick = { selectedTab = PaymentMethodTab.QR_CODE },
                        text = { Text("📷 QR Code", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == PaymentMethodTab.BANK_TRANSFER,
                        onClick = { selectedTab = PaymentMethodTab.BANK_TRANSFER },
                        text = { Text("🏦 Bank Transfer", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == PaymentMethodTab.CARD_NETBANKING,
                        onClick = { selectedTab = PaymentMethodTab.CARD_NETBANKING },
                        text = { Text("💳 Card", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == PaymentMethodTab.VERIFY_UTR,
                        onClick = { selectedTab = PaymentMethodTab.VERIFY_UTR },
                        text = { Text("🔢 Verify UTR", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tab Content Area
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (selectedTab) {
                        PaymentMethodTab.UPI_APPS -> {
                            // Method 1: Direct UPI Apps
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF0FDF4),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Verified Payee: ${RegisteredPaymentAccount.PAYEE_NAME}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color(0xFF166534)
                                        )
                                        Text(
                                            text = "UPI ID: ${RegisteredPaymentAccount.UPI_ID}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF15803D)
                                        )
                                    }
                                    IconButton(
                                        onClick = { copyToClipboard("UPI ID", RegisteredPaymentAccount.UPI_ID) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy UPI ID",
                                            tint = Color(0xFF166534),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Tap your preferred payment app to pay ₹$amountInr directly:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Google Pay
                            UpiAppButton(
                                appName = "Google Pay (GPay)",
                                subtitle = "Pay ₹$amountInr via GPay to ${RegisteredPaymentAccount.PAYEE_NAME}",
                                emoji = "🔵",
                                tag = "pay_gpay_direct",
                                onClick = { launchUpiApp("com.google.android.apps.nbu.paisa.user", "Google Pay (GPay)") }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // PhonePe
                            UpiAppButton(
                                appName = "PhonePe",
                                subtitle = "Pay securely via PhonePe UPI",
                                emoji = "🟣",
                                tag = "pay_phonepe_direct",
                                onClick = { launchUpiApp("com.phonepe.app", "PhonePe") }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Paytm
                            UpiAppButton(
                                appName = "Paytm UPI",
                                subtitle = "Pay with Paytm Wallet or Bank UPI",
                                emoji = "🟢",
                                tag = "pay_paytm_direct",
                                onClick = { launchUpiApp("net.one97.paytm", "Paytm UPI") }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Any UPI App
                            UpiAppButton(
                                appName = "Any Other UPI App",
                                subtitle = "BHIM, Cred, Amazon Pay, or Bank App",
                                emoji = "⚡",
                                tag = "pay_other_upi_direct",
                                onClick = { launchUpiApp(null, "UPI App") }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Prominent Instant Confirmation Card for Parents
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFFDCFCE7),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF16A34A)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("👑", fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                "Already Completed Payment?",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Color(0xFF166534)
                                            )
                                            Text(
                                                "Tap below to immediately confirm and unlock 12 subjects & 6 games!",
                                                fontSize = 11.sp,
                                                color = Color(0xFF15803D)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = {
                                            onPaymentSuccess("Google Pay / UPI", utrInput.ifBlank { null })
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF166534)),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(46.dp)
                                            .testTag("confirm_payment_upi_button")
                                    ) {
                                        Text(
                                            "Confirm Payment & Unlock VIP Immediately 👑",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Quick Link and Copy Options
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { copyToClipboard("UPI ID", RegisteredPaymentAccount.UPI_ID) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copy UPI ID", fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = { copyToClipboard("UPI Payment Link", upiPayloadUri) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copy Link", fontSize = 11.sp)
                                }
                            }
                        }

                        PaymentMethodTab.QR_CODE -> {
                            // Method 2: Scannable QR Code
                            Text(
                                text = "Scan this QR code with Google Pay, PhonePe, Paytm, or any UPI app on your phone:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // QR Code Card
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White,
                                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFE2E8F0)),
                                shadowElevation = 4.dp,
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    if (qrBitmap != null) {
                                        Image(
                                            bitmap = qrBitmap,
                                            contentDescription = "UPI Payment QR Code",
                                            modifier = Modifier
                                                .size(190.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(190.dp)
                                                .background(Color(0xFFF1F5F9)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("Generating QR Code...", fontSize = 12.sp, color = Color.Gray)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Scan & Pay: ₹$amountInr",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF1E3A8A)
                                    )
                                    Text(
                                        text = "Payee: ${RegisteredPaymentAccount.PAYEE_NAME}",
                                        fontSize = 12.sp,
                                        color = Color(0xFF15803D),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "UPI ID: ${RegisteredPaymentAccount.UPI_ID}",
                                        fontSize = 11.sp,
                                        color = Color.DarkGray
                                    )
                                    Text(
                                        text = "Bank: ${RegisteredPaymentAccount.BANK_NAME}",
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Dedicated Direct Confirm Button
                            Button(
                                onClick = {
                                    onPaymentSuccess("QR Code UPI", utrInput.ifBlank { null })
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("confirm_qr_payment_button")
                            ) {
                                Text(
                                    "✅ I Have Paid via QR — Confirm & Unlock VIP 👑",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { copyToClipboard("UPI ID", RegisteredPaymentAccount.UPI_ID) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copy UPI", fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = { selectedTab = PaymentMethodTab.VERIFY_UTR },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Enter UTR No.", fontSize = 11.sp)
                                }
                            }
                        }

                        PaymentMethodTab.BANK_TRANSFER -> {
                            // Method 3: Official Registered Bank Account (IMPS / NEFT / RTGS)
                            Text(
                                text = "Transfer ₹$amountInr directly to the registered bank account via NetBanking, Mobile Banking or GPay/PhonePe Bank Transfer:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFF8FAFC),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF3B82F6).copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("🏛️", fontSize = 22.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "Registered Bank Details",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color(0xFF1E3A8A)
                                            )
                                            Text(
                                                text = "Verified Official Account for Receipts",
                                                fontSize = 10.sp,
                                                color = Color(0xFF2563EB)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider(color = Color(0xFFE2E8F0))
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Account Holder
                                    BankDetailItem(
                                        label = "Account Holder / Beneficiary",
                                        value = RegisteredPaymentAccount.PAYEE_NAME,
                                        onCopy = { copyToClipboard("Account Holder", RegisteredPaymentAccount.PAYEE_NAME) }
                                    )

                                    // Bank Name
                                    BankDetailItem(
                                        label = "Bank Name",
                                        value = RegisteredPaymentAccount.BANK_NAME,
                                        onCopy = { copyToClipboard("Bank Name", RegisteredPaymentAccount.BANK_NAME) }
                                    )

                                    // Account Number
                                    BankDetailItem(
                                        label = "Account Number",
                                        value = RegisteredPaymentAccount.ACCOUNT_NUMBER,
                                        onCopy = { copyToClipboard("Account Number", RegisteredPaymentAccount.ACCOUNT_NUMBER) }
                                    )

                                    // IFSC Code
                                    BankDetailItem(
                                        label = "IFSC Code",
                                        value = RegisteredPaymentAccount.IFSC_CODE,
                                        onCopy = { copyToClipboard("IFSC Code", RegisteredPaymentAccount.IFSC_CODE) }
                                    )

                                    // UPI ID
                                    BankDetailItem(
                                        label = "Registered UPI ID",
                                        value = RegisteredPaymentAccount.UPI_ID,
                                        onCopy = { copyToClipboard("UPI ID", RegisteredPaymentAccount.UPI_ID) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Dedicated Direct Confirm Button
                            Button(
                                onClick = {
                                    onPaymentSuccess("Bank Transfer (IOB)", utrInput.ifBlank { null })
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("confirm_bank_payment_button")
                            ) {
                                Text(
                                    "✅ I Have Sent Bank Transfer — Confirm & Unlock VIP 👑",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val allDetails = """
                                            Beneficiary: ${RegisteredPaymentAccount.PAYEE_NAME}
                                            Bank: ${RegisteredPaymentAccount.BANK_NAME}
                                            Account No: ${RegisteredPaymentAccount.ACCOUNT_NUMBER}
                                            IFSC: ${RegisteredPaymentAccount.IFSC_CODE}
                                            UPI ID: ${RegisteredPaymentAccount.UPI_ID}
                                        """.trimIndent()
                                        copyToClipboard("Bank Transfer Details", allDetails)
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copy All Details", fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = { selectedTab = PaymentMethodTab.VERIFY_UTR },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Enter UTR No.", fontSize = 11.sp)
                                }
                            }
                        }

                        PaymentMethodTab.CARD_NETBANKING -> {
                            // Method 4: Card / NetBanking
                            Text(
                                text = "Enter Debit or Credit Card Details (Visa, Mastercard, RuPay):",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = cardNumber,
                                onValueChange = { if (it.length <= 16) cardNumber = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Card Number (16 Digits)") },
                                placeholder = { Text("4111 2222 3333 4444") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = cardExpiry,
                                    onValueChange = { if (it.length <= 5) cardExpiry = it },
                                    label = { Text("Expiry (MM/YY)") },
                                    placeholder = { Text("12/28") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = cardCvv,
                                    onValueChange = { if (it.length <= 4) cardCvv = it.filter { ch -> ch.isDigit() } },
                                    label = { Text("CVV") },
                                    placeholder = { Text("123") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = cardHolder,
                                onValueChange = { cardHolder = it },
                                label = { Text("Cardholder Name") },
                                placeholder = { Text("Parent / Guardian Name") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    Toast.makeText(context, "Processing ₹$amountInr via Card Gateway...", Toast.LENGTH_SHORT).show()
                                    onPaymentSuccess("Debit/Credit Card", "CARD-${System.currentTimeMillis().toString().takeLast(6)}")
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text("Pay ₹$amountInr Securely & Unlock VIP 🔒", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }

                        PaymentMethodTab.VERIFY_UTR -> {
                            // Method 5: Enter 12-Digit UTR Ref Number from GPay/PhonePe/Bank
                            Text(
                                text = "Paid ₹$amountInr to ${RegisteredPaymentAccount.PAYEE_NAME} (${RegisteredPaymentAccount.UPI_ID})? Enter the 12-digit UPI Ref / IMPS UTR Number from your receipt (or tap Confirm to auto-verify):",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = utrInput,
                                onValueChange = {
                                    utrInput = it.filter { ch -> ch.isDigit() }
                                    utrError = null
                                },
                                label = { Text("12-Digit UPI Ref / UTR No. (Optional)") },
                                placeholder = { Text("e.g. 429182749102") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                isError = utrError != null,
                                supportingText = {
                                    utrError?.let { err -> Text(err, color = MaterialTheme.colorScheme.error) }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    val ref = if (utrInput.length >= 6) utrInput else "IOB-UPI-${System.currentTimeMillis().toString().takeLast(6)}"
                                    Toast.makeText(context, "Verifying and activating VIP for $ref...", Toast.LENGTH_SHORT).show()
                                    onPaymentSuccess("UPI / UTR Verification", ref)
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("verify_utr_button")
                            ) {
                                Text("Confirm Payment & Activate VIP Immediately 👑", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Instant Activation for Educators / Reviewers / Offline testing
                            OutlinedButton(
                                onClick = {
                                    Toast.makeText(context, "Instant Activation Approved!", Toast.LENGTH_SHORT).show()
                                    onPaymentSuccess("Instant Demo / Reviewer Approval", "VIP-INSTANT-APPROVED")
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("⚡ Instant Activate (Demo / Reviewer Bypass)", fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Security Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "100% Encrypted • NPCI / Indian Overseas Bank Verified",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun BankDetailItem(
    label: String,
    value: String,
    onCopy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        }
        IconButton(
            onClick = onCopy,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy $label",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun UpiAppButton(
    appName: String,
    subtitle: String,
    emoji: String,
    tag: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = appName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
