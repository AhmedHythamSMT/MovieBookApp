package asm2.moob.movieapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import asm2.moob.movieapp.data.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import asm2.moob.movieapp.util.NetworkUtil
import kotlinx.coroutines.coroutineScope
import android.util.Log
import asm2.moob.movieapp.data.repository.MovieRepository

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

    private val _nowPlayingMovies = MutableStateFlow<List<Movie>>(emptyList())
    val nowPlayingMovies: StateFlow<List<Movie>> = _nowPlayingMovies

    private val _upcomingMovies = MutableStateFlow<List<Movie>>(emptyList())
    val upcomingMovies: StateFlow<List<Movie>> = _upcomingMovies

    private val _topRatedMovies = MutableStateFlow<List<Movie>>(emptyList())
    val topRatedMovies: StateFlow<List<Movie>> = _topRatedMovies

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                Log.d("MovieViewModel", "Loading initial data...")
                
                // Load popular movies first
                try {
                    val popularResponse = repository.getPopularMovies(1)
                    Log.d("MovieViewModel", "Popular movies loaded: ${popularResponse.results.size}")
                    _movies.value = popularResponse.results
                } catch (e: Exception) {
                    Log.e("MovieViewModel", "Error loading popular movies", e)
                    throw e
                }

                // Then load other categories
                coroutineScope {
                    launch {
                        try {
                            val nowPlayingResponse = repository.getNowPlayingMovies()
                            Log.d("MovieViewModel", "Now playing loaded: ${nowPlayingResponse.results.size}")
                            _nowPlayingMovies.value = nowPlayingResponse.results
                        } catch (e: Exception) {
                            Log.e("MovieViewModel", "Error loading now playing", e)
                        }
                    }

                    launch {
                        try {
                            val upcomingResponse = repository.getUpcomingMovies()
                            _upcomingMovies.value = upcomingResponse.results
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    launch {
                        try {
                            val topRatedResponse = repository.getTopRatedMovies()
                            _topRatedMovies.value = topRatedResponse.results
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error in loadInitialData", e)
                _error.value = NetworkUtil.getErrorMessage(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadAllCategories() {
        viewModelScope.launch {
            try {
                coroutineScope {
                    launch { loadNowPlayingMovies() }
                    launch { loadUpcomingMovies() }
                    launch { loadTopRatedMovies() }
                }
            } catch (e: Exception) {
                _error.value = NetworkUtil.getErrorMessage(e)
            }
        }
    }

    private suspend fun loadNowPlayingMovies() {
        try {
            val response = repository.getNowPlayingMovies()
            _nowPlayingMovies.value = response.results
        } catch (e: Exception) {
            // Handle error
        }
    }

    private suspend fun loadUpcomingMovies() {
        try {
            val response = repository.getUpcomingMovies()
            _upcomingMovies.value = response.results
        } catch (e: Exception) {
            // Handle error
        }
    }

    private suspend fun loadTopRatedMovies() {
        try {
            val response = repository.getTopRatedMovies()
            _topRatedMovies.value = response.results
        } catch (e: Exception) {
            // Handle error
        }
    }

    private fun loadPopularMovies(isLoadingMore: Boolean = false) {
        viewModelScope.launch {
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

                _movies.value = currentMovies + response.results
                _hasMorePages.value = currentPage < response.totalPages
                if (_hasMorePages.value) currentPage++
            } catch (e: Exception) {
                if (isLoadingMore) {
                    _paginationError.value = NetworkUtil.getErrorMessage(e)
                } else {
                    _error.value = NetworkUtil.getErrorMessage(e)
                }
            } finally {
                _isLoading.value = false
                _isLoadingMore.value = false
            }
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
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                loadPopularMovies()
                loadAllCategories()
            } catch (e: Exception) {
                _error.value = NetworkUtil.getErrorMessage(e)
            } finally {
                _isLoading.value = false
            }
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