package com.aizid.playmov.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aizid.playmov.remote.dto.Movie
import com.aizid.playmov.ui.discover.MovieRowCard
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchScreen(
    onMovieClick: (Movie) -> Unit,
    onBackClick: () -> Unit,
    viewModel: SearchViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SearchContent(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onLoadMore = viewModel::loadMore,
        onRefresh = { if (uiState.query.isNotBlank()) viewModel.onQueryChange(uiState.query) },
        onMovieClick = onMovieClick,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchContent(
    uiState: SearchUiState,
    onQueryChange: (String) -> Unit,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit,
    onMovieClick: (Movie) -> Unit,
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
                        text = "Search PlayMov",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = onQueryChange,
                placeholder = { Text("Search title, actor, or genre...", color = mutedText) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = accentCol
                    )
                },
                trailingIcon = {
                    if (uiState.query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = mutedText
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = cardBg,
                    unfocusedContainerColor = cardBg,
                    focusedBorderColor = accentCol,
                    unfocusedBorderColor = borderCol,
                    focusedTextColor = primaryText,
                    unfocusedTextColor = primaryText
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            PullToRefreshBox(
                isRefreshing = uiState.isLoading && uiState.searchResults.isNotEmpty(),
                onRefresh = onRefresh,
                state = pullToRefreshState,
                modifier = Modifier.fillMaxSize()
            ) {
                if (uiState.isLoading && uiState.searchResults.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = accentCol
                    )
                } else if (uiState.searchResults.isEmpty() && uiState.query.length >= 2) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🔍", fontSize = 44.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No Results Found",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = primaryText
                        )
                        Text(
                            text = "Try searching for a different movie title",
                            fontSize = 13.sp,
                            color = mutedText
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(uiState.searchResults) { _, movie ->
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
}
