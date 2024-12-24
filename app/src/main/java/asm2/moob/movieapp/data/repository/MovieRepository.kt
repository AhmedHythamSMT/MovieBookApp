package asm2.moob.movieapp.data.repository

import ReviewsResponse
import asm2.moob.movieapp.data.model.Credits
import asm2.moob.movieapp.data.model.GenresResponse
import asm2.moob.movieapp.data.model.Mood
import asm2.moob.movieapp.data.model.Movie
import asm2.moob.movieapp.data.model.MovieCollection
import asm2.moob.movieapp.data.model.MovieResponse
import asm2.moob.movieapp.data.model.MoviesResponse
import asm2.moob.movieapp.data.model.VideoResponse
import asm2.moob.movieapp.data.remote.MovieApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class MovieRepository(private val movieApi: MovieApi) {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getPopularMovies(page: Int = 1): MovieResponse {
        return movieApi.getPopularMovies(page)
    }

    suspend fun searchMovies(query: String, page: Int): MovieResponse {
        return movieApi.searchMovies(query, page)
    }

    suspend fun getMovieDetails(movieId: Int): Movie {
        return movieApi.getMovieDetails(movieId)
    }

    suspend fun getMovieCredits(movieId: Int): Credits {
        return movieApi.getMovieCredits(movieId)
    }

    suspend fun getNowPlayingMovies(): MovieResponse {
        return movieApi.getNowPlayingMovies(1)
    }

    suspend fun getUpcomingMovies(): MovieResponse {
        return movieApi.getUpcomingMovies(1)
    }

    suspend fun getTopRatedMovies(): MovieResponse {
        return movieApi.getTopRatedMovies(1)
    }

    suspend fun getMovieVideos(movieId: Int): VideoResponse {
        return movieApi.getMovieVideos(movieId)
    }

    suspend fun getGenres(): GenresResponse {
        return movieApi.getGenres()
    }

    suspend fun getFilteredMovies(
        genres: String? = null,
        year: Int? = null,
        minRating: Float? = null,
        maxRating: Float? = null,
        sortBy: String? = null,
        page: Int = 1
    ): MoviesResponse {
        return movieApi.getFilteredMovies(
            genres = genres,
            year = year,
            minRating = minRating,
            maxRating = maxRating,
            sortBy = sortBy,
            page = page
        )
    }

    suspend fun getRecommendations(movieId: Int): MovieResponse {
        return movieApi.getRecommendations(movieId)
    }

    suspend fun getMovieReviews(movieId: Int, page: Int = 1): ReviewsResponse {
        return movieApi.getMovieReviews(movieId, page)
    }

    suspend fun getAllMovies(page: Int = 1): MovieResponse {
        return movieApi.getAllMovies(
            page = page
        )
    }

    suspend fun getMoodBasedMovies(mood: Mood): MoviesResponse = withContext(Dispatchers.IO) {
        try {
            movieApi.getFilteredMovies(
                genres = mood.genreIds.joinToString(","),
                sortBy = "vote_average.desc",
                page = 1
            )
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun createCollection(collection: MovieCollection) {
        auth.currentUser?.let { user ->
            firestore.collection("collections")
                .document(user.uid)
                .collection("userCollections")
                .document(collection.id)
                .set(collection)
        }
    }

    suspend fun getUserCollections(): List<MovieCollection> = withContext(Dispatchers.IO) {
        try {
            val collections = mutableListOf<MovieCollection>()
            auth.currentUser?.let { user ->
                val snapshot = firestore.collection("collections")
                    .document(user.uid)
                    .collection("userCollections")
                    .get()
                    .await()
                
                collections.addAll(snapshot.toObjects(MovieCollection::class.java))
            }
            collections
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addMovieToCollection(collectionId: String, movieId: Int) {
        auth.currentUser?.let { user ->
            val collectionRef = firestore.collection("collections")
                .document(user.uid)
                .collection("userCollections")
                .document(collectionId)

            val collection = collectionRef.get().await().toObject(MovieCollection::class.java)
            val updatedMovieIds = (collection?.movieIds ?: emptyList()) + movieId

            collectionRef.update("movieIds", updatedMovieIds)
        }
    }

    suspend fun removeMovieFromCollection(collectionId: String, movieId: Int) {
        auth.currentUser?.let { user ->
            firestore.collection("collections")
                .document(user.uid)
                .collection("userCollections")
                .document(collectionId)
                .update("movieIds", FieldValue.arrayRemove(movieId))
        }
    }

    suspend fun deleteCollection(collectionId: String) {
        auth.currentUser?.let { user ->
            firestore.collection("collections")
                .document(user.uid)
                .collection("userCollections")
                .document(collectionId)
                .delete()
                .await()
        }
    }
} 