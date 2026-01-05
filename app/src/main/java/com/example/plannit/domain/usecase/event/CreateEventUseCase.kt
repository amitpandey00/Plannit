package com.example.plannit.domain.usecase.event

import com.example.plannit.domain.model.Event
import com.example.plannit.domain.repository.EventRepository
import java.time.LocalDateTime

class CreateEventUseCase(
    private val repository: EventRepository
) {
    suspend operator fun invoke(event: Event): Long {
        val updatedEvent = event.copy(
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        return repository.insertEvent(updatedEvent)
    }
}
