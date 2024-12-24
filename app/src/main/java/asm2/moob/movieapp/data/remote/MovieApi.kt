package asm2.moob.movieapp.data.remote

import ArticlesResponse
import ReviewsResponse
import asm2.moob.movieapp.data.model.Movie
import asm2.moob.movieapp.data.model.MovieResponse
import asm2.moob.movieapp.data.model.Credits
import asm2.moob.movieapp.data.model.GenresResponse
import asm2.moob.movieapp.data.model.VideoResponse
import asm2.moob.movieapp.data.model.MoviesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int,
        @Query("language") language: String = "en-US"
    ): MovieResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("language") language: String = "en-US"
    ): MovieResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "en-US"
    ): Movie

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "en-US"
    ): Credits

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("page") page: Int,
        @Query("language") language: String = "en-US"
    ): MovieResponse

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("page") page: Int,
        @Query("language") language: String = "en-US"
    ): MovieResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("page") page: Int,
        @Query("language") language: String = "en-US"
    ): MovieResponse

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "en-US"
    ): VideoResponse

    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("language") language: String = "en-US"
    ): GenresResponse

    @GET("discover/movie")
    suspend fun getFilteredMovies(
        @Query("with_genres") genres: String? = null,
        @Query("primary_release_year") year: Int? = null,
        @Query("vote_average.gte") minRating: Float? = null,
        @Query("vote_average.lte") maxRating: Float? = null,
        @Query("sort_by") sortBy: String? = null,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US"
    ): MoviesResponse

    @GET("movie/{movie_id}/recommendations")
    suspend fun getRecommendations(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "en-US"
    ): MovieResponse

    @GET("movie/{movie_id}/reviews")
    suspend fun getMovieReviews(
        @Path("movie_id") movieId: Int,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US"
    ): ReviewsResponse

    @GET("movie/now_playing")
    suspend fun getMovieNews(
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US"
    ): MoviesResponse

    @GET("discover/movie")
    suspend fun getAllMovies(
        @Query("page") page: Int,
        @Query("language") language: String = "en-US",
        @Query("with_genres") genres: String? = null,
        @Query("year") year: Int? = null,
        @Query("vote_average.gte") minRating: Float? = null,
        @Query("vote_average.lte") maxRating: Float? = null,
        @Query("sort_by") sortBy: String = "popularity.desc"
    ): MovieResponse
} 