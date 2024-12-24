data class Video(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val type: String
)

data class VideoResponse(
    val id: Int,
    val results: List<Video>
) 