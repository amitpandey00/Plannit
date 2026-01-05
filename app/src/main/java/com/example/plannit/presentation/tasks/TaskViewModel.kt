package com.example.plannit.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plannit.domain.model.Task
import com.example.plannit.domain.repository.TaskRepository
import com.example.plannit.domain.usecase.task.GetActiveTasksUseCase
import com.example.plannit.domain.usecase.task.ToggleTaskCompletionUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class TaskViewModel(
    private val repository: TaskRepository,
    private val getActiveTasks: GetActiveTasksUseCase,
    private val toggleTaskCompletion: ToggleTaskCompletionUseCase
) : ViewModel() {

    private val _showCompleted = MutableStateFlow(false)
    val showCompleted: StateFlow<Boolean> = _showCompleted.asStateFlow()

    val tasks: StateFlow<List<Task>> = showCompleted.flatMapLatest { showCompleted ->
        if (showCompleted) {
            repository.getAllTasks()
        } else {
            getActiveTasks()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleShowCompleted() {
        _showCompleted.value = !_showCompleted.value
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            val newTask = task.copy(
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            repository.insertTask(newTask)
        }
    }

    fun toggleTaskCompletion(taskId: Long) {
        viewModelScope.launch {
            toggleTaskCompletion.invoke(taskId)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTaskById(taskId)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(updatedAt = LocalDateTime.now())
            repository.updateTask(updatedTask)
        }
    }
}
