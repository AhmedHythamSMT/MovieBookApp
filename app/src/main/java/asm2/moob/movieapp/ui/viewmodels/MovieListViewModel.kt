package asm2.moob.movieapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import asm2.moob.movieapp.data.model.Movie
import asm2.moob.movieapp.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieListViewModel(
    private val repository: MovieRepository
) : ViewModel() {
    private val _popularMovies = MutableStateFlow<List<Movie>>(emptyList())
    val popularMovies: StateFlow<List<Movie>> = _popularMovies

    private val _nowPlayingMovies = MutableStateFlow<List<Movie>>(emptyList())
    val nowPlayingMovies: StateFlow<List<Movie>> = _nowPlayingMovies

    private val _upcomingMovies = MutableStateFlow<List<Movie>>(emptyList())
    val upcomingMovies: StateFlow<List<Movie>> = _upcomingMovies

    private val _topRatedMovies = MutableStateFlow<List<Movie>>(emptyList())
    val topRatedMovies: StateFlow<List<Movie>> = _topRatedMovies

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("MovieListViewModel", "Loading movies...")
                val popularResponse = repository.getPopularMovies()
                Log.d("MovieListViewModel", "Popular movies loaded: ${popularResponse.results.size}")
                _popularMovies.value = popularResponse.results

                val nowPlayingResponse = repository.getNowPlayingMovies()
                Log.d("MovieListViewModel", "Now playing movies loaded: ${nowPlayingResponse.results.size}")
                _nowPlayingMovies.value = nowPlayingResponse.results

                val upcomingResponse = repository.getUpcomingMovies()
                Log.d("MovieListViewModel", "Upcoming movies loaded: ${upcomingResponse.results.size}")
                _upcomingMovies.value = upcomingResponse.results

                val topRatedResponse = repository.getTopRatedMovies()
                Log.d("MovieListViewModel", "Top rated movies loaded: ${topRatedResponse.results.size}")
                _topRatedMovies.value = topRatedResponse.results
            } catch (e: Exception) {
                Log.e("MovieListViewModel", "Error loading movies", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
} 