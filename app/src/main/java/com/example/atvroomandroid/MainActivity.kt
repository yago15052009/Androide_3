package com.example.atvroomandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.atvroomandroid.data.MovieEntity
import com.example.atvroomandroid.ui.theme.AtvRoomAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AtvRoomAndroidTheme {
                GhibliScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GhibliScreen(vm: MovieViewModel = viewModel()) {
    val movies by vm.movies.collectAsState()
    val syncState by vm.syncState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diário de Filmes Ghibli") },
                actions = {
                    if (syncState is SyncState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        TextButton(onClick = { vm.sync() }) { Text("Sync") }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (syncState is SyncState.Error) {
                Snackbar(
                    modifier = Modifier.padding(8.dp),
                    action = { TextButton(onClick = { vm.sync() }) { Text("Tentar novamente") } }
                ) {
                    Text((syncState as SyncState.Error).message)
                }
            }

            if (movies.isEmpty() && syncState !is SyncState.Loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum filme encontrado. Verifique sua conexão.")
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(movies, key = { it.id }) { movie ->
                        MovieCard(movie)
                    }
                }
            }
        }
    }
}

@Composable
fun MovieCard(movie: MovieEntity) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(movie.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Diretor: ${movie.director}", style = MaterialTheme.typography.bodySmall)
            Text("Ano: ${movie.releaseDate}  •  RT Score: ${movie.rtScore}%", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(4.dp))
            Text(
                movie.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3
            )
        }
    }
}
