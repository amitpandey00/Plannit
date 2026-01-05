# Plannit - Offline-First Calendar & To-Do App

A complete offline-first calendar and to-do management application built with Jetpack Compose, following clean architecture principles and MVVM pattern.

## Features

### Calendar Management
- **Multiple View Modes**: Daily, Weekly, and Monthly calendar views
- **Event Creation**: Create events with title, description, location, and time
- **Color-Coded Events**: Visual organization with customizable event colors
- **All-Day Events**: Support for full-day events
- **Event Notifications**: Configurable reminders before events

### Task Management
- **Task Creation**: Create tasks with title, description, and priority levels
- **Priority Levels**: LOW, MEDIUM, HIGH, URGENT
- **Task Completion**: Mark tasks as done with completion tracking
- **Due Dates**: Optional due date/time for tasks
- **Task Notifications**: Reminders for tasks with due dates
- **Filter Views**: Toggle between active and completed tasks

### Offline-First Architecture
- **Local Database**: Room database for persistent storage
- **No Backend Required**: Fully functional without internet connection
- **Instant Sync**: All data stored and retrieved locally

### Notifications
- **Local Notifications**: WorkManager-based notification scheduling
- **Customizable Reminders**: Set reminder time before events/tasks
- **Persistent Scheduling**: Notifications survive app restarts

## Architecture

### Clean Architecture Layers

```
presentation/          # UI Layer (Compose + ViewModels)
├── calendar/         # Calendar screens and ViewModels
├── tasks/            # Task screens and ViewModels
├── addevent/         # Add event screen
├── addtask/          # Add task screen
└── navigation/       # Navigation setup

domain/               # Business Logic Layer
├── model/           # Domain models (Event, Task)
├── repository/      # Repository interfaces
└── usecase/         # Use cases
    ├── event/       # Event-related use cases
    └── task/        # Task-related use cases

data/                # Data Layer
├── local/           # Local data sources
│   ├── entity/     # Room entities
│   ├── dao/        # Data Access Objects
│   ├── database/   # Database configuration
│   └── converter/  # Type converters
├── repository/      # Repository implementations
├── mapper/          # Entity-Domain mappers
└── notification/    # Notification system
```

### SOLID Principles Applied

1. **Single Responsibility**: Each class has one clear purpose
   - ViewModels handle UI state
   - Repositories manage data operations
   - Use cases contain business logic

2. **Open/Closed**: Extensible through interfaces
   - Repository interfaces allow multiple implementations
   - Use cases can be composed and extended

3. **Liskov Substitution**: Interface-based design
   - Repository implementations are interchangeable
   - Domain models independent of data layer

4. **Interface Segregation**: Focused interfaces
   - Separate DAOs for Events and Tasks
   - Specific use cases for each operation

5. **Dependency Inversion**: Depend on abstractions
   - ViewModels depend on repository interfaces
   - Use cases depend on repository interfaces

## Technology Stack

- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVVM + Clean Architecture
- **Database**: Room (SQLite)
- **Async**: Kotlin Coroutines + Flow
- **Notifications**: WorkManager
- **Navigation**: Jetpack Navigation Compose
- **Dependency Injection**: Manual (Factory pattern)

## Project Structure

### Data Models

**Event**
- Title, description, location
- Start and end date/time
- Color coding
- All-day flag
- Notification settings

**Task**
- Title, description
- Due date/time (optional)
- Priority level
- Completion status
- Notification settings

### Key Components

**Database**
- `PlannitDatabase`: Room database singleton
- `EventDao`: Event data access
- `TaskDao`: Task data access
- `Converters`: LocalDateTime and enum type converters

**Repositories**
- `EventRepository`: Event data operations
- `TaskRepository`: Task data operations

**Use Cases**
- `GetEventsForDateRangeUseCase`: Fetch events for calendar views
- `CreateEventUseCase`: Create new events
- `GetActiveTasksUseCase`: Fetch incomplete tasks
- `ToggleTaskCompletionUseCase`: Mark tasks complete/incomplete

**ViewModels**
- `CalendarViewModel`: Calendar state and operations
- `TaskViewModel`: Task list state and operations

**Notification System**
- `NotificationHelper`: Notification channel and display
- `NotificationScheduler`: Schedule event/task reminders
- `ReminderWorker`: WorkManager worker for notifications

## Setup & Build

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle dependencies
4. Run on device or emulator (API 24+)

## Permissions

- `POST_NOTIFICATIONS`: Display notifications (Android 13+)
- `SCHEDULE_EXACT_ALARM`: Schedule precise reminders

## Future Enhancements

- Event editing and details view
- Task editing and details view
- Recurring events and tasks
- Categories and tags
- Search functionality
- Data export/import
- Widget support
- Dark theme customization
- Calendar sync (optional)

## License

This project is for educational purposes.


---

## Google Calendar Integration (NEW)

### Overview
Plannit now supports bidirectional sync with Google Calendar, allowing you to:
- Connect your Google account
- Select which calendars to sync
- Pull events from Google Calendar
- Push local events to Google Calendar
- Handle conflicts automatically
- Work offline with automatic sync when online

### Features

#### Authentication
- OAuth 2.0 Google Sign-In
- Secure token storage
- Automatic token refresh
- Minimal scope: `calendar.events`

#### Sync Capabilities
- **Initial Sync**: Full fetch of selected calendars
- **Incremental Sync**: Efficient updates using syncToken
- **Conflict Resolution**: ETag-based with last-write-wins
- **Offline Support**: All operations work offline, sync when online
- **Background Sync**: Periodic sync every 30-60 minutes

#### Supported Features
- Single and recurring events (RRULE)
- Event CRUD operations
- Multiple calendar support
- Timezone handling
- Local notifications for synced events

### Google Cloud Platform Setup

#### Prerequisites
- Google Cloud Platform account
- Android Studio with debug keystore

#### Step 1: Create GCP Project

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Click "Select a project" → "New Project"
3. Enter project name: "Plannit Calendar Sync"
4. Click "Create"

#### Step 2: Enable Google Calendar API

1. In your project, go to "APIs & Services" → "Library"
2. Search for "Google Calendar API"
3. Click on it and press "Enable"

#### Step 3: Configure OAuth Consent Screen

1. Go to "APIs & Services" → "OAuth consent screen"
2. Select "External" user type → Click "Create"
3. Fill in required information:
   - **App name**: Plannit
   - **User support email**: Your email
   - **Developer contact**: Your email
4. Click "Save and Continue"
5. On "Scopes" page, click "Add or Remove Scopes"
6. Search and add: `https://www.googleapis.com/auth/calendar.events`
7. Click "Update" → "Save and Continue"
8. On "Test users" page, add your Google account email
9. Click "Save and Continue" → "Back to Dashboard"

#### Step 4: Create OAuth 2.0 Credentials

##### Get SHA-1 Fingerprint

Open terminal and run:

```bash
# For debug keystore (development)
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

# For release keystore (production)
keytool -list -v -keystore /path/to/your/release.keystore -alias your-alias
```

Copy the SHA-1 fingerprint from the output.

##### Create Android OAuth Client

1. Go to "APIs & Services" → "Credentials"
2. Click "Create Credentials" → "OAuth client ID"
3. Select "Android" as application type
4. Fill in:
   - **Name**: Plannit Android
   - **Package name**: `com.example.plannit`
   - **SHA-1 certificate fingerprint**: Paste your SHA-1
5. Click "Create"
6. Note down the **Client ID**

##### Create Web OAuth Client (for server auth code)

1. Click "Create Credentials" → "OAuth client ID" again
2. Select "Web application"
3. Name: "Plannit Web"
4. Click "Create"
5. Note down the **Client ID**

#### Step 5: Configure App

1. Copy `app/src/main/res/values/google_config.xml.template` to `google_config.xml`
2. Replace placeholders with your client IDs:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="google_client_id">YOUR_ANDROID_CLIENT_ID.apps.googleusercontent.com</string>
    <string name="google_server_client_id">YOUR_WEB_CLIENT_ID.apps.googleusercontent.com</string>
</resources>
```

3. Add to `.gitignore`:
```
app/src/main/res/values/google_config.xml
```

### Usage

#### Connect Google Calendar

1. Open Plannit app
2. Go to Settings → "Connect Google Calendar"
3. Sign in with your Google account
4. Grant calendar permissions
5. Select calendars to sync
6. Tap "Sync Now"

#### Sync Management

- **Manual Sync**: Tap "Sync Now" button
- **Auto Sync**: Runs every 30-60 minutes in background
- **Sync Status**: View in "Sync Diagnostics" screen

#### Conflict Resolution

When conflicts occur (same event edited on both sides):
- App compares timestamps
- Last-write-wins by default
- Shows merge dialog if you have unsaved local edits
- You can choose which version to keep

#### Offline Mode

- All operations work offline
- Changes are queued locally
- Automatically sync when online
- Dirty events are pushed on next sync

### Sync Diagnostics

Access via Settings → "Sync Diagnostics" to view:
- Last sync time for each calendar
- Sync token status
- Pending dirty items (not yet pushed)
- Sync errors and retry status

### Architecture Details

See [GOOGLE_CALENDAR_INTEGRATION.md](GOOGLE_CALENDAR_INTEGRATION.md) for:
- Detailed architecture
- Database schema
- Sync flow diagrams
- API documentation
- Testing strategy
- Implementation status

### Troubleshooting

#### "Sign-in failed" error
- Verify SHA-1 fingerprint matches your keystore
- Check OAuth consent screen is configured
- Ensure test user email is added (for external apps)

#### "API not enabled" error
- Enable Google Calendar API in GCP Console
- Wait a few minutes for propagation

#### Sync not working
- Check internet connection
- Verify calendar is selected in settings
- Check Sync Diagnostics for errors
- Try manual "Sync Now"

#### Events not appearing
- Ensure calendar is selected for sync
- Check date range (syncs 1 year back by default)
- Verify events are not marked as deleted

### Privacy & Security

- **Minimal Scope**: Only `calendar.events` permission
- **Secure Storage**: Tokens encrypted with EncryptedSharedPreferences
- **Local-First**: All data stored locally, sync is optional
- **No Backend**: Direct Google API communication
- **Revocable**: Disconnect anytime from settings

### Limitations

- Requires Google account
- Internet needed for sync (offline mode available)
- Recurring events expanded for display only
- Attachments not supported
- Attendees/invitations not supported (future enhancement)

### API Rate Limits

Google Calendar API has rate limits:
- 1,000,000 queries per day
- 10 queries per second per user

Plannit handles this with:
- Incremental sync (reduces API calls)
- Exponential backoff on rate limit errors
- Batch operations where possible

### Testing

For development/testing:
1. Use debug keystore SHA-1
2. Add your test account to OAuth consent screen
3. App will show "unverified app" warning (normal for testing)
4. For production, submit app for verification

### Support

For issues or questions:
1. Check [GOOGLE_CALENDAR_INTEGRATION.md](GOOGLE_CALENDAR_INTEGRATION.md)
2. Review GCP Console for API errors
3. Check Sync Diagnostics in app
4. Verify OAuth configuration

#   P l a n n i t  
 