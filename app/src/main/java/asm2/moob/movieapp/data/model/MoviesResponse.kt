package asm2.moob.movieapp.data.model

data class MoviesResponse(
    val results: List<Movie>,
    val page: Int,
    val total_pages: Int,
    val total_results: Int
) 