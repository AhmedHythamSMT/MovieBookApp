package asm2.moob.movieapp.navigation

sealed class Routes {
    object Splash : Routes() {
        const val route = "splash"
    }
    object Register : Routes() {
        const val route = "register"
    }
    object Login : Routes() {
        const val route = "login"
    }
    object MovieList : Routes() {
        const val route = "movieList"
    }
    object MovieDetail : Routes() {
        const val route = "movieDetail/{movieId}"
        fun createRoute(movieId: Int) = "movieDetail/$movieId"
    }
} 