package asm2.moob.movieapp.ui.screens

import MovieRepository
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import asm2.moob.movieapp.data.model.Movie
import asm2.moob.movieapp.data.remote.RetrofitClient
import asm2.moob.movieapp.navigation.Screen
import asm2.moob.movieapp.ui.viewmodels.MovieViewModel
import asm2.moob.movieapp.ui.viewmodels.MovieViewModelFactory
import coil.compose.AsyncImage
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import asm2.moob.movieapp.navigation.Routes
import asm2.moob.movieapp.ui.components.LoadingState
import asm2.moob.movieapp.ui.components.ErrorState
import asm2.moob.movieapp.ui.components.EmptyState
import asm2.moob.movieapp.ui.viewmodels.AuthState
import asm2.moob.movieapp.ui.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    navController: NavController,
    viewModel: MovieViewModel = viewModel(
        factory = MovieViewModelFactory(MovieRepository(RetrofitClient.movieApi))
    ),
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    // Handle navigation when logged out
    LaunchedEffect(authState) {
        if (authState is AuthState.Initial) {
            navController.navigate(Routes.Register.route) {
                popUpTo(Routes.MovieList.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movie Book") },
                actions = {
                    if (authState is AuthState.Success) {
                        Text(
                            text = (authState as AuthState.Success).user.email ?: "",
                            modifier = Modifier.padding(end = 8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        IconButton(
                            onClick = { authViewModel.logout() }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Logout"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            var searchQuery by remember { mutableStateOf("") }
            val movies = viewModel.movies.collectAsStateWithLifecycle()
            val isLoading = viewModel.isLoading.collectAsStateWithLifecycle()
            val isLoadingMore = viewModel.isLoadingMore.collectAsStateWithLifecycle()
            val hasMorePages = viewModel.hasMorePages.collectAsStateWithLifecycle()
            val error = viewModel.error.collectAsStateWithLifecycle()
            val paginationError = viewModel.paginationError.collectAsStateWithLifecycle()

            Column {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { 
                        searchQuery = it
                        viewModel.searchMovies(it)
                    },
                    onSearch = { viewModel.searchMovies(searchQuery) },
                    modifier = Modifier.fillMaxWidth()
                )

                when {
                    isLoading.value -> {
                        LoadingState(
                            message = if (searchQuery.isBlank()) 
                                "Loading popular movies..." 
                            else 
                                "Searching for '$searchQuery'..."
                        )
                    }
                    error.value != null -> {
                        ErrorState(
                            message = getErrorMessage(error.value!!, searchQuery),
                            onRetry = { viewModel.retry() }
                        )
                    }
                    movies.value.isEmpty() -> {
                        EmptyState(
                            message = if (searchQuery.isBlank()) 
                                "No movies available" 
                            else 
                                "No results found for '$searchQuery'",
                            onRefresh = { viewModel.retry() }
                        )
                    }
                    else -> {
                        LazyColumn {
                            items(
                                items = movies.value,
                                key = { it.id }
                            ) { movie ->
                                MovieItem(
                                    movie = movie,
                                    onClick = { navController.navigate(Screen.MovieDetail.createRoute(movie.id)) }
                                )

                                if (movie == movies.value.lastOrNull()) {
                                    if (hasMorePages.value && !isLoadingMore.value) {
                                        LaunchedEffect(Unit) {
                                            viewModel.loadMore()
                                        }
                                    }
                                }
                            }

                            item {
                                when {
                                    isLoadingMore.value -> {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(32.dp),
                                                    strokeWidth = 2.dp
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = "Loading more...",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                    paginationError.value != null && hasMorePages.value -> {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = paginationError.value!!,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Button(
                                                onClick = { viewModel.loadMore() }
                                            ) {
                                                Text("Try Loading More")
                                            }
                                        }
                                    }
                                    !hasMorePages.value -> {
                                        Text(
                                            text = "No more movies to load",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
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
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        placeholder = { Text("Search movies...") },
        trailingIcon = {
            IconButton(onClick = onSearch) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch() }
        )
    )
}

@Composable
fun MovieItem(movie: Movie, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w185${movie.posterPath}",
                contentDescription = movie.title,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = movie.releaseDate,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private fun getErrorMessage(error: String, searchQuery: String): String {
    return when {
        error.contains("Unable to resolve host") -> 
            "No internet connection. Please check your network and try again."
        error.contains("timeout") -> 
            "The request timed out. Please try again."
        searchQuery.isNotBlank() -> 
            "Failed to search for '$searchQuery'. Please try again."
        else -> 
            "Failed to load movies. Please try again."
    }
} 