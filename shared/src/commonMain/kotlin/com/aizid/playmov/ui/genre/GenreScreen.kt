package com.aizid.playmov.ui.genre

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
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
import com.aizid.playmov.remote.dto.Genre
import com.aizid.playmov.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GenreScreen(
    onGenreClick: (Genre) -> Unit,
    onSearchClick: () -> Unit,
    onUserClick: () -> Unit,
    viewModel: GenreViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    GenreContent(
        uiState = uiState,
        onGenreClick = onGenreClick,
        onSearchClick = onSearchClick,
        onUserClick = onUserClick,
        onRetryClick = viewModel::loadGenres,
        onRefresh = viewModel::loadGenres
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreContent(
    uiState: GenreUiState,
    onGenreClick: (Genre) -> Unit,
    onSearchClick: () -> Unit,
    onUserClick: () -> Unit,
    onRetryClick: () -> Unit,
    onRefresh: () -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = null,
                            tint = PlayMovPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Genre Explorer",
                            fontWeight = FontWeight.ExtraBold,
                            color = PlayMovTextMain,
                            fontSize = 20.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = PlayMovPrimary
                        )
                    }
                    IconButton(onClick = onUserClick) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User",
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
        PullToRefreshBox(
            isRefreshing = uiState.isLoading && uiState.movieGenres.isNotEmpty(),
            onRefresh = onRefresh,
            state = pullToRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading && uiState.movieGenres.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PlayMovPrimary
                )
            } else if (uiState.errorMessage != null && uiState.movieGenres.isEmpty()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = uiState.errorMessage, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onRetryClick,
                        colors = ButtonDefaults.buttonColors(containerColor = PlayMovPrimary)
                    ) {
                        Text("Retry", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                val genres = uiState.movieGenres
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(genres) { genre ->
                        val icon = GenreIcons.getIconForGenre(genre.id, genre.name)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(112.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            PlayMovSurfaceCard,
                                            PlayMovSurfaceCardGradientEnd
                                        )
                                    )
                                )
                                .border(1.dp, PlayMovBorder, RoundedCornerShape(18.dp))
                                .clickable { onGenreClick(genre) }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(PlayMovPrimary.copy(alpha = 0.15f))
                                        .border(1.dp, PlayMovPrimary.copy(alpha = 0.35f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = genre.name,
                                        tint = PlayMovPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = genre.name.orEmpty(),
                                    color = PlayMovTextMain,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
