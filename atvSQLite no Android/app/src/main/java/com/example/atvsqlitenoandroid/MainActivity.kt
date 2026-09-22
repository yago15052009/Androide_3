package com.example.atvsqlitenoandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.atvsqlitenoandroid.model.Movie
import com.example.atvsqlitenoandroid.ui.theme.AtvSQLiteNoAndroidTheme
import com.example.atvsqlitenoandroid.viewmodel.MovieViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AtvSQLiteNoAndroidTheme {
                MovieDiaryScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDiaryScreen(vm: MovieViewModel = viewModel()) {
    val movies by vm.movies.collectAsState()
    var showForm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Diário de Filmes") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showForm = true }) {
                Text("+", style = MaterialTheme.typography.titleLarge)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (movies.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum filme cadastrado ainda.")
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(movies, key = { it.id }) { movie ->
                        MovieItem(movie, onDelete = { vm.remove(movie.id) })
                    }
                }
            }
        }

        if (showForm) {
            MovieFormDialog(
                onDismiss = { showForm = false },
                onSave = { movie ->
                    vm.add(movie)
                    showForm = false
                }
            )
        }
    }
}

@Composable
fun MovieItem(movie: Movie, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(movie.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${movie.director} • ${movie.year}", style = MaterialTheme.typography.bodySmall)
                Text("Nota: ${"%.1f".format(movie.rating)}/10", style = MaterialTheme.typography.bodySmall)
                if (movie.notes.isNotBlank()) {
                    Text(movie.notes, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Remover")
            }
        }
    }
}

@Composable
fun MovieFormDialog(onDismiss: () -> Unit, onSave: (Movie) -> Unit) {
    var title by remember { mutableStateOf("") }
    var director by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Filme") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título *") }, singleLine = true)
                OutlinedTextField(value = director, onValueChange = { director = it }, label = { Text("Diretor *") }, singleLine = true)
                OutlinedTextField(
                    value = year, onValueChange = { year = it }, label = { Text("Ano *") },
                    singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = rating, onValueChange = { rating = it }, label = { Text("Nota (0-10) *") },
                    singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Observações") }, maxLines = 3)
                if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val y = year.toIntOrNull()
                val r = rating.toFloatOrNull()
                when {
                    title.isBlank() || director.isBlank() -> error = "Título e diretor são obrigatórios."
                    y == null || y < 1888 || y > 2100 -> error = "Ano inválido."
                    r == null || r < 0f || r > 10f -> error = "Nota deve ser entre 0 e 10."
                    else -> onSave(Movie(title = title.trim(), director = director.trim(), year = y, rating = r, notes = notes.trim()))
                }
            }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
