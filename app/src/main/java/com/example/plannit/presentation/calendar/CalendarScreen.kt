package com.example.plannit.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.plannit.domain.model.Event
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    events: List<Event>,
    selectedDate: LocalDate,
    viewMode: CalendarViewMode,
    onDateSelected: (LocalDate) -> Unit,
    onViewModeChanged: (CalendarViewMode) -> Unit,
    onAddEvent: () -> Unit,
    onEventClick: (Event) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendar") },
                actions = {
                    CalendarViewModeSelector(
                        currentMode = viewMode,
                        onModeSelected = onViewModeChanged
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEvent) {
                Icon(Icons.Default.Add, contentDescription = "Add Event")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (viewMode) {
                CalendarViewMode.DAY -> DayView(selectedDate, events, onEventClick)
                CalendarViewMode.WEEK -> WeekView(selectedDate, events, onDateSelected, onEventClick)
                CalendarViewMode.MONTH -> MonthView(selectedDate, events, onDateSelected, onEventClick)
            }
        }
    }
}

@Composable
fun CalendarViewModeSelector(
    currentMode: CalendarViewMode,
    onModeSelected: (CalendarViewMode) -> Unit
) {
    Row {
        CalendarViewMode.values().forEach { mode ->
            TextButton(
                onClick = { onModeSelected(mode) },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (currentMode == mode) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text(mode.name)
            }
        }
    }
}

@Composable
fun DayView(
    date: LocalDate,
    events: List<Event>,
    onEventClick: (Event) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )
        EventList(events, onEventClick)
    }
}

@Composable
fun WeekView(
    selectedDate: LocalDate,
    events: List<Event>,
    onDateSelected: (LocalDate) -> Unit,
    onEventClick: (Event) -> Unit
) {
    val startOfWeek = selectedDate.minusDays(selectedDate.dayOfWeek.value.toLong() - 1)
    
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(7) { index ->
                val date = startOfWeek.plusDays(index.toLong())
                DayCell(
                    date = date,
                    isSelected = date == selectedDate,
                    eventCount = events.count { event ->
                        event.startDateTime.toLocalDate() == date
                    },
                    onClick = { onDateSelected(date) }
                )
            }
        }
        EventList(events, onEventClick)
    }
}

@Composable
fun MonthView(
    selectedDate: LocalDate,
    events: List<Event>,
    onDateSelected: (LocalDate) -> Unit,
    onEventClick: (Event) -> Unit
) {
    var currentMonth by remember { mutableStateOf(selectedDate) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Month navigation header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { 
                currentMonth = currentMonth.minusMonths(1)
            }) {
                Icon(
                    Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous Month",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            
            IconButton(onClick = { 
                currentMonth = currentMonth.plusMonths(1)
            }) {
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next Month",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        // Day of week headers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(48.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        MonthGrid(currentMonth, events, onDateSelected)
        
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        
        EventList(events, onEventClick)
    }
}

@Composable
fun MonthGrid(
    selectedDate: LocalDate,
    events: List<Event>,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDayOfMonth = selectedDate.withDayOfMonth(1)
    val daysInMonth = selectedDate.lengthOfMonth()
    val startDayOfWeek = firstDayOfMonth.dayOfWeek.value
    
    Column(modifier = Modifier.padding(8.dp)) {
        repeat((daysInMonth + startDayOfWeek - 1 + 6) / 7) { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(7) { day ->
                    val dayNumber = week * 7 + day - startDayOfWeek + 2
                    if (dayNumber in 1..daysInMonth) {
                        val date = selectedDate.withDayOfMonth(dayNumber)
                        DayCell(
                            date = date,
                            isSelected = date == selectedDate,
                            eventCount = events.count { it.startDateTime.toLocalDate() == date },
                            onClick = { onDateSelected(date) }
                        )
                    } else {
                        Spacer(modifier = Modifier.size(48.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    eventCount: Int,
    onClick: () -> Unit
) {
    val isToday = date == LocalDate.now()
    
    Box(
        modifier = Modifier
            .size(48.dp)
            .padding(2.dp)
            .clickable(onClick = onClick)
            .background(
                color = when {
                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                    isToday -> MaterialTheme.colorScheme.secondaryContainer
                    else -> Color.Transparent
                },
                shape = MaterialTheme.shapes.small
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                    isToday -> MaterialTheme.colorScheme.onSecondaryContainer
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
            if (eventCount > 0) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.extraSmall
                        )
                )
            }
        }
    }
}

@Composable
fun EventList(
    events: List<Event>,
    onEventClick: (Event) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(events) { event ->
            EventCard(event = event, onClick = { onEventClick(event) })
        }
    }
}

@Composable
fun EventCard(
    event: Event,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .background(Color(event.color))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = event.startDateTime.format(DateTimeFormatter.ofPattern("h:mm a")),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (event.location.isNotEmpty()) {
                    Text(
                        text = event.location,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
