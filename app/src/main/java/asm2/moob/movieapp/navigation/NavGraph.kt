package asm2.moob.movieapp.navigation

import CollectionDetailScreen
import CollectionsScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import asm2.moob.movieapp.ui.screens.*
import androidx.lifecycle.viewmodel.compose.viewModel
import asm2.moob.movieapp.ui.viewmodels.AuthViewModel
import asm2.moob.movieapp.data.repository.MovieRepository
import asm2.moob.movieapp.data.remote.RetrofitClient
import asm2.moob.movieapp.ui.viewmodels.MovieViewModelFactory
import asm2.moob.movieapp.ui.viewmodels.CollectionsViewModelFactory
import asm2.moob.movieapp.ui.viewmodels.MovieDetailViewModel
import asm2.moob.movieapp.ui.viewmodels.WishlistViewModel
import asm2.moob.movieapp.ui.viewmodels.CollectionsViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Routes.Splash,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.Splash) {
            SplashScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Routes.Login) {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

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
            route = Routes.MovieDetail + "/{movieId}",
            arguments = listOf(
                navArgument("movieId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: return@composable
            val movieDetailViewModel: MovieDetailViewModel = viewModel(
                factory = MovieViewModelFactory(MovieRepository(RetrofitClient.movieApi))
            )
            val wishlistViewModel: WishlistViewModel = viewModel()
            val collectionsViewModel: CollectionsViewModel = viewModel(
                factory = CollectionsViewModelFactory(MovieRepository(RetrofitClient.movieApi))
            )

            MovieDetailScreen(
                movieId = movieId,
                navController = navController,
                viewModel = movieDetailViewModel,
                wishlistViewModel = wishlistViewModel,
                collectionsViewModel = collectionsViewModel
            )
        }
        
        composable(Routes.Wishlist) {
            WishlistScreen(navController = navController)
        }

        composable(
            route = "${Routes.AllMovies}?category={category}",
            arguments = listOf(
                navArgument("category") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            AllMoviesScreen(
                navController = navController
            )
        }

        composable(Routes.Collections) {
            CollectionsScreen(navController = navController)
        }

        composable(
            route = Routes.CollectionDetail + "/{collectionId}",
            arguments = listOf(
                navArgument("collectionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getString("collectionId") ?: return@composable
            CollectionDetailScreen(
                collectionId = collectionId,
                navController = navController
            )
        }
    }
} 