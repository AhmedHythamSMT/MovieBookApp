package asm2.moob.movieapp.ui.screens

import MovieRepository
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import asm2.moob.movieapp.data.model.Cast
import asm2.moob.movieapp.data.model.Movie
import asm2.moob.movieapp.data.remote.RetrofitClient
import asm2.moob.movieapp.ui.viewmodels.MovieDetailViewModel
import asm2.moob.movieapp.ui.viewmodels.MovieViewModelFactory
import asm2.moob.movieapp.ui.components.LoadingState
import asm2.moob.movieapp.ui.components.ErrorState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    movieId: Int,
    navController: NavController,
    viewModel: MovieDetailViewModel = viewModel(
        factory = MovieViewModelFactory(MovieRepository(RetrofitClient.movieApi))
    )
) {
    val movieState = viewModel.movie.collectAsStateWithLifecycle()
    val isLoading = viewModel.isLoading.collectAsStateWithLifecycle()
    val error = viewModel.error.collectAsStateWithLifecycle()

    LaunchedEffect(movieId) {
        viewModel.loadMovieDetails(movieId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading.value -> {
                LoadingState(message = "Loading movie details...")
            }
            error.value != null -> {
                ErrorState(
                    message = error.value!!,
                    onRetry = { viewModel.retry(movieId) }
                )
            }
            movieState.value != null -> {
                // Existing movie details UI
                val movie = movieState.value!!
                
                // Backdrop image as background
                movie.backdropPath?.let { backdrop ->
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w1280$backdrop",
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.surface
                                ),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                )

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { },
                            navigationIcon = {
                                IconButton(onClick = { navController.navigateUp() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent,
                                navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    },
                    containerColor = Color.Transparent
                ) { padding ->
                    movieState.value?.let { movie ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(padding)
                        ) {
                            Spacer(modifier = Modifier.height(180.dp))

                            // Movie poster and info
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .fillMaxWidth()
                            ) {
                                // Poster
                                Card(
                                    modifier = Modifier
                                        .width(120.dp)
                                        .height(180.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                                ) {
                                    AsyncImage(
                                        model = "https://image.tmdb.org/t/p/w342${movie.posterPath}",
                                        contentDescription = movie.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                // Title and basic info
                                Column(
                                    modifier = Modifier
                                        .padding(start = 16.dp)
                                        .weight(1f)
                                ) {
                                    Text(
                                        text = movie.title,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    RatingBadge(rating = movie.voteAverage)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Released: ${movie.releaseDate}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    movie.runtime?.let {
                                        Text(
                                            text = "${it}min",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }

                            // Content
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // Genres
                                if (movie.genres.isNotEmpty()) {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    ) {
                                        items(movie.genres) { genre ->
                                            GenreChip(genre.name)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Overview
                                Text(
                                    text = "Overview",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = movie.overview,
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                // Cast
                                if (movie.cast.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text(
                                        text = "Cast",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        contentPadding = PaddingValues(vertical = 8.dp)
                                    ) {
                                        items(movie.cast) { cast ->
                                            CastCard(cast)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RatingBadge(rating: Double) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = "Rating",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = String.format("%.1f", rating),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun GenreChip(genre: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text = genre,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun CastCard(cast: Cast) {
    Column(
        modifier = Modifier.width(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w185${cast.profilePath}",
            contentDescription = cast.name,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = cast.name,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1
        )
        Text(
            text = cast.character,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
} 