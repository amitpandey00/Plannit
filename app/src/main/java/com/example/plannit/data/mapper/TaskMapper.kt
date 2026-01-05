package com.example.plannit.data.mapper

import com.example.plannit.data.local.entity.TaskEntity
import com.example.plannit.domain.model.Task

fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        dueDateTime = dueDateTime,
        priority = priority,
        isCompleted = isCompleted,
        completedAt = completedAt,
        notificationMinutesBefore = notificationMinutesBefore,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        dueDateTime = dueDateTime,
        priority = priority,
        isCompleted = isCompleted,
        completedAt = completedAt,
        notificationMinutesBefore = notificationMinutesBefore,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
