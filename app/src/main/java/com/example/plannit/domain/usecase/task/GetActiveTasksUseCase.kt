package com.example.plannit.domain.usecase.task

import com.example.plannit.domain.model.Task
import com.example.plannit.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetActiveTasksUseCase(
    private val repository: TaskRepository
) {
    operator fun invoke(): Flow<List<Task>> {
        return repository.getActiveTasks()
    }
}
