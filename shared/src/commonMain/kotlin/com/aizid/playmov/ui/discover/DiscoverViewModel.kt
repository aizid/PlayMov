package com.aizid.playmov.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aizid.playmov.domain.repository.MonoRepo
import com.aizid.playmov.remote.dto.Movie
import com.aizid.playmov.utils.network.Resource
import com.aizid.playmov.utils.other.PagingManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DiscoverViewModel(
    private val monoRepo: MonoRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    private val pagingManager = PagingManager<Movie>()
    private var currentGenreId: Int? = null

    fun loadDiscover(genreId: Int?, genreName: String?) {
        currentGenreId = genreId
        pagingManager.clearData()

        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoading = true, 
                    errorMessage = null,
                    genreName = genreName ?: "Discover Movies"
                ) 
            }
            fetchMoviesPage(page = 1)
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoading || state.isPagingLoading || !state.hasMorePages) return

        viewModelScope.launch {
            _uiState.update { it.copy(isPagingLoading = true) }
            fetchMoviesPage(page = pagingManager.currentPage + 1)
        }
    }

    private suspend fun fetchMoviesPage(page: Int) {
        monoRepo.getMovieDiscover(genreId = currentGenreId, page = page).collect { result ->
            when (result) {
                is Resource.Success -> {
                    val response = result.data
                    val newMovies = response?.results.orEmpty()
                    val totalPages = response?.totalPages ?: 1

                    pagingManager.addData(newMovies, page, totalPages)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isPagingLoading = false,
                            movies = pagingManager.getData(),
                            hasMorePages = pagingManager.hasMorePages()
                        )
                    }
                }
                is Resource.Failure -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isPagingLoading = false,
                            errorMessage = result.failureData.message ?: "Failed to fetch movies"
                        )
                    }
                }
                else -> Unit
            }
        }
    }
}