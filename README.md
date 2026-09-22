# Diário de Filmes Ghibli

App Android que exibe os filmes do Studio Ghibli com persistência local via Room e sincronização com a Studio Ghibli API.

## API Utilizada

**Studio Ghibli API** — `https://ghibliapi.vercel.app/`

- Endpoint: `GET /films`
- Gratuita, sem autenticação
- Retorna lista de filmes com id, título, descrição, diretor, ano de lançamento e nota RT Score

## Arquitetura

```
GhibliApi (Retrofit)
       ↓  syncFromApi()
  MovieRepository  ←→  MovieDao (Room / AppDatabase)
       ↓  observeMovies(): Flow
  MovieViewModel (AndroidViewModel)
       ↓  StateFlow<List<MovieEntity>>
  GhibliScreen (Compose)
```

### Single Source of Truth

A UI **nunca** lê da API diretamente. O fluxo é:

1. `MovieViewModel.init` chama `repository.syncFromApi()`
2. O Repository busca os filmes na API via Retrofit
3. Os filmes são gravados no banco Room via `dao.upsertAll()`
4. A UI observa `dao.observeAll()` como `Flow` → `StateFlow`

### Offline-First

- Ao abrir o app sem internet, os dados já salvos no Room continuam aparecendo
- Um erro de rede exibe um `Snackbar` com opção de retry, **sem apagar** a lista existente

### Estado de Sincronização

`SyncState` é um `sealed interface` separado da lista de filmes:

```kotlin
sealed interface SyncState {
    data object Idle : SyncState
    data object Loading : SyncState
    data class Error(val message: String) : SyncState
}
```

## Estrutura de Arquivos

```
app/src/main/java/com/example/atvroomandroid/
├── data/
│   ├── MovieEntity.kt      # @Entity Room
│   ├── MovieDao.kt         # @Dao com Flow e Upsert
│   ├── AppDatabase.kt      # @Database Room
│   └── MovieRepository.kt  # Single Source of Truth
├── network/
│   └── GhibliApi.kt        # Interface Retrofit
├── MovieViewModel.kt       # AndroidViewModel + StateFlow
└── MainActivity.kt         # UI Compose
```

## Como executar

1. Clone o repositório
2. Abra no Android Studio
3. Execute em dispositivo/emulador com Android 7.0+ (API 24)
4. O app sincroniza automaticamente ao abrir; use o botão **Sync** para atualizar manualmente
