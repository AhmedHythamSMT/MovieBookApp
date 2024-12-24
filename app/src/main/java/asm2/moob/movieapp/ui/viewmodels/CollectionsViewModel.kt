package asm2.moob.movieapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import asm2.moob.movieapp.data.model.Movie
import asm2.moob.movieapp.data.model.MovieCollection
import asm2.moob.movieapp.data.model.Mood
import asm2.moob.movieapp.data.repository.MovieRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CollectionsViewModel(
    private val repository: MovieRepository
) : ViewModel() {
    private val _collections = MutableStateFlow<List<MovieCollection>>(emptyList())
    val collections: StateFlow<List<MovieCollection>> = _collections

    private val _moodMovies = MutableStateFlow<List<Movie>>(emptyList())
    val moodMovies: StateFlow<List<Movie>> = _moodMovies

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _currentCollection = MutableStateFlow<MovieCollection?>(null)
    val currentCollection: StateFlow<MovieCollection?> = _currentCollection

    private val _collectionMovies = MutableStateFlow<List<Movie>>(emptyList())
    val collectionMovies: StateFlow<List<Movie>> = _collectionMovies

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    init {
        loadCollections()
    }

    fun loadCollections() {
        viewModelScope.launch {
            try {
                _collections.value = repository.getUserCollections()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun createCollection(name: String, description: String?) {
        viewModelScope.launch {
            try {
                val collection = MovieCollection(
                    name = name,
                    description = description,
                    userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                )
                repository.createCollection(collection)
                loadCollections()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun getMoodBasedMovies(mood: Mood) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = repository.getMoodBasedMovies(mood)
                _moodMovies.value = response.results
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addMovieToCollection(collectionId: String, movieId: Int) {
        viewModelScope.launch {
            try {
                // Check if movie is already in collection
                val collection = repository.getUserCollections()
                    .find { it.id == collectionId }

                if (collection?.movieIds?.contains(movieId) == true) {
                    _toastMessage.value = "Movie is already in this collection"
                    return@launch
                }

                repository.addMovieToCollection(collectionId, movieId)
                _toastMessage.value = "Movie added to collection"
                loadCollections()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun loadCollectionDetails(collectionId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null  // Clear any previous errors
                
                // Get the collection details
                val collections = repository.getUserCollections()
                _currentCollection.value = collections.find { it.id == collectionId }
                
                if (_currentCollection.value == null) {
                    _error.value = "Collection not found"
                    return@launch
                }
                
                // Get all movies in the collection
                val movies = mutableListOf<Movie>()
                _currentCollection.value?.movieIds?.forEach { movieId ->
                    try {
                        val movie = repository.getMovieDetails(movieId)
                        movies.add(movie)
                    } catch (e: Exception) {
                        Log.e("CollectionsViewModel", "Error loading movie $movieId", e)
                    }
                }
                _collectionMovies.value = movies
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
                Log.e("CollectionsViewModel", "Error loading collection", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeMovieFromCollection(collectionId: String, movieId: Int) {
        viewModelScope.launch {
            try {
                repository.removeMovieFromCollection(collectionId, movieId)
                // Reload collection details to reflect changes
                loadCollectionDetails(collectionId)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun deleteCollection(collectionId: String) {
        viewModelScope.launch {
            try {
                repository.deleteCollection(collectionId)
                // Reload collections after successful deletion
                loadCollections()
                _toastMessage.value = "Collection deleted successfully"
            } catch (e: Exception) {
                _error.value = e.message
                _toastMessage.value = "Failed to delete collection"
            }
        }
    }
} 