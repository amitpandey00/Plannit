package com.example.plannit.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plannit.domain.repository.TaskRepository
import com.example.plannit.domain.usecase.task.GetActiveTasksUseCase
import com.example.plannit.domain.usecase.task.ToggleTaskCompletionUseCase

class TaskViewModelFactory(
    private val repository: TaskRepository,
    private val getActiveTasks: GetActiveTasksUseCase,
    private val toggleTaskCompletion: ToggleTaskCompletionUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository, getActiveTasks, toggleTaskCompletion) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
