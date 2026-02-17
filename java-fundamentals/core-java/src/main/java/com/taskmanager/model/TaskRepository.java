package com.taskmanager.model;

import java.util.*;

public class TaskRepository<T extends Task> {
    private final Map<Long, T> tasks = new HashMap<>();

    public void save(T task) {
        tasks.put(task.getId(), task);
    }

    public Optional<T> findById(Long id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public List<T> findAll() {
        return new ArrayList<>(tasks.values());
    }

    public int count() {
        return tasks.size();
    }
}
