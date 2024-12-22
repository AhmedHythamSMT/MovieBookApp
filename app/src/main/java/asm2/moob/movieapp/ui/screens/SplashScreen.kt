package asm2.moob.movieapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import asm2.moob.movieapp.navigation.Routes
import asm2.moob.movieapp.ui.viewmodels.AuthViewModel
import asm2.moob.movieapp.ui.viewmodels.AuthState

@Composable
fun SplashScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }

    LaunchedEffect(authState) {
        delay(1000) // Brief delay to show splash screen
        when (authState) {
            is AuthState.Success -> {
                navController.navigate(Routes.MovieList.route) {
                    popUpTo(Routes.Splash.route) { inclusive = true }
                }
            }
            else -> {
                navController.navigate(Routes.Register.route) {
                    popUpTo(Routes.Splash.route) { inclusive = true }
                }
            }
        }
    }
} 