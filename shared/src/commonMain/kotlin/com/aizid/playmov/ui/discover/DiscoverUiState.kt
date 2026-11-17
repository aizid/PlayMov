package com.aizid.playmov.ui.discover

import com.aizid.playmov.remote.dto.Movie

data class DiscoverUiState(
    val isLoading: Boolean = false,
    val isPagingLoading: Boolean = false,
    val errorMessage: String? = null,
    val genreName: String = "Discover Movies",
    val movies: List<Movie> = emptyList(),
    val hasMorePages: Boolean = true
)
