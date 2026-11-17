package com.aizid.playmov.ui.movie_list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MovieReviewsScreen(
    movieId: Int,
    movieTitle: String,
    onBackClick: () -> Unit,
    viewModel: ReviewsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(movieId) {
        viewModel.loadReviews(movieId)
    }

    MovieReviewsContent(
        uiState = uiState,
        movieTitle = movieTitle,
        onLoadMore = viewModel::loadMore,
        onRefresh = { viewModel.loadReviews(movieId) },
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieReviewsContent(
    uiState: ReviewsUiState,
    movieTitle: String,
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
            totalItems > 0 && lastVisibleItem >= totalItems - 2
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
                        text = "Reviews - $movieTitle",
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
            isRefreshing = uiState.isLoading && uiState.reviews.isNotEmpty(),
            onRefresh = onRefresh,
            state = pullToRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading && uiState.reviews.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = accentCol
                )
            } else if (uiState.reviews.isEmpty()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "💬",
                        fontSize = 44.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Community Reviews Yet",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = primaryText
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Be the first to share your thoughts on $movieTitle!",
                        fontSize = 13.sp,
                        color = mutedText,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(uiState.reviews) { _, review ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = cardBg,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderCol)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "@${review.author.orEmpty()}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = accentCol
                                    )
                                    Text(
                                        text = "⭐ ${review.authorDetails.rating ?: 8}/10",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = starGold
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = review.content.orEmpty(),
                                    fontSize = 13.sp,
                                    color = mutedText,
                                    lineHeight = 18.sp
                                )
                            }
                        }
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
