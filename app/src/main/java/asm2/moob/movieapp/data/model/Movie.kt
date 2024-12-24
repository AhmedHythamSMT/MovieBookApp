package asm2.moob.movieapp.data.model

import com.google.gson.annotations.SerializedName

data class Movie(
    val id: Int = 0,
    val title: String = "",
    val overview: String = "",
    @SerializedName("poster_path")
    val posterPath: String? = null,
    @SerializedName("backdrop_path")
    val backdropPath: String? = null,
    @SerializedName("release_date")
    val releaseDate: String = "",
    @SerializedName("vote_average")
    val voteAverage: Double = 0.0,
    @SerializedName("genre_ids")
    val genres: List<Int>? = null,
    @SerializedName("runtime")
    val runtime: Int? = null,
    @SerializedName("vote_count")
    val voteCount: Int = 0,
    @SerializedName("original_language")
    val originalLanguage: String = "",
    var cast: List<Cast> = emptyList()
)
