package com.example.atvapis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.atvapis.model.Character
import com.example.atvapis.ui.theme.AtvAPIsTheme
import com.example.atvapis.viewmodel.CharacterState
import com.example.atvapis.viewmodel.CharacterViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AtvAPIsTheme {
                CharacterScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterScreen(vm: CharacterViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    var query by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Rick and Morty") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            SearchBar(
                query = query,
                onQueryChange = { query = it },
                onSearch = { vm.search(query.trim()) }
            )

            when (val s = state) {
                is CharacterState.Idle -> Unit

                is CharacterState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is CharacterState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(s.message, style = MaterialTheme.typography.bodyLarge)
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = { vm.search(query.trim()) }) { Text("Tentar novamente") }
                        }
                    }
                }

                is CharacterState.Success -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(s.characters, key = { it.id }) { character ->
                            CharacterCard(character)
                        }
                    }
                    PaginationBar(
                        currentPage = s.currentPage,
                        totalPages = s.totalPages,
                        onPrev = { vm.prevPage() },
                        onNext = { vm.nextPage() }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, onSearch: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Buscar personagem...") },
            singleLine = true
        )
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = onSearch) {
            Icon(Icons.Default.Search, contentDescription = "Buscar")
        }
    }
}

@Composable
fun CharacterCard(character: Character) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = character.image,
                contentDescription = character.name,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(character.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusDot(character.status)
                    Spacer(Modifier.width(4.dp))
                    Text("${character.status} — ${character.species}", style = MaterialTheme.typography.bodySmall)
                }
                Text("Gênero: ${character.gender}", style = MaterialTheme.typography.bodySmall)
                Text("Origem: ${character.origin.name}", style = MaterialTheme.typography.bodySmall)
                Text("Local: ${character.location.name}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun StatusDot(status: String) {
    val color = when (status) {
        "Alive" -> Color(0xFF4CAF50)
        "Dead" -> Color(0xFFF44336)
        else -> Color.Gray
    }
    Surface(
        modifier = Modifier.size(8.dp),
        shape = CircleShape,
        color = color
    ) {}
}

@Composable
fun PaginationBar(currentPage: Int, totalPages: Int, onPrev: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onPrev, enabled = currentPage > 1) { Text("← Anterior") }
        Text("$currentPage / $totalPages", style = MaterialTheme.typography.bodyMedium)
        TextButton(onClick = onNext, enabled = currentPage < totalPages) { Text("Próxima →") }
    }
}
