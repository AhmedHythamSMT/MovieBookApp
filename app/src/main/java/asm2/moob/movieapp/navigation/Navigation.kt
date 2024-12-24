package asm2.moob.movieapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import asm2.moob.movieapp.ui.screens.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import asm2.moob.movieapp.ui.viewmodels.AuthViewModel

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.Splash
    ) {
        composable(Routes.Register) {
            RegisterScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Routes.MovieList) {
            MovieListScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(
            route = "${Routes.MovieDetail}/{movieId}",
            arguments = listOf(
                navArgument("movieId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: return@composable
            MovieDetailScreen(
                movieId = movieId,
                navController = navController
            )
        }

        composable(Routes.Splash) {
            SplashScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Routes.Wishlist) {
            WishlistScreen(navController = navController)
        }

        composable(Routes.AllMovies) {
            AllMoviesScreen(navController = navController)
        }
    }
} 