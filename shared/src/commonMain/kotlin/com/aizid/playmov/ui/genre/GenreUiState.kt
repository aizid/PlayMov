package com.aizid.playmov.ui.genre

import com.aizid.playmov.remote.dto.Genre

data class GenreUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedTab: Int = 0, // 0 for Movies, 1 for Series
    val movieGenres: List<Genre> = emptyList(),
    val seriesGenres: List<Genre> = emptyList()
)
