package com.taskmanager.model;

import java.util.*;

import lombok.*;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Task {

    private final Long id;
    private String title;
    private String description;
    private String status; // TODO, IN_PROGRESS, DONE

    // Static factory method with validation -> factory pattern
    public static Task of(Long id, String title, String description, String status) {
        if (id == null) {
            throw new InvalidTaskException("ID cannot be null");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidTaskException("Title cannot be null or blank");
        }
        // You can add more rules here (e.g. status in allowed values)

        return new Task(id, title.trim(), description, status);
    }

    public boolean isCompleted() {
        return "DONE".equals(status);
    }
}
