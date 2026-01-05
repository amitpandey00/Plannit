package com.example.plannit.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plannit.domain.model.Event
import com.example.plannit.domain.repository.EventRepository
import com.example.plannit.domain.usecase.event.CreateEventUseCase
import com.example.plannit.domain.usecase.event.GetEventsForDateRangeUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class CalendarViewModel(
    private val repository: EventRepository,
    private val getEventsForDateRange: GetEventsForDateRangeUseCase,
    private val createEvent: CreateEventUseCase
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _viewMode = MutableStateFlow(CalendarViewMode.MONTH)
    val viewMode: StateFlow<CalendarViewMode> = _viewMode.asStateFlow()

    val events: StateFlow<List<Event>> = combine(
        selectedDate,
        viewMode
    ) { date, mode ->
        val (start, end) = getDateRange(date, mode)
        getEventsForDateRange(start, end)
    }.flatMapLatest { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun changeViewMode(mode: CalendarViewMode) {
        _viewMode.value = mode
    }

    fun addEvent(event: Event) {
        viewModelScope.launch {
            createEvent(event)
        }
    }

    fun deleteEvent(eventId: Long) {
        viewModelScope.launch {
            repository.deleteEventById(eventId)
        }
    }

    private fun getDateRange(date: LocalDate, mode: CalendarViewMode): Pair<LocalDateTime, LocalDateTime> {
        return when (mode) {
            CalendarViewMode.DAY -> {
                date.atStartOfDay() to date.plusDays(1).atStartOfDay()
            }
            CalendarViewMode.WEEK -> {
                val startOfWeek = date.minusDays(date.dayOfWeek.value.toLong() - 1)
                startOfWeek.atStartOfDay() to startOfWeek.plusDays(7).atStartOfDay()
            }
            CalendarViewMode.MONTH -> {
                val startOfMonth = date.withDayOfMonth(1)
                startOfMonth.atStartOfDay() to startOfMonth.plusMonths(1).atStartOfDay()
            }
        }
    }
}

enum class CalendarViewMode {
    DAY, WEEK, MONTH
}
