package com.aizid.playmov.ui.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aizid.playmov.domain.repository.MonoRepo
import com.aizid.playmov.utils.network.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    private val monoRepo: MonoRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieDetailUiState())
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Check SQLDelight local favorite status
            val favStatus = monoRepo.isFavoriteMovie(movieId)
            _uiState.update { it.copy(isFavorite = favStatus) }
            
            monoRepo.getMovieDetails(movieId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                movieDetail = result.data
                            )
                        }
                    }
                    is Resource.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.failureData.message ?: "Failed to fetch movie details"
                            )
                        }
                    }
                    else -> Unit
                }
            }

            monoRepo.getMovieVideos(movieId).collect { result ->
                if (result is Resource.Success) {
                    _uiState.update {
                        it.copy(videos = result.data?.results.orEmpty())
                    }
                }
            }
        }
    }

    fun onToggleFavorite() {
        val detail = _uiState.value.movieDetail ?: return
        viewModelScope.launch {
            monoRepo.toggleFavoriteMovie(detail)
            val updatedStatus = monoRepo.isFavoriteMovie(detail.id)
            _uiState.update { it.copy(isFavorite = updatedStatus) }
        }
    }
}
