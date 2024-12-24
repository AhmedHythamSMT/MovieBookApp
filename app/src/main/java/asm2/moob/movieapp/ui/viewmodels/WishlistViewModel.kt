package asm2.moob.movieapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import asm2.moob.movieapp.data.model.Movie

class WishlistViewModel : ViewModel() {
    private val _wishlistMovies = MutableStateFlow<List<Movie>>(emptyList())
    val wishlistMovies: StateFlow<List<Movie>> = _wishlistMovies

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    init {
        loadWishlist()
    }

    private fun loadWishlist() {
        auth.currentUser?.let { user ->
            firestore.collection("wishlists")
                .document(user.uid)
                .collection("movies")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("WishlistViewModel", "Error loading wishlist", error)
                        return@addSnapshotListener
                    }

                    val movies = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            val data = doc.data
                            if (data != null) {
                                Movie(
                                    id = (data["id"] as? Number)?.toInt() ?: 0,
                                    title = data["title"] as? String ?: "",
                                    overview = data["overview"] as? String ?: "",
                                    posterPath = data["posterPath"] as? String,
                                    backdropPath = data["backdropPath"] as? String,
                                    releaseDate = data["releaseDate"] as? String ?: "",
                                    voteAverage = (data["voteAverage"] as? Number)?.toDouble() ?: 0.0
                                )
                            } else null
                        } catch (e: Exception) {
                            Log.e("WishlistViewModel", "Error converting document", e)
                            null
                        }
                    } ?: emptyList()
                    
                    _wishlistMovies.value = movies
                    Log.d("WishlistViewModel", "Loaded ${movies.size} movies")
                }
        }
    }

    fun addToWishlist(movie: Movie) {
        auth.currentUser?.let { user ->
            firestore.collection("wishlists")
                .document(user.uid)
                .collection("movies")
                .document(movie.id.toString())
                .set(movie.toMap())
                .addOnSuccessListener {
                    Log.d("WishlistViewModel", "Movie added to wishlist: ${movie.title}")
                }
                .addOnFailureListener { e ->
                    Log.e("WishlistViewModel", "Error adding movie to wishlist", e)
                }
        }
    }

    fun removeFromWishlist(movieId: Int) {
        auth.currentUser?.let { user ->
            firestore.collection("wishlists")
                .document(user.uid)
                .collection("movies")
                .document(movieId.toString())
                .delete()
                .addOnSuccessListener {
                    Log.d("WishlistViewModel", "Movie removed from wishlist: $movieId")
                }
                .addOnFailureListener { e ->
                    Log.e("WishlistViewModel", "Error removing movie from wishlist", e)
                }
        }
    }

    private fun Movie.toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "title" to title,
            "overview" to overview,
            "posterPath" to posterPath,
            "backdropPath" to backdropPath,
            "releaseDate" to releaseDate,
            "voteAverage" to voteAverage
        )
    }
} 