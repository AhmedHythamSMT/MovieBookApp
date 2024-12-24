package asm2.moob.movieapp.data.model

import java.util.UUID

data class MovieCollection(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val name: String = "",
    val description: String? = null,
    val coverMovieId: Int? = null,
    val movieIds: List<Int> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
) 