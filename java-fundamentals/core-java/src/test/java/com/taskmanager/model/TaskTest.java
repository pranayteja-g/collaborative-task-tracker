package com.taskmanager.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.print.attribute.HashAttributeSet;

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
}
