package com.aizid.playmov.ui.movies

import com.aizid.playmov.remote.dto.MovieDetail
import com.aizid.playmov.remote.dto.Video

data class MovieDetailUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isFavorite: Boolean = false,
    val movieDetail: MovieDetail? = null,
    val videos: List<Video> = emptyList()
)
