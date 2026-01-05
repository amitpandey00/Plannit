package com.example.plannit.data.mapper

import com.example.plannit.data.local.entity.EventEntity
import com.example.plannit.domain.model.Event

fun EventEntity.toDomain(): Event {
    return Event(
        id = id,
        title = title,
        description = description,
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        location = location,
        color = color,
        isAllDay = isAllDay,
        notificationMinutesBefore = notificationMinutesBefore,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Event.toEntity(): EventEntity {
    return EventEntity(
        id = id,
        title = title,
        description = description,
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        location = location,
        color = color,
        isAllDay = isAllDay,
        notificationMinutesBefore = notificationMinutesBefore,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
