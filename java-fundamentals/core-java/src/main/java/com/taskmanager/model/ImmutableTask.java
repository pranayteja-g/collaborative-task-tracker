package com.taskmanager.model;

import java.util.Objects;

import com.taskmanager.common.Identifiable;

public record ImmutableTask(
        Long id,
        String title,
        String description,
        String status) implements Identifiable {

    // compact constructor with validation
    public ImmutableTask {
        Objects.requireNonNull(id, "ID must not be null");
        Objects.requireNonNull(title, "Title must not be null");
        if (title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be blank");
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public boolean isCompleted() {
        return "DONE".equals(status);
    }
}
