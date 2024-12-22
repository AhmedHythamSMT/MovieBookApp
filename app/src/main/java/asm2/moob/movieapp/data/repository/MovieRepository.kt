import asm2.moob.movieapp.data.model.Movie
import asm2.moob.movieapp.data.model.MovieResponse
import asm2.moob.movieapp.data.remote.MovieApi

class MovieRepository(private val api: MovieApi) {
    suspend fun getPopularMovies(page: Int): MovieResponse {
        return api.getPopularMovies(page, API_KEY)
    }

    suspend fun searchMovies(query: String, page: Int): MovieResponse {
        return api.searchMovies(query, page, API_KEY)
    }

    suspend fun getMovieDetails(movieId: Int): Movie {
        val movie = api.getMovieDetails(movieId, API_KEY)
        val credits = api.getMovieCredits(movieId, API_KEY)
        return movie.copy(cast = credits.cast)
    }

    companion object {
        private const val API_KEY = "4dff68b0e574fd9cbb2541a1770c8865"
    }
} 