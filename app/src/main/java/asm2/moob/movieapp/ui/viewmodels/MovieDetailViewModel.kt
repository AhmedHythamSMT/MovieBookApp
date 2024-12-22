package asm2.moob.movieapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import asm2.moob.movieapp.data.model.Movie
import MovieRepository
import asm2.moob.movieapp.util.NetworkUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieDetailViewModel(private val repository: MovieRepository) : ViewModel() {
    private val _movie = MutableStateFlow<Movie?>(null)
    val movie: StateFlow<Movie?> = _movie

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val movieDetails = repository.getMovieDetails(movieId)
                _movie.value = movieDetails
            } catch (e: Exception) {
                _error.value = NetworkUtil.getErrorMessage(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun retry(movieId: Int) {
        loadMovieDetails(movieId)
    }
} 