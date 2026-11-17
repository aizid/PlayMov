package com.aizid.playmov.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aizid.playmov.remote.dto.account.AccountDetails
import com.aizid.playmov.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onLogoutSuccess: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onLogoutSuccess()
        }
    }

    ProfileContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onLogoutClick = viewModel::logout,
        onRetryClick = viewModel::loadProfile
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileContent(
    uiState: ProfileUiState,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onRetryClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "User Profile",
                        fontWeight = FontWeight.ExtraBold,
                        color = PlayMovTextMain,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PlayMovPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PlayMovSurface
                )
            )
        },
        containerColor = PlayMovBgBase
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading && uiState.accountDetails == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PlayMovPrimary
                )
            } else if (uiState.errorMessage != null && uiState.accountDetails == null) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.errorMessage,
                        color = PlayMovHeartPink,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRetryClick,
                        colors = ButtonDefaults.buttonColors(containerColor = PlayMovPrimary)
                    ) {
                        Text("Retry", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                val details = uiState.accountDetails
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Avatar Circle
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(PlayMovPrimary, PlayMovAccent)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Avatar",
                            tint = Color(0xFF0A0E17),
                            modifier = Modifier.size(56.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Name
                    Text(
                        text = details?.name.orEmpty().ifBlank { "User" },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PlayMovTextMain,
                        textAlign = TextAlign.Center
                    )

                    // Email (if available)
                    if (!details?.email.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = details.email.orEmpty(),
                            fontSize = 14.sp,
                            color = PlayMovTextMuted,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Account ID Chip
                    if (!details?.accId.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = PlayMovSurfaceCard,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PlayMovBorder)
                        ) {
                            Text(
                                text = "ID: ${details.accId}",
                                color = PlayMovPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Info Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PlayMovSurfaceCard),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PlayMovBorder)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Account Information",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PlayMovTextMain,
                                modifier = Modifier.padding(bottom = 14.dp)
                            )

                            // Name Row
                            ProfileInfoRow(
                                label = "Full Name",
                                value = details?.name.orEmpty().ifBlank { "-" }
                            )

                            // Email Row
                            if (!details?.email.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                ProfileInfoRow(
                                    label = "Email Address",
                                    value = details.email.orEmpty()
                                )
                            }

                            // Phone Number Row
                            if (!details?.phoneNumber.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                ProfileInfoRow(
                                    label = "Phone Number",
                                    value = details.phoneNumber.orEmpty()
                                )
                            }

                            // NIP Row
                            if (!details?.nip.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                ProfileInfoRow(
                                    label = "NIP",
                                    value = details.nip.orEmpty()
                                )
                            }
                        }
                    }

                    // Roles Section
                    if (!details?.roles.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = PlayMovSurfaceCard),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PlayMovBorder)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "Roles",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PlayMovTextMain,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    details.roles.forEach { role ->
                                        Surface(
                                            color = PlayMovPrimary.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(8.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, PlayMovPrimary.copy(alpha = 0.4f))
                                        ) {
                                            Text(
                                                text = role.roleDefName,
                                                color = PlayMovPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Permissions Section
                    if (!details?.permissions.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = PlayMovSurfaceCard),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PlayMovBorder)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "Permissions",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PlayMovTextMain,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    details.permissions.forEach { perm ->
                                        Surface(
                                            color = PlayMovSurface,
                                            shape = RoundedCornerShape(8.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, PlayMovBorder)
                                        ) {
                                            Text(
                                                text = "${perm.permResource}:${perm.permAction}",
                                                color = PlayMovTextMuted,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Logout Button
                    Button(
                        onClick = onLogoutClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PlayMovHeartPink.copy(alpha = 0.15f),
                            contentColor = PlayMovHeartPink
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PlayMovHeartPink.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Log Out",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlayMovHeartPink
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = PlayMovTextMuted
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = PlayMovTextMain
        )
    }
}
