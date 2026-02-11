package com.taskmanager.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    void testTaskEqualityBasedOnId() {
        // Arrange
        Task task1 = new Task(1L, "Buy groceries", "Milk, eggs, bread", "TODO");
        Task task2 = new Task(1L, "Buy groceries", "Different desc", "DONE");

        Task task3 = new Task(2L, "Buy groceries", "Milk, eggs, bread", "TODO");

        // Act & Assert
        assertEquals(task1, task2); // should be equal because same ID
        assertNotEquals(task1, task3); // different ID -> not equal
        assertEquals(task1.hashCode(), task2.hashCode()); // hashcode must match for same ID
    }

    @Test
    void testTaskList_addAndContains() {
        Task t1 = new Task(1L, "Write report", "Q4 summary", "TODO");
        Task t2 = new Task(2L, "Review code", "PR #123", "IN_PROGRESS");
        Task t3 = new Task(1L, "Write report", "Updated", "DONE"); // same ID as t1

        List<Task> tasks = new ArrayList<>();
        tasks.add(t1);
        tasks.add(t2);
        tasks.add(t3); // should add duplicate (list allows it)

        assertEquals(3, tasks.size(), "List allows duplicates even if equals() is true");
        assertTrue(tasks.contains(t1));
        assertTrue(tasks.contains(t3), "contains uses equals() → same ID is considered present");
    }

    @Test
    void testTaskSet_noDuplicatesById() {
        Task t1 = new Task(1L, "Task A", "Desc A", "TODO");
        Task t2 = new Task(2L, "Task B", "Desc B", "DONE");
        Task t3 = new Task(1L, "Task A updated", "New desc", "IN_PROGRESS"); // same ID

        Set<Task> taskSet = new HashSet<>();

        boolean added1 = taskSet.add(t1);
        boolean added2 = taskSet.add(t2);
        boolean added3 = taskSet.add(t3);

        assertTrue(added1);
        assertTrue(added2);
        assertFalse(added3, "add() returns false for duplicate by ID");

        assertEquals(2, taskSet.size(), "set enforces uniqueness via equals() + hashCode() ");
        assertTrue(taskSet.contains(t3), "contains() finds qual element");
    }

    @Test
    void testTaskMap_byId() {
        Task t1 = new Task(1L, "Meeting", "Team sync", "TODO");
        Task t2 = new Task(2L, "Email client", "Follow up", "DONE");

        Map<Long, Task> taskMap = new HashMap<>();

        taskMap.put(t1.getId(), t1);
        taskMap.put(t2.getId(), t2);

        assertEquals(2, taskMap.size());
        assertSame(t1, taskMap.get(1L)); // same reference
        assertEquals("Meeting", taskMap.get(1L).getTitle());
        // overwrite with same key
        Task t3 = new Task(1L, "Updated meeting", "New agenda", "DONE");
        taskMap.put(t3.getId(), t3);

        assertEquals(2, taskMap.size(), "map replaces on the same key");
        assertEquals("Updated meeting", taskMap.get(1L).getTitle());
    }

    @Test
    void testFilterTodoTasksAndSortTitles() {
        Task t1 = Task.of(1L, "Clean kitchen", "...", "TODO");
        Task t2 = Task.of(2L, "Buy groceries", "...", "DONE");
        Task t3 = Task.of(3L, "Read chapter 5", "...", "TODO");
        Task t4 = Task.of(4L, "Call mom", "...", "IN_PROGRESS");

        List<Task> tasks = List.of(t1, t2, t3, t4);
        List<String> todoTitles = tasks.stream()
                .filter(task -> "TODO".equals(task.getStatus()))
                .map(Task::getTitle)
                .sorted()
                .collect(Collectors.toList());
        
        assertEquals(List.of("Clean kitchen", "Read chapter 5"), todoTitles);
    }
}
