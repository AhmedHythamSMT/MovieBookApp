data class Review(
    val id: String,
    val author: String,
    val content: String,
    val createdAt: String,
    val rating: Float? = null
)

data class ReviewsResponse(
    val results: List<Review>,
    val page: Int,
    val totalPages: Int
) 