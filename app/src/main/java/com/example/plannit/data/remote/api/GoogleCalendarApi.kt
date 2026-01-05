package com.example.plannit.data.remote.api

import com.example.plannit.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface GoogleCalendarApi {
    
    @GET("calendar/v3/users/me/calendarList")
    suspend fun getCalendarList(): Response<CalendarListResponse>
    
    @GET("calendar/v3/calendars/{calendarId}/events")
    suspend fun getEvents(
        @Path("calendarId") calendarId: String,
        @Query("timeMin") timeMin: String? = null,
        @Query("timeMax") timeMax: String? = null,
        @Query("singleEvents") singleEvents: Boolean = true,
        @Query("orderBy") orderBy: String = "startTime",
        @Query("maxResults") maxResults: Int = 250
    ): Response<EventListResponse>
    
    @GET("calendar/v3/calendars/{calendarId}/events")
    suspend fun getEventsWithSyncToken(
        @Path("calendarId") calendarId: String,
        @Query("syncToken") syncToken: String
    ): Response<EventListResponse>
    
    @GET("calendar/v3/calendars/{calendarId}/events/{eventId}")
    suspend fun getEvent(
        @Path("calendarId") calendarId: String,
        @Path("eventId") eventId: String
    ): Response<GoogleEventDto>
    
    @POST("calendar/v3/calendars/{calendarId}/events")
    suspend fun createEvent(
        @Path("calendarId") calendarId: String,
        @Body event: CreateEventRequest
    ): Response<GoogleEventDto>
    
    @PUT("calendar/v3/calendars/{calendarId}/events/{eventId}")
    suspend fun updateEvent(
        @Path("calendarId") calendarId: String,
        @Path("eventId") eventId: String,
        @Header("If-Match") etag: String?,
        @Body event: UpdateEventRequest
    ): Response<GoogleEventDto>
    
    @DELETE("calendar/v3/calendars/{calendarId}/events/{eventId}")
    suspend fun deleteEvent(
        @Path("calendarId") calendarId: String,
        @Path("eventId") eventId: String,
        @Header("If-Match") etag: String?
    ): Response<Unit>
}
