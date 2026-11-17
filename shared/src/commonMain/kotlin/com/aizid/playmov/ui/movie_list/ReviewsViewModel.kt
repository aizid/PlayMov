package com.aizid.playmov.ui.movie_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aizid.playmov.domain.repository.MonoRepo
import com.aizid.playmov.remote.dto.Review
import com.aizid.playmov.utils.network.Resource
import com.aizid.playmov.utils.other.PagingManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReviewsViewModel(
    private val monoRepo: MonoRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewsUiState())
    val uiState: StateFlow<ReviewsUiState> = _uiState.asStateFlow()

    private val pagingManager = PagingManager<Review>()
    private var currentMovieId: Int? = null

    fun loadReviews(movieId: Int) {
        currentMovieId = movieId
        pagingManager.clearData()

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            fetchReviewsPage(page = 1)
        }
    }

    fun loadMore() {
        val state = _uiState.value
        val movieId = currentMovieId ?: return
        if (state.isLoading || state.isPagingLoading || !state.hasMorePages) return

        viewModelScope.launch {
            _uiState.update { it.copy(isPagingLoading = true) }
            fetchReviewsPage(page = pagingManager.currentPage + 1)
        }
    }

    private suspend fun fetchReviewsPage(page: Int) {
        val movieId = currentMovieId ?: return
        monoRepo.getMovieReviews(movieId = movieId, page = page).collect { result ->
            when (result) {
                is Resource.Success -> {
                    val response = result.data
                    val newReviews = response?.results.orEmpty()
                    val totalPages = response?.totalPages ?: 1

                    pagingManager.addData(newReviews, page, totalPages)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isPagingLoading = false,
                            reviews = pagingManager.getData(),
                            hasMorePages = pagingManager.hasMorePages()
                        )
                    }
                }
                is Resource.Failure -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isPagingLoading = false,
                            errorMessage = result.failureData.message ?: "Failed to load reviews"
                        )
                    }
                }
                else -> Unit
            }
        }
    }
}
