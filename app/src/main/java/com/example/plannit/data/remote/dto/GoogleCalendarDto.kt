package com.example.plannit.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CalendarListResponse(
    val items: List<GoogleCalendarDto>?
)

data class GoogleCalendarDto(
    val id: String,
    val summary: String,
    val description: String?,
    val backgroundColor: String?,
    val primary: Boolean?
)

data class EventListResponse(
    val items: List<GoogleEventDto>?,
    val nextSyncToken: String?,
    val nextPageToken: String?
)

data class GoogleEventDto(
    val id: String,
    val etag: String?,
    val status: String?, // confirmed, tentative, cancelled
    val summary: String?,
    val description: String?,
    val location: String?,
    val start: EventDateTime?,
    val end: EventDateTime?,
    val recurrence: List<String>?, // RRULE strings
    val recurringEventId: String?,
    val updated: String? // RFC3339 timestamp
)

data class EventDateTime(
    val dateTime: String?, // RFC3339 timestamp
    val date: String?, // YYYY-MM-DD for all-day events
    val timeZone: String?
)

// Request DTOs
data class CreateEventRequest(
    val summary: String,
    val description: String?,
    val location: String?,
    val start: EventDateTime,
    val end: EventDateTime,
    val recurrence: List<String>?
)

data class UpdateEventRequest(
    val summary: String,
    val description: String?,
    val location: String?,
    val start: EventDateTime,
    val end: EventDateTime,
    val recurrence: List<String>?
)
