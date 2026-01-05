package com.example.plannit

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.plannit.data.notification.NotificationScheduler
import com.example.plannit.data.repository.EventRepositoryImpl
import com.example.plannit.data.repository.TaskRepositoryImpl
import com.example.plannit.domain.usecase.event.CreateEventUseCase
import com.example.plannit.domain.usecase.event.GetEventsForDateRangeUseCase
import com.example.plannit.domain.usecase.task.GetActiveTasksUseCase
import com.example.plannit.domain.usecase.task.ToggleTaskCompletionUseCase
import com.example.plannit.presentation.addevent.AddEventScreen
import com.example.plannit.presentation.addtask.AddTaskScreen
import com.example.plannit.presentation.calendar.CalendarScreen
import com.example.plannit.presentation.calendar.CalendarViewModel
import com.example.plannit.presentation.navigation.Screen
import com.example.plannit.presentation.tasks.TaskScreen
import com.example.plannit.presentation.tasks.TaskViewModel
import com.example.plannit.presentation.tasks.TaskViewModelFactory
import com.example.plannit.presentation.calendar.CalendarViewModelFactory
import com.example.plannit.ui.theme.PlannitTheme

class MainActivity : ComponentActivity() {
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        
        enableEdgeToEdge()
        setContent {
            PlannitTheme {
                PlannitApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannitApp() {
    val navController = rememberNavController()
    val database = (navController.context as ComponentActivity)
        .application.let { it as PlannitApplication }.database
    
    val eventRepository = remember { EventRepositoryImpl(database.eventDao()) }
    val taskRepository = remember { TaskRepositoryImpl(database.taskDao()) }
    val notificationScheduler = remember { NotificationScheduler(navController.context) }
    
    val calendarViewModel: CalendarViewModel = viewModel(
        factory = CalendarViewModelFactory(
            eventRepository,
            GetEventsForDateRangeUseCase(eventRepository),
            CreateEventUseCase(eventRepository)
        )
    )
    
    val taskViewModel: TaskViewModel = viewModel(
        factory = TaskViewModelFactory(
            taskRepository,
            GetActiveTasksUseCase(taskRepository),
            ToggleTaskCompletionUseCase(taskRepository)
        )
    )
    
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Calendar") },
                    label = { Text("Calendar") },
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        navController.navigate(Screen.Calendar.route) {
                            popUpTo(Screen.Calendar.route) { inclusive = true }
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Tasks") },
                    label = { Text("Tasks") },
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        navController.navigate(Screen.Tasks.route) {
                            popUpTo(Screen.Calendar.route)
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Calendar.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Calendar.route) {
                val events by calendarViewModel.events.collectAsState()
                val selectedDate by calendarViewModel.selectedDate.collectAsState()
                val viewMode by calendarViewModel.viewMode.collectAsState()
                
                CalendarScreen(
                    events = events,
                    selectedDate = selectedDate,
                    viewMode = viewMode,
                    onDateSelected = calendarViewModel::selectDate,
                    onViewModeChanged = calendarViewModel::changeViewMode,
                    onAddEvent = { navController.navigate(Screen.AddEvent.route) },
                    onEventClick = { }
                )
            }
            
            composable(Screen.Tasks.route) {
                val tasks by taskViewModel.tasks.collectAsState()
                val showCompleted by taskViewModel.showCompleted.collectAsState()
                
                TaskScreen(
                    tasks = tasks,
                    showCompleted = showCompleted,
                    onToggleShowCompleted = taskViewModel::toggleShowCompleted,
                    onAddTask = { navController.navigate(Screen.AddTask.route) },
                    onTaskClick = { },
                    onToggleCompletion = taskViewModel::toggleTaskCompletion,
                    onDeleteTask = taskViewModel::deleteTask
                )
            }
            
            composable(Screen.AddEvent.route) {
                AddEventScreen(
                    onSave = { event ->
                        calendarViewModel.addEvent(event)
                        notificationScheduler.scheduleEventNotification(event)
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.AddTask.route) {
                AddTaskScreen(
                    onSave = { task ->
                        taskViewModel.addTask(task)
                        task.dueDateTime?.let {
                            notificationScheduler.scheduleTaskNotification(task)
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}