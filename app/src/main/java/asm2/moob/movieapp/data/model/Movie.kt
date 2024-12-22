package asm2.moob.movieapp.data.model

import com.google.gson.annotations.SerializedName

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    val genres: List<Genre> = emptyList(),
    @SerializedName("runtime")
    val runtime: Int? = null,
    @SerializedName("vote_count")
    val voteCount: Int = 0,
    @SerializedName("original_language")
    val originalLanguage: String = "",
    var cast: List<Cast> = emptyList()
)
