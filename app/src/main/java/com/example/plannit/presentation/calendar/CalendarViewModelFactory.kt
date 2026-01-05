package com.example.plannit.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plannit.domain.repository.EventRepository
import com.example.plannit.domain.usecase.event.CreateEventUseCase
import com.example.plannit.domain.usecase.event.GetEventsForDateRangeUseCase

class CalendarViewModelFactory(
    private val repository: EventRepository,
    private val getEventsForDateRange: GetEventsForDateRangeUseCase,
    private val createEvent: CreateEventUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(repository, getEventsForDateRange, createEvent) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
