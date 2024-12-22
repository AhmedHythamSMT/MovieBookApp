package asm2.moob.movieapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import asm2.moob.movieapp.ui.screens.MovieListScreen
import asm2.moob.movieapp.ui.screens.MovieDetailScreen
import androidx.navigation.NavHostController
import asm2.moob.movieapp.ui.viewmodels.AuthViewModel

sealed class Screen(val route: String) {
    object MovieList : Screen("movieList")
    object MovieDetail : Screen("movieDetail/{movieId}") {
        fun createRoute(movieId: Int) = "movieDetail/$movieId"
    }
}

@Composable
fun MovieNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    NavHost(navController = navController, startDestination = Screen.MovieList.route) {
        composable(Screen.MovieList.route) {
            MovieListScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable(
            Screen.MovieDetail.route,
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            MovieDetailScreen(
                movieId = backStackEntry.arguments?.getInt("movieId") ?: 0,
                navController = navController
            )
        }
    }
} 