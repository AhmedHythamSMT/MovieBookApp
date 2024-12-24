package asm2.moob.movieapp.data.model

data class Genre(
    val id: Int,
    val name: String
)

data class GenresResponse(
    val genres: List<Genre>
) 