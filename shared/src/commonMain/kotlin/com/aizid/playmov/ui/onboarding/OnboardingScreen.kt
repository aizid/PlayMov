package com.aizid.playmov.ui.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aizid.playmov.ui.auth.components.PlayMovPrimaryButton
import com.aizid.playmov.ui.theme.*
import kotlinx.coroutines.launch

data class OnboardingSlide(
    val title: String,
    val subtitle: String,
    val badge: String,
    val icon: ImageVector
)

val onboardingSlides = listOf(
    OnboardingSlide(
        title = "Unlimited Movies\n& TV Series",
        subtitle = "Discover thousands of trending releases, blockbusters, and award-winning cinema updated live from TMDB.",
        badge = "Global Discovery",
        icon = Icons.Default.Movie
    ),
    OnboardingSlide(
        title = "Official Trailers\n& Deep Reviews",
        subtitle = "Watch high-definition YouTube trailers instantly and read authentic reviews from global movie critics.",
        badge = "Stream & Community",
        icon = Icons.Default.PlayCircleFilled
    ),
    OnboardingSlide(
        title = "Curate Your\nPersonal Watchlist",
        subtitle = "Save your favorite movies securely to local storage and build your personalized cinema catalog.",
        badge = "SQLite Offline Ready",
        icon = Icons.Default.Bookmark
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onGetStartedClick: () -> Unit,
    onSignInClick: () -> Unit,
    onSkipClick: () -> Unit = onGetStartedClick
) {
    val pagerState = rememberPagerState(pageCount = { onboardingSlides.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = PlayMovBgBase
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Logo & Skip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(PlayMovPrimary, PlayMovAccent)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "PlayMov Logo",
                            tint = Color(0xFF0A0E17),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "PlayMov",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PlayMovTextMain,
                        letterSpacing = (-0.5).sp
                    )
                }

                Text(
                    text = "Skip",
                    color = PlayMovTextMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSkipClick() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.weight(0.15f))

            // Carousel Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                val slide = onboardingSlides[page]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Feature Icon Art
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        PlayMovPrimary.copy(alpha = 0.25f),
                                        PlayMovSurfaceCard.copy(alpha = 0.8f)
                                    )
                                )
                            )
                            .border(1.5.dp, PlayMovBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = slide.icon,
                            contentDescription = null,
                            tint = PlayMovPrimary,
                            modifier = Modifier.size(64.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Badge Pill
                    Surface(
                        color = PlayMovSurfaceCard,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PlayMovBorder)
                    ) {
                        Text(
                            text = slide.badge,
                            color = PlayMovPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Title
                    Text(
                        text = slide.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PlayMovTextMain,
                        textAlign = TextAlign.Center,
                        lineHeight = 34.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Subtitle
                    Text(
                        text = slide.subtitle,
                        fontSize = 14.sp,
                        color = PlayMovTextMuted,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Page Indicator Dots
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 28.dp)
            ) {
                repeat(onboardingSlides.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    val width by animateDpAsState(targetValue = if (isSelected) 28.dp else 8.dp)
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(if (isSelected) PlayMovPrimary else PlayMovBorder)
                    )
                }
            }

            // Bottom Actions: Get Started Button
            PlayMovPrimaryButton(
                text = "Get Started",
                onClick = onGetStartedClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Secondary: Already have an account? Sign In
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
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
                    modifier = Modifier.clickable { onSignInClick() }
                )
            }
        }
    }
}
