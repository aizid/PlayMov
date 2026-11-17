package com.aizid.playmov.ui.auth.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aizid.playmov.ui.auth.components.PlayMovAuthTextField
import com.aizid.playmov.ui.auth.components.PlayMovPasswordTextField
import com.aizid.playmov.ui.auth.components.PlayMovPrimaryButton
import com.aizid.playmov.ui.theme.*
import com.aizid.playmov.utils.toast.Toast
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: (email: String, phoneNumber: String, name: String, registrationToken: String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: RegisterViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.registeredEmail, uiState.registeredPhone) {
        val email = uiState.registeredEmail
        val phone = uiState.registeredPhone
        val name = uiState.registeredName.orEmpty()
        val token = uiState.registrationToken.orEmpty()
        if (phone != null) {
            Toast.show("Verification code sent to $phone")
            onRegisterSuccess(email.orEmpty(), phone, name, token)
            viewModel.resetRegisteredEmail()
        }
    }

    RegisterContent(
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onPhoneNumberChange = viewModel::onPhoneNumberChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onRegisterClick = viewModel::onRegisterClick,
        onNavigateToLogin = onNavigateToLogin,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterContent(
    uiState: RegisterUiState,
    onNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()

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
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Heading & Subtitle
            Text(
                text = "Create Account",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PlayMovTextMain,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Join PlayMov to explore movies, stream official trailers, and curate your watchlist.",
                fontSize = 14.sp,
                color = PlayMovTextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Name Field
            PlayMovAuthTextField(
                value = uiState.name,
                onValueChange = onNameChange,
                label = "Full Name",
                placeholder = "John Doe",
                leadingIcon = Icons.Default.Person,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                isError = uiState.nameError != null,
                errorMessage = uiState.nameError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Number Field (Required for GraphQL Register)
            PlayMovAuthTextField(
                value = uiState.phoneNumber,
                onValueChange = onPhoneNumberChange,
                label = "Phone Number",
                placeholder = "+6281234567890",
                leadingIcon = Icons.Default.Phone,
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next,
                isError = uiState.phoneNumberError != null,
                errorMessage = uiState.phoneNumberError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Field
            PlayMovAuthTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = "Email Address (Optional)",
                placeholder = "name@example.com",
                leadingIcon = Icons.Default.Email,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                isError = uiState.emailError != null,
                errorMessage = uiState.emailError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            PlayMovPasswordTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = "Password",
                placeholder = "At least 6 characters",
                leadingIcon = Icons.Default.Lock,
                imeAction = ImeAction.Next,
                isError = uiState.passwordError != null,
                errorMessage = uiState.passwordError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Field
            PlayMovPasswordTextField(
                value = uiState.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = "Confirm Password",
                placeholder = "Re-enter your password",
                leadingIcon = Icons.Default.Lock,
                imeAction = ImeAction.Done,
                onImeAction = onRegisterClick,
                isError = uiState.confirmPasswordError != null,
                errorMessage = uiState.confirmPasswordError
            )

            Spacer(modifier = Modifier.height(24.dp))

            // General Error Banner (if any)
            if (uiState.generalError != null) {
                Surface(
                    color = PlayMovHeartPink.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PlayMovHeartPink),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = uiState.generalError,
                        color = PlayMovHeartPink,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Create Account Action Button
            PlayMovPrimaryButton(
                text = "Create Account",
                onClick = onRegisterClick,
                isLoading = uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Footer navigation to Login
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Text(
                    text = "Already have an account? ",
                    color = PlayMovTextMuted,
                    fontSize = 14.sp
                )
                Text(
                    text = "Sign In",
                    color = PlayMovPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}
