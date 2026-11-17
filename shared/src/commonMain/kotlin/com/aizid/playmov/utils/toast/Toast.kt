package com.aizid.playmov.utils.toast

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import com.aizid.playmov.ui.theme.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import playmov.shared.generated.resources.Res
import playmov.shared.generated.resources.close
import playmov.shared.generated.resources.information_circle

object Toast {
    var message by mutableStateOf<String?>(null)
        private set
    var title by mutableStateOf<String?>(null)
        private set
    var content by mutableStateOf<(@Composable ColumnScope.() -> Unit)?>(null)
        private set
    var icon by mutableStateOf<DrawableResource>(Res.drawable.information_circle)
        private set

    private val scope = CoroutineScope(Dispatchers.Main)

    fun show(message: String, title: String? = null, icon: DrawableResource = Res.drawable.information_circle) {
        this.message = message
        this.title = title
        this.content = null
        this.icon = icon

        launchHideTimer()
    }

    fun show(title: String, icon: DrawableResource = Res.drawable.information_circle, content: @Composable ColumnScope.() -> Unit) {
        this.message = null
        this.title = title
        this.content = content
        this.icon = icon

        launchHideTimer()
    }

    private fun launchHideTimer() {
        scope.launch {
            delay(3000)
            hide()
        }
    }

    fun hide() {
        message = null
        title = null
        content = null
        icon = Res.drawable.information_circle
    }
}

@Composable
fun ToastHost() {
    val message = Toast.message
    val title = Toast.title
    val content = Toast.content
    val icon = Toast.icon

    // Store local state to keep values during fade out animation
    var displayMessage by remember { mutableStateOf<String?>(null) }
    var displayTitle by remember { mutableStateOf<String?>(null) }
    var displayContent by remember { mutableStateOf<(@Composable ColumnScope.() -> Unit)?>(null) }
    var displayIcon by remember { mutableStateOf<DrawableResource>(Res.drawable.information_circle) }

    val isVisible = message != null || content != null

    if (isVisible) {
        displayMessage = message
        displayTitle = title
        displayContent = content
        displayIcon = icon
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 32.dp)
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(8.dp))
                    .background(BuyGreenLight, RoundedCornerShape(8.dp))
                    .border(1.dp, ToastSuccessBorder, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = if (displayTitle != null) Alignment.Top else Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            painter = painterResource(displayIcon),
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            if (displayTitle != null) {
                                Text(
                                    text = displayTitle!!,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextHeading
                                )
                            }
                            if (displayContent != null) {
                                displayContent!!()
                            } else if (displayMessage != null) {
                                Text(
                                    text = displayMessage!!,
                                    fontSize = 14.sp,
                                    color = if (displayTitle != null) TextDescription else TextBody
                                )
                            }
                        }
                        IconButton(
                            onClick = { Toast.hide() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.close),
                                contentDescription = "Close",
                                modifier = Modifier.size(16.dp),
                                tint = TextHeading
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ToastPreview() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
            // Kita panggil show secara manual untuk keperluan preview
            SideEffect {
                Toast.show("Withdraw is in progress")
            }
            ToastHost()
        }
    }
}
