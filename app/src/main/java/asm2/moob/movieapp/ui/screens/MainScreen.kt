package asm2.moob.movieapp.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import asm2.moob.movieapp.navigation.BottomNavItem
import asm2.moob.movieapp.navigation.Routes
import asm2.moob.movieapp.ui.viewmodels.AuthViewModel

@Composable
fun MainScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val mainNavController = rememberNavController()
    val navBackStackEntry by mainNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Hide bottom bar on certain screens
    val showBottomBar = when {
        currentRoute == Routes.Splash -> false
        currentRoute?.startsWith(Routes.MovieDetail) == true -> false
        else -> true
    }

    val items = listOf(
        BottomNavItem.MovieList,
        BottomNavItem.AllMovies,
        BottomNavItem.Wishlist
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val currentDestination = navBackStackEntry?.destination
                    items.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                mainNavController.navigate(item.route) {
                                    popUpTo(mainNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = mainNavController,
            startDestination = Routes.Splash,
            modifier = Modifier
        ) {
            composable(Routes.Splash) {
                SplashScreen(
                    navController = mainNavController,
                    authViewModel = authViewModel
                )
            }
            
            composable(BottomNavItem.MovieList.route) {
                Box(modifier = Modifier.padding(bottom = if (showBottomBar) padding.calculateBottomPadding() else 0.dp)) {
                    MovieListScreen(
                        navController = mainNavController,
                        authViewModel = authViewModel
                    )
                }
            }
            
            composable(BottomNavItem.AllMovies.route) {
                Box(modifier = Modifier.padding(bottom = if (showBottomBar) padding.calculateBottomPadding() else 0.dp)) {
                    AllMoviesScreen(navController = mainNavController)
                }
            }
            
            composable(BottomNavItem.Wishlist.route) {
                Box(modifier = Modifier.padding(bottom = if (showBottomBar) padding.calculateBottomPadding() else 0.dp)) {
                    WishlistScreen(navController = mainNavController)
                }
            }
            
            composable(
                route = Routes.MovieDetail + "/{movieId}",
                arguments = listOf(
                    navArgument("movieId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getInt("movieId") ?: return@composable
                MovieDetailScreen(
                    movieId = movieId,
                    navController = mainNavController
                )
            }
        }
    }
} 