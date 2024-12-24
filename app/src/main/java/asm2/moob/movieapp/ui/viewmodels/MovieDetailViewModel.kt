package asm2.moob.movieapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import asm2.moob.movieapp.data.model.Movie
import asm2.moob.movieapp.util.NetworkUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import asm2.moob.movieapp.data.model.Video
import asm2.moob.movieapp.data.repository.MovieRepository
import asm2.moob.movieapp.data.model.Cast

class MovieDetailViewModel(private val repository: MovieRepository) : ViewModel() {
    private val _movie = MutableStateFlow<Movie?>(null)
    val movie: StateFlow<Movie?> = _movie

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _video = MutableStateFlow<Video?>(null)
    val video: StateFlow<Video?> = _video

    private val _isInWishlist = MutableStateFlow(false)
    val isInWishlist: StateFlow<Boolean> = _isInWishlist

    private val _relatedMovies = MutableStateFlow<List<Movie>>(emptyList())
    val relatedMovies: StateFlow<List<Movie>> = _relatedMovies

    private val _cast = MutableStateFlow<List<Cast>>(emptyList())
    val cast: StateFlow<List<Cast>> = _cast

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load movie details
                val movieDetails = repository.getMovieDetails(movieId)
                _movie.value = movieDetails

                // Load cast
                val credits = repository.getMovieCredits(movieId)
                _cast.value = credits.cast

                // Load video/trailer
                val videos = repository.getMovieVideos(movieId)
                _video.value = videos.results.firstOrNull { 
                    it.type.equals("Trailer", ignoreCase = true) || 
                    it.type.equals("Teaser", ignoreCase = true)
                }

                // Check wishlist status
                checkWishlistStatus(movieId)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun checkWishlistStatus(movieId: Int) {
        auth.currentUser?.let { user ->
            firestore.collection("wishlists")
                .document(user.uid)
                .collection("movies")
                .document(movieId.toString())
                .addSnapshotListener { snapshot, _ ->
                    _isInWishlist.value = snapshot?.exists() == true
                }
        }
    }

    fun retry(movieId: Int) {
        loadMovieDetails(movieId)
    }
} 