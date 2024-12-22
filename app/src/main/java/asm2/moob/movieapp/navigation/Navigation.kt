import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import asm2.moob.movieapp.ui.screens.LoginScreen
import asm2.moob.movieapp.ui.screens.MovieDetailScreen
import asm2.moob.movieapp.ui.screens.MovieListScreen
import asm2.moob.movieapp.ui.screens.RegisterScreen
import asm2.moob.movieapp.ui.screens.SplashScreen
import asm2.moob.movieapp.ui.viewmodels.AuthState
import asm2.moob.movieapp.ui.viewmodels.AuthViewModel
import asm2.moob.movieapp.navigation.Routes
import androidx.compose.runtime.LaunchedEffect

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route
    ) {
        composable(Routes.Splash.route) {
            SplashScreen(navController, authViewModel)
        }
        composable(Routes.Register.route) {
            RegisterScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable(Routes.Login.route) {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable(Routes.MovieList.route) {
            MovieListScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable(
            Routes.MovieDetail.route,
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            MovieDetailScreen(
                movieId = backStackEntry.arguments?.getInt("movieId") ?: 0,
                navController = navController
            )
        }
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Initial && navController.currentDestination?.route != Routes.Splash.route) {
            navController.navigate(Routes.Register.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
} 