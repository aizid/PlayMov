package com.aizid.playmov.ui.search

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

class SearchViewModel(
    private val monoRepo: MonoRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val pagingManager = PagingManager<Movie>()

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
        if (newQuery.length >= 2) {
            pagingManager.clearData()
            performSearch(query = newQuery, page = 1)
        } else {
            pagingManager.clearData()
            _uiState.update { it.copy(searchResults = emptyList(), isLoading = false, isPagingLoading = false) }
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoading || state.isPagingLoading || !state.hasMorePages || state.query.length < 2) return

        viewModelScope.launch {
            _uiState.update { it.copy(isPagingLoading = true) }
            performSearch(query = state.query, page = pagingManager.currentPage + 1)
        }
    }

    private fun performSearch(query: String, page: Int) {
        viewModelScope.launch {
            if (page == 1) {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }
            monoRepo.searchMovies(query = query, page = page).collect { result ->
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
                                searchResults = pagingManager.getData(),
                                hasMorePages = pagingManager.hasMorePages()
                            )
                        }
                    }
                    is Resource.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isPagingLoading = false,
                                errorMessage = result.failureData.message ?: "Search failed"
                            )
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}
