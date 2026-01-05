package com.example.plannit.presentation.navigation

sealed class Screen(val route: String) {
    object Calendar : Screen("calendar")
    object Tasks : Screen("tasks")
    object AddEvent : Screen("add_event")
    object AddTask : Screen("add_task")
}
