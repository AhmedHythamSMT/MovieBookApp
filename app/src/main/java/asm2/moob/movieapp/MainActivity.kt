package asm2.moob.movieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import asm2.moob.movieapp.navigation.BottomNavItem
import asm2.moob.movieapp.navigation.NavGraph
import asm2.moob.movieapp.navigation.Routes
import asm2.moob.movieapp.ui.theme.MovieAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieAppTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Hide bottom bar on these screens
    val shouldShowBottomBar = when (currentRoute) {
        Routes.Splash -> false
        Routes.Login -> false
        Routes.Register -> false
        Routes.MovieDetail -> false
        null -> false
        else -> true
    }
    
    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar {
                    BottomNavItem.items.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavGraph(
                navController = navController,
                startDestination = Routes.Splash,
                modifier = Modifier.padding(
                    bottom = if (shouldShowBottomBar) innerPadding.calculateBottomPadding() else 0.dp
                )
            )
        }
    }
}