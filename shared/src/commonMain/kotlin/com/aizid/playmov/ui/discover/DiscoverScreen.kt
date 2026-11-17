package com.aizid.playmov.ui.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aizid.playmov.remote.dto.Movie
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DiscoverScreen(
    genreId: Int?,
    genreName: String?,
    onMovieClick: (Movie) -> Unit,
    onBackClick: () -> Unit,
    viewModel: DiscoverViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(genreId) {
        viewModel.loadDiscover(genreId, genreName)
    }

    DiscoverContent(
        uiState = uiState,
        onMovieClick = onMovieClick,
        onLoadMore = viewModel::loadMore,
        onRefresh = { viewModel.loadDiscover(genreId, genreName) },
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverContent(
    uiState: DiscoverUiState,
    onMovieClick: (Movie) -> Unit,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit,
    onBackClick: () -> Unit
) {
    val darkBg = Color(0xFF0A0E17)
    val cardBg = Color(0xFF141C2E)
    val borderCol = Color(0xFF1E293B)
    val primaryText = Color(0xFFF8FAFC)
    val mutedText = Color(0xFF94A3B8)
    val accentCol = Color(0xFF38BDF8)
    val starGold = Color(0xFFF59E0B)

    val listState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisibleItem >= totalItems - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onLoadMore()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.genreName,
                        fontWeight = FontWeight.Bold,
                        color = primaryText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = primaryText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF141C2E)
                )
            )
        },
        containerColor = darkBg
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading && uiState.movies.isNotEmpty(),
            onRefresh = onRefresh,
            state = pullToRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading && uiState.movies.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = accentCol
                )
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(uiState.movies) { _, movie ->
                        MovieRowCard(
                            movie = movie,
                            cardBg = cardBg,
                            borderCol = borderCol,
                            primaryText = primaryText,
                            mutedText = mutedText,
                            accentCol = accentCol,
                            starGold = starGold,
                            onClick = { onMovieClick(movie) }
                        )
                    }

                    if (uiState.isPagingLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = accentCol,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieRowCard(
    movie: Movie,
    cardBg: Color,
    borderCol: Color,
    primaryText: Color,
    mutedText: Color,
    accentCol: Color,
    starGold: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, borderCol, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        color = cardBg,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!movie.posterPath.isNullOrEmpty()) {
                coil3.compose.AsyncImage(
                    model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                    contentDescription = movie.title,
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier
                        .width(100.dp)
                        .height(140.dp)
                        .background(Color(0xFF0F172A))
                )
            } else {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(140.dp)
                        .background(Color(0xFF0F172A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎬",
                        fontSize = 24.sp
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = movie.title.orEmpty(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = primaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = starGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${movie.voteAverage}",
                        fontSize = 13.sp,
                        color = starGold,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${movie.releaseDate.orEmpty().take(4)}",
                        fontSize = 13.sp,
                        color = mutedText,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = movie.overview.orEmpty(),
                    fontSize = 12.sp,
                    color = mutedText,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp
                )
            }
        }
    }
}
