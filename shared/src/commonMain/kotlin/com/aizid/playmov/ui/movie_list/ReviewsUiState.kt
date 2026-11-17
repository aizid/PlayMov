package com.aizid.playmov.ui.movie_list

import com.aizid.playmov.remote.dto.Review

data class ReviewsUiState(
    val isLoading: Boolean = false,
    val isPagingLoading: Boolean = false,
    val errorMessage: String? = null,
    val reviews: List<Review> = emptyList(),
    val hasMorePages: Boolean = true
)
