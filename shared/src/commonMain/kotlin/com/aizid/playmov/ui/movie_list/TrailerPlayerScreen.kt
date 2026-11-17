package com.aizid.playmov.ui.movie_list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aizid.playmov.ui.theme.*
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrailerPlayerScreen(
    videoKey: String,
    title: String,
    onBackClick: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val isValidKey = videoKey.isNotBlank() && videoKey != "default_key"
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Official Trailer",
                        fontWeight = FontWeight.Bold,
                        color = PlayMovTextMain
                    )
                },
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
                    containerColor = PlayMovSurface
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
                .padding(16.dp)
        ) {
            if (isValidKey) {
                // HTML5 Privacy-Enhanced YouTube Embed with strict origin & referrer configuration to mitigate Error 152-4
                val htmlData = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                        <meta name="referrer" content="strict-origin-when-cross-origin">
                        <style>
                            * { margin: 0; padding: 0; box-sizing: border-box; background-color: #000000; }
                            html, body { width: 100%; height: 100%; overflow: hidden; display: flex; align-items: center; justify-content: center; }
                            iframe { width: 100%; height: 100%; border: 0; }
                        </style>
                    </head>
                    <body>
                        <iframe
                            src="https://www.youtube-nocookie.com/embed/$videoKey?autoplay=1&playsinline=1&enablejsapi=1&origin=https://com.aizid.playmov&widget_referrer=https://com.aizid.playmov&rel=0&modestbranding=1"
                            referrerpolicy="strict-origin-when-cross-origin"
                            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                            allowfullscreen>
                        </iframe>
                    </body>
                    </html>
                """.trimIndent()

                val webViewState = rememberWebViewStateWithHTMLData(
                    data = htmlData,
                    baseUrl = "https://com.aizid.playmov"
                )

                // Configure platform web settings with desktop/mobile user agent to bypass WebView in-app restrictions
                webViewState.webSettings.apply {
                    isJavaScriptEnabled = true
                    customUserAgentString = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Mobile Safari/537.36"
                    androidWebSettings.apply {
                        domStorageEnabled = true
                        mediaPlaybackRequiresUserGesture = false
                        allowFileAccess = true
                    }
                    iOSWebSettings.apply {
                        bounces = false
                    }
                }

                // Video Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black)
                        .border(1.dp, PlayMovBorder, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    WebView(
                        state = webViewState,
                        modifier = Modifier.fillMaxSize()
                    )

                    if (webViewState.isLoading) {
                        CircularProgressIndicator(
                            color = PlayMovPrimary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Title & Details
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PlayMovTextMain,
                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Streaming official preview via YouTube • Video ID: $videoKey",
                    fontSize = 13.sp,
                    color = PlayMovTextMuted
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Primary Button: Open directly in YouTube App
                Button(
                    onClick = {
                        uriHandler.openUri("https://www.youtube.com/watch?v=$videoKey")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PlayMovPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Watch on YouTube App",
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Informational Notice Card regarding Studio Embedding Restrictions (Error 152 / 150)
                Surface(
                    color = PlayMovSurfaceCard,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PlayMovBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = PlayMovPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Notice about Video Unavailable (Error 152 / 150)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PlayMovTextMain
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Certain movie production studios disable third-party in-app embedding for official trailers. If playback is blocked, tap \"Watch on YouTube App\" above to watch the trailer directly in full resolution.",
                                fontSize = 12.sp,
                                color = PlayMovTextMuted,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            } else {
                // Empty / Unavailable State
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(PlayMovSurfaceCard),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = null,
                            tint = PlayMovTextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Trailer Unavailable",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlayMovTextMain
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "No official video trailer has been provided for \"$title\".",
                        fontSize = 14.sp,
                        color = PlayMovTextMuted,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onBackClick,
                        colors = ButtonDefaults.buttonColors(containerColor = PlayMovPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Go Back",
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
