package com.example.plannit.domain.usecase.event

import com.example.plannit.domain.model.Event
import com.example.plannit.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class GetEventsForDateRangeUseCase(
    private val repository: EventRepository
) {
    operator fun invoke(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Event>> {
        return repository.getEventsBetween(startDate, endDate)
    }
}
