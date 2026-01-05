# Google Calendar Integration - Implementation Guide

## Overview
This document outlines the Google Calendar sync integration for Plannit app.

## Architecture

### Layer Structure
```
UI Layer (Compose)
    ↓
ViewModel Layer
    ↓
Use Case Layer (Domain)
    ↓
Repository Layer
    ↓
Data Sources (Local DB + Remote API)
```

### Key Components

#### 1. Authentication (`data/auth/`)
- `GoogleAuthManager`: Handles OAuth2 flow, token management
- `TokenStore`: Secure token persistence using EncryptedSharedPreferences

#### 2. Data Layer (`data/`)
- **Local**: Room DAOs for Calendar, Event, SyncState
- **Remote**: Retrofit API for Google Calendar v3
- **Repository**: `GoogleCalendarRepository` - orchestrates sync logic

#### 3. Sync Engine (`data/sync/`)
- `CalendarSyncManager`: Coordinates full and incremental sync
- `ConflictResolver`: Handles ETag conflicts with last-write-wins
- `SyncWorker`: Background periodic sync using WorkManager

#### 4. Domain Layer (`domain/`)
- Use cases for calendar operations
- Business logic for conflict resolution
- Event expansion for recurring events

#### 5. Presentation Layer (`presentation/`)
- Calendar settings screen
- Sync diagnostics screen
- Conflict resolution UI

## Database Schema (Version 2)

### CalendarEntity
- id (PK): Google Calendar ID
- summary, description, color
- isSelected, isPrimary
- updatedAt

### EventEntity (Updated)
- id (PK, auto-increment)
- Existing fields...
- **New fields:**
  - googleEventId, googleCalendarId
  - etag (for conflict detection)
  - recurringRule (RRULE string)
  - startTimezone, endTimezone
  - isDeleted, isDirty
  - lastSyncedAt

### SyncStateEntity
- calendarId (PK)
- syncToken (for incremental sync)
- lastSyncAt
- lastSyncStatus, errorMessage

## Sync Flow

### Initial Sync (Full)
1. User authenticates with Google
2. Fetch calendar list
3. User selects calendars to sync
4. For each selected calendar:
   - Fetch all events (timeMin = now - 1 year)
   - Store events locally
   - Save nextSyncToken

### Incremental Sync
1. Retrieve syncToken from SyncState
2. Call events.list with syncToken
3. Process changes:
   - New/updated events → upsert locally
   - status="cancelled" → mark as deleted
4. Save new syncToken
5. Handle 410 Gone → trigger full resync

### Conflict Resolution
- Use ETag with If-Match header
- On 412 Precondition Failed:
  - Fetch latest version from Google
  - Compare timestamps
  - Last-write-wins (unless user has unsaved edits)
  - Show merge dialog if needed

### Offline Support
- All CRUD operations hit local DB first
- Set isDirty flag for modified events
- Queue dirty events for next sync
- Push dirty events when online

## API Scopes Required
```
https://www.googleapis.com/auth/calendar.events
```

## GCP Setup Instructions

### 1. Create Project
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create new project: "Plannit Calendar Sync"
3. Enable Google Calendar API

### 2. Configure OAuth Consent Screen
1. Navigate to APIs & Services → OAuth consent screen
2. Choose "External" user type
3. Fill in app information:
   - App name: Plannit
   - User support email
   - Developer contact
4. Add scope: `../auth/calendar.events`
5. Add test users (for development)

### 3. Create OAuth 2.0 Credentials
1. APIs & Services → Credentials
2. Create OAuth client ID
3. Application type: Android
4. Package name: `com.example.plannit`
5. SHA-1 certificate fingerprint:
   ```bash
   # Debug keystore
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
   ```
6. Download client configuration

### 4. Add to Project
Create `app/src/main/res/values/google_config.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="google_client_id">YOUR_CLIENT_ID.apps.googleusercontent.com</string>
    <string name="google_server_client_id">YOUR_WEB_CLIENT_ID.apps.googleusercontent.com</string>
</resources>
```

## Implementation Status

### ✅ Completed
- [x] Dependencies added (Google Sign-In, Retrofit, OkHttp)
- [x] Database schema updated (v2)
- [x] New entities: CalendarEntity, SyncStateEntity
- [x] EventEntity extended with sync fields
- [x] DAOs created for all entities
- [x] Google Calendar API DTOs
- [x] Retrofit API interface

### 🚧 In Progress
- [ ] Authentication layer
- [ ] Token management
- [ ] Repository implementation
- [ ] Sync engine
- [ ] Conflict resolver
- [ ] Background sync worker
- [ ] UI components
- [ ] Unit tests

### 📋 TODO
- [ ] Recurring event expansion
- [ ] Timezone handling
- [ ] Notification rescheduling
- [ ] Error handling & retry logic
- [ ] Sync diagnostics screen
- [ ] Integration tests
- [ ] Documentation

## Testing Strategy

### Unit Tests
- Repository merge logic
- SyncToken handling (full vs incremental)
- Deletion tombstones
- ETag conflict scenarios
- Recurring event expansion

### Integration Tests
- Auth flow (mocked)
- List/insert/update/delete (mocked HTTP)
- Full sync flow
- Incremental sync flow
- Conflict resolution

## Next Steps

1. **Implement Authentication**
   - GoogleAuthManager with OAuth2 flow
   - Secure token storage
   - Token refresh logic

2. **Build Sync Engine**
   - CalendarSyncManager
   - Full sync implementation
   - Incremental sync with syncToken
   - Conflict resolution

3. **Create UI**
   - Google Sign-In button
   - Calendar selection screen
   - Sync now action
   - Sync diagnostics

4. **Background Sync**
   - WorkManager periodic sync
   - Battery-friendly scheduling
   - Network constraints

5. **Testing**
   - Write unit tests
   - Integration tests
   - Manual testing with real Google account

## Notes

- **Offline-first**: All operations work offline, sync when online
- **Battery-friendly**: Sync every 30-60 minutes with constraints
- **Conflict policy**: Last-write-wins with timestamp comparison
- **Recurring events**: Expand using RRULE for display
- **Notifications**: Reschedule on every event change
- **Error handling**: Exponential backoff on 429/5xx errors

## Resources

- [Google Calendar API v3 Docs](https://developers.google.com/calendar/api/v3/reference)
- [OAuth 2.0 for Mobile Apps](https://developers.google.com/identity/protocols/oauth2/native-app)
- [Incremental Sync](https://developers.google.com/calendar/api/guides/sync)
- [Recurring Events](https://developers.google.com/calendar/api/concepts/events-calendars#recurring_events)
