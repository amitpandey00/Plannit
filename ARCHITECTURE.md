# Plannit Architecture Documentation

## Overview

Plannit follows Clean Architecture principles with strict separation of concerns across three main layers: Presentation, Domain, and Data. The architecture ensures testability, maintainability, and scalability.

## Architecture Layers

### 1. Presentation Layer (UI)

**Responsibility**: Handle user interactions and display data

**Components**:
- **Composables**: UI screens built with Jetpack Compose
- **ViewModels**: Manage UI state and handle user actions
- **ViewModelFactories**: Create ViewModels with dependencies

**Key Principles**:
- ViewModels never reference Android framework classes (except AndroidViewModel when needed)
- UI state exposed as StateFlow for reactive updates
- Single source of truth for UI state
- Unidirectional data flow

**Example Flow**:
```
User Action → Composable → ViewModel → Use Case → Repository
                ↑                                      ↓
                └──────── StateFlow ←─────────────────┘
```

### 2. Domain Layer (Business Logic)

**Responsibility**: Define business rules and models

**Components**:
- **Models**: Pure Kotlin data classes (Event, Task)
- **Repository Interfaces**: Define data operations contracts
- **Use Cases**: Encapsulate single business operations

**Key Principles**:
- No Android dependencies
- Pure Kotlin code
- Reusable business logic
- Single Responsibility per use case

**Use Case Pattern**:
```kotlin
class GetEventsForDateRangeUseCase(
    private val repository: EventRepository
) {
    operator fun invoke(start: LocalDateTime, end: LocalDateTime): Flow<List<Event>> {
        return repository.getEventsBetween(start, end)
    }
}
```

### 3. Data Layer

**Responsibility**: Manage data sources and persistence

**Components**:
- **Entities**: Room database entities
- **DAOs**: Data Access Objects for database operations
- **Database**: Room database configuration
- **Repository Implementations**: Concrete implementations of repository interfaces
- **Mappers**: Convert between entities and domain models
- **Type Converters**: Handle complex types in Room

**Key Principles**:
- Repository pattern for data abstraction
- Mapper pattern for layer separation
- Flow for reactive data streams
- Single source of truth (database)

## Data Flow

### Reading Data (Query)

```
UI (Composable)
    ↓ collect StateFlow
ViewModel
    ↓ observe Flow
Use Case
    ↓ call repository
Repository Interface
    ↓ implementation
Repository Impl
    ↓ query
DAO
    ↓ SQL query
Room Database
    ↓ map to entity
Entity
    ↓ mapper
Domain Model
    ↓ Flow emission
    ↑ (back to ViewModel)
```

### Writing Data (Command)

```
UI (Composable)
    ↓ user action
ViewModel
    ↓ launch coroutine
Use Case
    ↓ business logic
Repository Interface
    ↓ implementation
Repository Impl
    ↓ map to entity
Entity
    ↓ insert/update
DAO
    ↓ SQL operation
Room Database
```

## MVVM Pattern Implementation

### ViewModel Responsibilities

1. **State Management**: Hold and expose UI state
2. **Event Handling**: Process user actions
3. **Use Case Orchestration**: Call appropriate use cases
4. **Lifecycle Awareness**: Survive configuration changes

### Example: CalendarViewModel

```kotlin
class CalendarViewModel(
    private val repository: EventRepository,
    private val getEventsForDateRange: GetEventsForDateRangeUseCase,
    private val createEvent: CreateEventUseCase
) : ViewModel() {
    
    // State
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()
    
    // Derived state from multiple sources
    val events: StateFlow<List<Event>> = combine(
        selectedDate,
        viewMode
    ) { date, mode ->
        val (start, end) = getDateRange(date, mode)
        getEventsForDateRange(start, end)
    }.flatMapLatest { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Actions
    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }
    
    fun addEvent(event: Event) {
        viewModelScope.launch {
            createEvent(event)
        }
    }
}
```

## Dependency Flow

```
MainActivity
    ↓ creates
PlannitApplication
    ↓ provides
Database Instance
    ↓ provides
DAOs
    ↓ injected into
Repository Implementations
    ↓ injected into
Use Cases
    ↓ injected into
ViewModels (via Factory)
    ↓ used by
Composables
```

## Offline-First Strategy

### Data Persistence

1. **Single Source of Truth**: Room database
2. **No Network Layer**: All data stored locally
3. **Immediate Availability**: No loading states for network
4. **Reactive Updates**: Flow-based observation

### Benefits

- Works without internet connection
- Instant data access
- No sync conflicts
- Simplified architecture
- Better performance

## Notification System

### Architecture

```
Event/Task Creation
    ↓
NotificationScheduler
    ↓
WorkManager
    ↓ schedules
ReminderWorker
    ↓ at scheduled time
NotificationHelper
    ↓ displays
System Notification
```

### Components

1. **NotificationHelper**: Creates notification channels and displays notifications
2. **NotificationScheduler**: Schedules notifications using WorkManager
3. **ReminderWorker**: Executes at scheduled time to show notification

### Scheduling Strategy

- Use WorkManager for reliable scheduling
- OneTimeWorkRequest with exact delay
- Unique work names to prevent duplicates
- Cancellation support when events/tasks deleted

## Testing Strategy

### Unit Tests

**Domain Layer**:
- Use cases with mock repositories
- Business logic validation
- Pure Kotlin, easy to test

**ViewModel Layer**:
- Test state changes
- Test user actions
- Mock use cases and repositories

### Integration Tests

**Repository Layer**:
- Test with in-memory Room database
- Verify data operations
- Test mappers

### UI Tests

**Composables**:
- Test user interactions
- Verify UI state rendering
- Navigation testing

## Design Patterns Used

1. **Repository Pattern**: Abstract data sources
2. **Use Case Pattern**: Encapsulate business logic
3. **Factory Pattern**: Create ViewModels with dependencies
4. **Mapper Pattern**: Convert between layers
5. **Observer Pattern**: Flow-based reactive updates
6. **Singleton Pattern**: Database instance
7. **Strategy Pattern**: Different calendar view modes

## SOLID Principles in Practice

### Single Responsibility Principle (SRP)

- Each ViewModel manages one screen's state
- Each use case performs one operation
- Each repository handles one entity type
- Each DAO manages one table

### Open/Closed Principle (OCP)

- Repository interfaces allow new implementations
- Use cases can be extended without modification
- ViewModels depend on abstractions, not concrete classes

### Liskov Substitution Principle (LSP)

- Any EventRepository implementation can replace another
- Domain models don't depend on implementation details

### Interface Segregation Principle (ISP)

- Separate DAOs for Events and Tasks
- Focused repository interfaces
- Specific use cases for each operation

### Dependency Inversion Principle (DIP)

- High-level modules (ViewModels) depend on abstractions (Repository interfaces)
- Low-level modules (Repository implementations) depend on abstractions
- Domain layer has no dependencies on data or presentation layers

## Performance Considerations

### Database Optimization

- Indexed columns for fast queries
- Efficient query design
- Flow-based reactive queries (no polling)

### UI Performance

- LazyColumn for efficient list rendering
- Remember and derivedStateOf for computation caching
- StateFlow with WhileSubscribed for lifecycle awareness

### Memory Management

- ViewModelScope for automatic coroutine cancellation
- Flow collection tied to lifecycle
- Proper cleanup in ViewModels

## Scalability

### Adding New Features

1. **New Entity**: Create entity, DAO, repository, use cases
2. **New Screen**: Create composable, ViewModel, navigation
3. **New Business Logic**: Create use case, inject into ViewModel

### Modularization Ready

The architecture supports future modularization:
- `:domain` module (pure Kotlin)
- `:data` module (Android dependencies)
- `:presentation` module (UI)
- `:app` module (composition)

## Security Considerations

- Local data only (no network exposure)
- Room database encrypted (can be added)
- No sensitive data in logs
- Proper permission handling for notifications

## Conclusion

This architecture provides:
- Clear separation of concerns
- Testable components
- Maintainable codebase
- Scalable structure
- Offline-first reliability
- SOLID principles adherence
- Clean code practices
