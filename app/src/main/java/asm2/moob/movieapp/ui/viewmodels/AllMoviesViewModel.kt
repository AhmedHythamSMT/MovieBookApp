package asm2.moob.movieapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import asm2.moob.movieapp.data.model.Genre
import asm2.moob.movieapp.data.model.Movie
import asm2.moob.movieapp.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class FilterParams(
    val genres: List<Int> = emptyList(),
    val year: Int? = null,
    val minRating: Float = 0f,
    val maxRating: Float = 10f
)

class AllMoviesViewModel(
    private val repository: MovieRepository
) : ViewModel() {
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var currentPage = 1
    private var isLastPage = false
    private var currentSearchQuery = ""
    private var currentCategory: String? = null

    init {
        loadMovies()
    }

    private fun loadMovies(isLoadingMore: Boolean = false) {
        if (isLastPage) return

        viewModelScope.launch {
            try {
                if (isLoadingMore) {
                    _isLoadingMore.value = true
                } else {
                    _isLoading.value = true
                    currentPage = 1
                    _movies.value = emptyList()
                }

                val response = when (currentCategory) {
                    "now_playing" -> repository.getNowPlayingMovies()
                    "upcoming" -> repository.getUpcomingMovies()
                    else -> if (currentSearchQuery.isBlank()) {
                        repository.getAllMovies(currentPage)
                    } else {
                        repository.searchMovies(currentSearchQuery, currentPage)
                    }
                }

                if (isLoadingMore) {
                    _movies.value = _movies.value + response.results
                } else {
                    _movies.value = response.results
                }

                isLastPage = response.page >= response.totalPages
                currentPage++
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
                _isLoadingMore.value = false
            }
        }
    }

    fun loadNextPage() {
        if (!_isLoading.value && !_isLoadingMore.value && !isLastPage) {
            loadMovies(isLoadingMore = true)
        }
    }

    fun searchMovies(query: String) {
        currentSearchQuery = query
        isLastPage = false
        loadMovies()
    }

    fun setCategory(category: String?) {
        currentCategory = category
        loadMovies()
    }
}