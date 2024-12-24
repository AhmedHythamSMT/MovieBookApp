data class Article(
    val id: Int,
    val title: String,
    val content: String,
    val imageUrl: String,
    val publishDate: String
)

data class ArticlesResponse(
    val results: List<Article>,
    val page: Int,
    val totalPages: Int
) 