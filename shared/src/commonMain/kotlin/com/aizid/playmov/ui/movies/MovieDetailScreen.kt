package com.aizid.playmov.ui.movies

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aizid.playmov.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MovieDetailScreen(
    movieId: Int,
    onWatchTrailerClick: (videoKey: String, title: String) -> Unit,
    onReviewsClick: (movieId: Int, title: String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: MovieDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(movieId) {
        viewModel.loadMovieDetails(movieId)
    }

    MovieDetailContent(
        uiState = uiState,
        onWatchTrailerClick = onWatchTrailerClick,
        onReviewsClick = onReviewsClick,
        onToggleFavorite = viewModel::onToggleFavorite,
        onRefresh = { viewModel.loadMovieDetails(movieId) },
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailContent(
    uiState: MovieDetailUiState,
    onWatchTrailerClick: (videoKey: String, title: String) -> Unit,
    onReviewsClick: (movieId: Int, title: String) -> Unit,
    onToggleFavorite: () -> Unit,
    onRefresh: () -> Unit,
    onBackClick: () -> Unit
) {
    val pullToRefreshState = androidx.compose.material3.pulltorefresh.rememberPullToRefreshState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Movie Details",
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
                actions = {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (uiState.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (uiState.isFavorite) PlayMovHeartPink else PlayMovTextMain
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
        androidx.compose.material3.pulltorefresh.PullToRefreshBox(
            isRefreshing = uiState.isLoading && uiState.movieDetail != null,
            onRefresh = onRefresh,
            state = pullToRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading && uiState.movieDetail == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PlayMovPrimary
                )
            } else {
                val detail = uiState.movieDetail
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // 1. Hero Backdrop Header with Gradient Overlay & Overlapping Poster
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                    ) {
                        val backdropPath = detail?.backdropPath ?: detail?.posterPath
                        if (!backdropPath.isNullOrEmpty()) {
                            coil3.compose.AsyncImage(
                                model = "https://image.tmdb.org/t/p/w780$backdropPath",
                                contentDescription = detail?.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .background(PlayMovSurfaceCard),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🎬 Backdrop Preview",
                                    color = PlayMovTextMuted,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        // Bottom Gradient Fade to Base Background
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            PlayMovBgBase.copy(alpha = 0.5f),
                                            PlayMovBgBase
                                        )
                                    )
                                )
                        )

                        // Floating Poster Image Card
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            if (!detail?.posterPath.isNullOrEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    border = androidx.compose.foundation.BorderStroke(2.dp, PlayMovPrimary),
                                    shadowElevation = 8.dp,
                                    color = PlayMovSurfaceCard
                                ) {
                                    coil3.compose.AsyncImage(
                                        model = "https://image.tmdb.org/t/p/w500${detail.posterPath}",
                                        contentDescription = detail.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .width(95.dp)
                                            .height(135.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 2. Movie Content Details
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Title & Rating Badge Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = detail?.title ?: "Movie Details",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 22.sp,
                                    color = PlayMovTextMain,
                                    lineHeight = 28.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(10.dp))

                            // Rating Chip Badge
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = PlayMovSurfaceCard,
                                border = androidx.compose.foundation.BorderStroke(1.dp, PlayMovStarGold.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Rating",
                                        tint = PlayMovStarGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${detail?.voteAverage ?: 0.0}",
                                        fontSize = 13.sp,
                                        color = PlayMovStarGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Meta Chips (Release Date & Runtime)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = PlayMovSuccess.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PlayMovSuccess.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "🗓️ Release: ${detail?.releaseDate.orEmpty()}",
                                    fontSize = 12.sp,
                                    color = PlayMovSuccess,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = PlayMovPrimary.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PlayMovPrimary.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "⏱️ ${detail?.runtime ?: 120} min",
                                    fontSize = 12.sp,
                                    color = PlayMovPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }

                        // Overview Section Card
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = PlayMovSurfaceCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, PlayMovBorder)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Overview",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = PlayMovTextMain
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = detail?.overview.orEmpty().ifBlank { "No description available for this movie." },
                                    fontSize = 13.sp,
                                    color = PlayMovTextMuted,
                                    lineHeight = 19.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Action Buttons
                        val trailer = uiState.videos.firstOrNull {
                            it.site.equals("YouTube", ignoreCase = true) && it.type.equals("Trailer", ignoreCase = true)
                        } ?: uiState.videos.firstOrNull {
                            it.site.equals("YouTube", ignoreCase = true)
                        } ?: uiState.videos.firstOrNull {
                            it.type.equals("Trailer", ignoreCase = true)
                        } ?: uiState.videos.firstOrNull()

                        val hasTrailer = trailer != null && trailer.key.isNotBlank()

                        Button(
                            onClick = {
                                if (hasTrailer) {
                                    onWatchTrailerClick(
                                        trailer.key,
                                        detail?.title ?: "Trailer"
                                    )
                                }
                            },
                            enabled = hasTrailer,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PlayMovPrimary,
                                disabledContainerColor = PlayMovSurfaceCard
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = if (hasTrailer) Color(0xFF0F172A) else PlayMovTextMuted,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (hasTrailer) "Watch Trailer" else "Trailer Unavailable",
                                color = if (hasTrailer) Color(0xFF0F172A) else PlayMovTextMuted,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                onReviewsClick(
                                    detail?.id ?: 0,
                                    detail?.title ?: "Movie"
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = androidx.compose.ui.graphics.SolidColor(PlayMovPrimary)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Comment,
                                contentDescription = null,
                                tint = PlayMovPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "View Community Reviews",
                                color = PlayMovPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
