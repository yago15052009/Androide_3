# Rick and Morty Explorer

App Android que consome a **Rick and Morty API** para listar e buscar personagens da série, seguindo arquitetura MVVM com gerenciamento de estado via `sealed interface`.

## API Utilizada

**Rick and Morty API** — `https://rickandmortyapi.com/api/`

- Endpoint: `GET /character?name={nome}&page={pagina}`
- Gratuita, sem autenticação
- Retorna lista paginada de personagens com nome, status, espécie, gênero, imagem, origem e localização atual

## Funcionalidades

- Listagem de todos os personagens (paginada)
- Busca por nome em tempo real
- Navegação entre páginas (anterior / próxima)
- Indicador visual de status (verde = vivo, vermelho = morto, cinza = desconhecido)
- Tratamento de erro com mensagem e botão de retry
- Loading indicator durante as requisições

## Arquitetura MVVM em Camadas

```
model/
  Character.kt        → data classes mapeando o JSON da API

network/
  RickAndMortyApi.kt  → interface Retrofit com @GET + cliente configurado

viewmodel/
  CharacterViewModel  → ViewModel com viewModelScope e StateFlow<CharacterState>
  CharacterState      → sealed interface: Idle | Loading | Success | Error

ui/ (theme/)
MainActivity.kt       → UI Compose: busca + LazyColumn + paginação
```

### Gerenciamento de Estado

```kotlin
sealed interface CharacterState {
    data object Idle : CharacterState
    data object Loading : CharacterState
    data class Success(characters, currentPage, totalPages) : CharacterState
    data class Error(message: String) : CharacterState
}
```

A UI nunca chama o Retrofit diretamente — apenas observa o `StateFlow` do ViewModel e dispara ações via callbacks.

## Como rodar

1. Clone o repositório
2. Abra no Android Studio (Electric Eel ou superior)
3. Execute em dispositivo ou emulador com Android 7.0+ (API 24) e internet
4. A lista carrega automaticamente; use a barra de busca para filtrar por nome

## Tecnologias

- Kotlin + Jetpack Compose
- Retrofit 2 + Gson (camada de rede)
- Coroutines + viewModelScope (assincronismo)
- ViewModel + StateFlow (gerenciamento de estado)
- Coil (carregamento de imagens)
