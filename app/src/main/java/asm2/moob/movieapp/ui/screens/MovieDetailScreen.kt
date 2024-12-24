package asm2.moob.movieapp.ui.screens

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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import asm2.moob.movieapp.data.model.Cast
import asm2.moob.movieapp.data.model.Movie
import asm2.moob.movieapp.data.repository.MovieRepository
import asm2.moob.movieapp.data.remote.RetrofitClient
import asm2.moob.movieapp.ui.viewmodels.MovieDetailViewModel
import asm2.moob.movieapp.ui.viewmodels.MovieViewModelFactory
import asm2.moob.movieapp.ui.components.LoadingState
import asm2.moob.movieapp.ui.components.ErrorState
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import asm2.moob.movieapp.ui.viewmodels.WishlistViewModel
import asm2.moob.movieapp.data.model.Video
import androidx.lifecycle.Lifecycle
import asm2.moob.movieapp.data.model.MovieCollection
import asm2.moob.movieapp.navigation.Routes
import kotlinx.coroutines.flow.StateFlow
import asm2.moob.movieapp.ui.viewmodels.CollectionsViewModel
import asm2.moob.movieapp.ui.viewmodels.CollectionsViewModelFactory
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    movieId: Int,
    navController: NavController,
    viewModel: MovieDetailViewModel = viewModel(
        factory = MovieViewModelFactory(MovieRepository(RetrofitClient.movieApi))
    ),
    wishlistViewModel: WishlistViewModel = viewModel(),
    collectionsViewModel: CollectionsViewModel = viewModel(
        factory = CollectionsViewModelFactory(MovieRepository(RetrofitClient.movieApi))
    )
) {
    var showCollectionDialog by remember { mutableStateOf(false) }

    LaunchedEffect(movieId) {
        viewModel.loadMovieDetails(movieId)
        collectionsViewModel.loadCollections()
    }

    val movieState = viewModel.movie.collectAsStateWithLifecycle()
    val videoState = viewModel.video.collectAsStateWithLifecycle()
    val castState = viewModel.cast.collectAsStateWithLifecycle()
    val isInWishlist by viewModel.isInWishlist.collectAsStateWithLifecycle()
    val collections by collectionsViewModel.collections.collectAsStateWithLifecycle()
    val toastMessage by collectionsViewModel.toastMessage.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    // Add toast message handler
    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            collectionsViewModel.clearToastMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* Empty title */ },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = Color.White
                ),
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = error ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            movieState.value?.let { movie ->
                Box(modifier = Modifier.fillMaxSize()) {
                    // Backdrop image with gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                    ) {
                        AsyncImage(
                            model = "https://image.tmdb.org/t/p/w1280${movie.backdropPath}",
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            MaterialTheme.colorScheme.surface
                                        ),
                                        startY = 200f
                                    )
                                )
                        )
                    }

                    // Scrollable content
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(300.dp))
                            
                            // Movie poster and basic info
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                // Poster
                                Card(
                                    modifier = Modifier
                                        .width(120.dp)
                                        .aspectRatio(2f/3f),
                                    elevation = CardDefaults.cardElevation(8.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    AsyncImage(
                                        model = "https://image.tmdb.org/t/p/w342${movie.posterPath}",
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                // Title and info
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 16.dp)
                                ) {
                                    Text(
                                        text = movie.title,
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Rating
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = Color(0xFFFFD700),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = String.format("%.1f", movie.voteAverage),
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                        Text(
                                            text = "/10",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }

                                    // Release date
                                    Text(
                                        text = "Released: ${movie.releaseDate}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            // Action buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Wishlist button
                                FilledTonalButton(
                                    onClick = {
                                        if (isInWishlist) {
                                            wishlistViewModel.removeFromWishlist(movie.id)
                                        } else {
                                            wishlistViewModel.addToWishlist(movie)
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (isInWishlist) {
                                            Icons.Default.Favorite
                                        } else {
                                            Icons.Default.FavoriteBorder
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isInWishlist) "Saved" else "Save",
                                        maxLines = 1,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                // Trailer button
                                videoState.value?.let { video ->
                                    FilledTonalButton(
                                        onClick = {
                                            val intent = Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("https://www.youtube.com/watch?v=${video.key}")
                                            )
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            Icons.Default.PlayArrow,
                                            null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Trailer",
                                            maxLines = 1,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }

                                // Add to collection button
                                FilledTonalButton(
                                    onClick = { showCollectionDialog = true },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        Icons.Default.PlaylistAdd,
                                        null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Add",
                                        maxLines = 1,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            // Collection Dialog
                            if (showCollectionDialog) {
                                AlertDialog(
                                    onDismissRequest = { showCollectionDialog = false },
                                    title = { Text("Add to Collection") },
                                    text = {
                                        Column {
                                            if (collections.isEmpty()) {
                                                Text("No collections yet. Create one first!")
                                            } else {
                                                collections.forEach { collection ->
                                                    ListItem(
                                                        headlineContent = { Text(collection.name) },
                                                        supportingContent = { 
                                                            Text("${collection.movieIds.size} movies") 
                                                        },
                                                        modifier = Modifier.clickable {
                                                            collectionsViewModel.addMovieToCollection(collection.id, movie.id)
                                                            showCollectionDialog = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    },
                                    confirmButton = {
                                        TextButton(onClick = { showCollectionDialog = false }) {
                                            Text("Cancel")
                                        }
                                    }
                                )
                            }

                            // Overview
                            Text(
                                text = "Overview",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Text(
                                text = movie.overview,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(16.dp)
                            )

                            // Cast section
                            if (castState.value.isNotEmpty()) {
                                Text(
                                    text = "Cast",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                LazyRow(
                                    contentPadding = PaddingValues(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(castState.value) { cast ->
                                        CastCard(cast = cast)
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
        // Cast photo
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w185${cast.profilePath}",
            contentDescription = cast.name,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Cast name
        Text(
            text = cast.name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        
        // Character name
        Text(
            text = cast.character,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RelatedMovieCard(
    movie: Movie,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w342${movie.posterPath}",
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentScale = ContentScale.Crop
            )
            
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", movie.voteAverage),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
} 