package asm2.moob.movieapp.ui.viewmodels

import MovieRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import asm2.moob.movieapp.data.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import asm2.moob.movieapp.util.NetworkUtil

class MovieViewModel(private val repository: MovieRepository) : ViewModel() {
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private val _hasMorePages = MutableStateFlow(true)
    val hasMorePages: StateFlow<Boolean> = _hasMorePages

    private var currentPage = 1
    private var searchJob: Job? = null
    private var currentQuery: String? = null

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _paginationError = MutableStateFlow<String?>(null)
    val paginationError: StateFlow<String?> = _paginationError

    private var paginationJob: Job? = null

    init {
        loadPopularMovies()
    }

    private fun loadPopularMovies(isLoadingMore: Boolean = false) {
        if (_isLoading.value || (!isLoadingMore && _isLoadingMore.value)) return
        if (isLoadingMore && paginationJob?.isActive == true) return

        val job = viewModelScope.launch {
            try {
                if (isLoadingMore) {
                    _isLoadingMore.value = true
                    _paginationError.value = null
                } else {
                    _isLoading.value = true
                    _error.value = null
                    currentPage = 1
                }

                val response = repository.getPopularMovies(currentPage)
                val currentMovies = if (isLoadingMore) _movies.value else emptyList()

                if (response.results.isEmpty() && currentMovies.isEmpty()) {
                    _error.value = "No movies available"
                } else {
                    _movies.value = currentMovies + response.results
                    _hasMorePages.value = currentPage < response.totalPages
                    if (_hasMorePages.value) currentPage++
                }
            } catch (e: Exception) {
                if (isLoadingMore) {
                    _paginationError.value = NetworkUtil.getErrorMessage(e)
                } else {
                    _error.value = NetworkUtil.getErrorMessage(e)
                }
            } finally {
                _isLoading.value = false
                _isLoadingMore.value = false
                if (isLoadingMore) {
                    paginationJob = null
                }
            }
        }

        if (isLoadingMore) {
            paginationJob = job
        }
    }

    fun searchMovies(query: String) {
        if (query.isBlank()) {
            currentQuery = null
            loadPopularMovies()
            return
        }

        currentQuery = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                delay(300) // Debounce search
                _isLoading.value = true
                _error.value = null
                currentPage = 1

                val response = repository.searchMovies(query, currentPage)
                if (response.results.isEmpty()) {
                    _error.value = "No results found for '$query'"
                } else {
                    _movies.value = response.results
                    _hasMorePages.value = currentPage < response.totalPages
                    if (_hasMorePages.value) currentPage++
                }
            } catch (e: Exception) {
                _error.value = NetworkUtil.getErrorMessage(e)
            } finally {
                _isLoading.value = false
                _isLoadingMore.value = false
            }
        }
    }

    fun retry() {
        _error.value = null
        _paginationError.value = null
        if (currentQuery != null) {
            searchMovies(currentQuery!!)
        } else {
            loadPopularMovies()
        }
    }

    fun loadMore() {
        if (!_hasMorePages.value || _isLoadingMore.value || paginationJob?.isActive == true) return
        
        if (currentQuery != null) {
            loadMoreSearch()
        } else {
            loadPopularMovies(isLoadingMore = true)
        }
    }

    private fun loadMoreSearch() {
        if (paginationJob?.isActive == true) return

        paginationJob = viewModelScope.launch {
            try {
                _isLoadingMore.value = true
                _paginationError.value = null
                val response = repository.searchMovies(currentQuery!!, currentPage)
                
                _movies.value = _movies.value + response.results
                _hasMorePages.value = currentPage < response.totalPages
                if (_hasMorePages.value) currentPage++
            } catch (e: Exception) {
                _paginationError.value = NetworkUtil.getErrorMessage(e)
            } finally {
                _isLoadingMore.value = false
                paginationJob = null
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
        paginationJob?.cancel()
    }
} 