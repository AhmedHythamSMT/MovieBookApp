package asm2.moob.movieapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    data object MovieList : BottomNavItem(
        title = "Home",
        icon = Icons.Default.Home,
        route = Routes.MovieList
    )

    object AllMovies : BottomNavItem(
        title = "Discover",
        icon = Icons.Default.List,
        route = Routes.AllMovies
    )

    object Collections : BottomNavItem(
        title = "Collections",
        icon = Icons.Default.PlaylistAdd,
        route = Routes.Collections
    )

    object Wishlist : BottomNavItem(
        title = "Wishlist",
        icon = Icons.Default.Favorite,
        route = Routes.Wishlist
    )

    companion object {
        val items = listOf(MovieList, AllMovies, Collections, Wishlist)
    }
} 