package com.aizid.playmov.ui.search

import com.aizid.playmov.remote.dto.Movie

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val isPagingLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchResults: List<Movie> = emptyList(),
    val hasMorePages: Boolean = true
)
