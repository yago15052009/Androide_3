# Diário de Filmes Assistidos

App Android de cadastro de filmes com persistência 100% local via SQLite nativo.

## Tema

Diário de Filmes Assistidos — registre os filmes que você já viu com título, diretor, ano, nota e observações.

## Funcionalidades

- Cadastrar filme (título, diretor, ano, nota 0–10, observações)
- Listar todos os filmes salvos em ordem de cadastro
- Remover filme da lista
- Dados persistem após fechar e reabrir o app

## Arquitetura em Camadas

```
model/
  Movie.kt              → data class puro, sem dependências Android

database/
  MovieDbHelper.kt      → SQLiteOpenHelper com CREATE TABLE manual via execSQL()

repository/
  MovieRepository.kt    → isola ContentValues, Cursor e SQLiteDatabase
                          todas as operações rodam em Dispatchers.IO

viewmodel/
  MovieViewModel.kt     → AndroidViewModel com StateFlow<List<Movie>>
                          não importa nada de SQLite

ui/ (theme/)
MainActivity.kt         → UI Compose: formulário + LazyColumn
```

## Como rodar

1. Clone o repositório
2. Abra no Android Studio (Electric Eel ou superior)
3. Execute em dispositivo ou emulador com Android 7.0+ (API 24)
4. Toque no botão **+** para cadastrar um filme

## Tecnologias

- Kotlin + Jetpack Compose
- SQLiteOpenHelper nativo (sem Room ou bibliotecas externas de persistência)
- Coroutines (Dispatchers.IO para operações de banco)
- ViewModel + StateFlow
