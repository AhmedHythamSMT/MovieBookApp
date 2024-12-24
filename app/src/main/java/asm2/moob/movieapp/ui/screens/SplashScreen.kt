package asm2.moob.movieapp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import asm2.moob.movieapp.navigation.Routes
import asm2.moob.movieapp.ui.viewmodels.AuthState
import asm2.moob.movieapp.ui.viewmodels.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    // Animation scale
    var scale by remember { mutableFloatStateOf(0f) }
    val scaleAnimation = animateFloatAsState(
        targetValue = scale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    // Progress animation
    var progress by remember { mutableFloatStateOf(0f) }
    val progressAnimation = animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(2000), // 2 seconds duration
        label = "progress"
    )

    LaunchedEffect(Unit) {
        scale = 1f // Start icon animation
        progress = 1f // Start progress animation
        authViewModel.checkAuthState()
        delay(2000) // Show splash for 2 seconds
        when (authState) {
            is AuthState.Success -> {
                navController.navigate(Routes.MovieList) {
                    popUpTo(Routes.Splash) { inclusive = true }
                }
            }
            else -> {
                navController.navigate(Routes.Login) {
                    popUpTo(Routes.Splash) { inclusive = true }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFCEADD)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(32.dp)
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Movie,
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .scale(scaleAnimation.value),
                    tint = Color(0xFF2D3250)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Movie Book",  // Changed app name
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 40.sp,
                        letterSpacing = 2.sp
                    ),
                    color = Color(0xFF2D3250)
                )

                Spacer(modifier = Modifier.height(32.dp))

                LinearProgressIndicator(
                    progress = progressAnimation.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Color(0xFF2D3250),
                    trackColor = Color(0xFF2D3250).copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${(progressAnimation.value * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF2D3250)
                )
            }

            Text(
                text = "Developed by Ahmed Hytham",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                ),
                color = Color(0xFF2D3250).copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
} 