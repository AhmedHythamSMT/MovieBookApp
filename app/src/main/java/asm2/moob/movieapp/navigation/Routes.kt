package asm2.moob.movieapp.navigation

object Routes {
    const val Splash = "splash"
    const val Login = "login"
    const val Register = "register"
    const val MovieList = "movieList"
    const val MovieDetail = "movie_detail"
    const val Wishlist = "wishlist"
    const val AllMovies = "allMovies"
    const val Collections = "collections"
    const val CollectionDetail = "collection"

    fun movieDetail(movieId: Int) = "movie_detail/$movieId"
    fun collectionDetail(id: String) = "$CollectionDetail/$id"
} 