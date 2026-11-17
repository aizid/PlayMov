package com.aizid.playmov.ui.auth.otp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aizid.playmov.ui.auth.components.PlayMovPrimaryButton
import com.aizid.playmov.ui.theme.*
import com.aizid.playmov.utils.toast.Toast
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OtpVerificationScreen(
    email: String,
    phoneNumber: String = "",
    name: String = "",
    registrationToken: String = "",
    onVerificationSuccess: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: OtpViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.show("Account verified successfully! Please sign in.")
            onVerificationSuccess()
            viewModel.resetSuccess()
        }
    }

    OtpVerificationContent(
        destination = phoneNumber.ifBlank { email },
        uiState = uiState,
        onOtpChange = viewModel::onOtpChange,
        onVerifyClick = {
            viewModel.onVerifyClick(
                code = uiState.otpCode,
                phoneNumber = phoneNumber,
                name = name,
                email = email,
                registrationToken = registrationToken
            )
        },
        onResendClick = { viewModel.onResendOtpClick(phoneNumber) },
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerificationContent(
    destination: String,
    uiState: OtpUiState,
    onOtpChange: (String) -> Unit,
    onVerifyClick: () -> Unit,
    onResendClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PlayMovTextMain
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PlayMovBgBase
                )
            )
        },
        containerColor = PlayMovBgBase
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Security Shield Icon Badge
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                PlayMovPrimary.copy(alpha = 0.25f),
                                PlayMovSurfaceCard.copy(alpha = 0.8f)
                            )
                        )
                    )
                    .border(1.5.dp, PlayMovBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = PlayMovPrimary,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = "Verification Code",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PlayMovTextMain,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "We have sent a 6-digit verification code to:",
                fontSize = 14.sp,
                color = PlayMovTextMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                color = PlayMovSurfaceCard,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PlayMovBorder)
            ) {
                Text(
                    text = destination,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlayMovPrimary,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Hidden BasicTextField capturing keyboard inputs layered with 6 visual digit boxes
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                BasicTextField(
                    value = uiState.otpCode,
                    onValueChange = onOtpChange,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onVerifyClick() }
                    ),
                    cursorBrush = SolidColor(Color.Transparent),
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .size(1.dp)
                )

                // Visual 6 Digit Boxes
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(uiState.otpLength) { index ->
                        val char = uiState.otpCode.getOrNull(index)?.toString().orEmpty()
                        val isFocused = uiState.otpCode.length == index ||
                                (index == uiState.otpLength - 1 && uiState.otpCode.length == uiState.otpLength)

                        Box(
                            modifier = Modifier
                                .width(46.dp)
                                .height(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(PlayMovSurfaceCard)
                                .border(
                                    width = if (isFocused) 2.dp else 1.dp,
                                    color = when {
                                        uiState.errorMessage != null -> PlayMovHeartPink
                                        isFocused -> PlayMovPrimary
                                        char.isNotEmpty() -> PlayMovAccent
                                        else -> PlayMovBorder
                                    },
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { focusRequester.requestFocus() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = char,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = PlayMovTextMain,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Error Message (if any)
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    color = PlayMovHeartPink,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Resend Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                if (uiState.canResend) {
                    Text(
                        text = "Didn't receive code? ",
                        color = PlayMovTextMuted,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Resend Code",
                        color = PlayMovPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            Toast.show("New verification code requested!")
                            onResendClick()
                        }
                    )
                } else {
                    val minutes = uiState.resendCountdown / 60
                    val seconds = uiState.resendCountdown % 60
                    val formatted = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
                    Text(
                        text = "Resend code in ",
                        color = PlayMovTextMuted,
                        fontSize = 14.sp
                    )
                    Text(
                        text = formatted,
                        color = PlayMovPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Confirm / Verify Action Button
            PlayMovPrimaryButton(
                text = "Verify Code",
                onClick = onVerifyClick,
                isLoading = uiState.isLoading,
                enabled = uiState.otpCode.length == uiState.otpLength,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
