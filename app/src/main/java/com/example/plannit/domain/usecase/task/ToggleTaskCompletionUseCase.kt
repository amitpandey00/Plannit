package com.example.plannit.domain.usecase.task

import com.example.plannit.domain.repository.TaskRepository
import java.time.LocalDateTime

class ToggleTaskCompletionUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: Long) {
        val task = repository.getTaskById(taskId) ?: return
        val updatedTask = task.copy(
            isCompleted = !task.isCompleted,
            completedAt = if (!task.isCompleted) LocalDateTime.now() else null,
            updatedAt = LocalDateTime.now()
        )
        repository.updateTask(updatedTask)
    }
}
