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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class TaskTest {

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

    @Test
    void testConcurrentAddToList_raceConditionPossible() throws InterruptedException, ExecutionException {
        // List<Task> tasks = new ArrayList<>();

        // List<Task> tasks = new CopyOnWriteArrayList<>();

        List<Task> tasks = Collections.synchronizedList(new ArrayList<>());

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable addTask = () -> {
            for (int i = 0; i < 1000; i++) {
                tasks.add(Task.of((long) i, "Task " + i, "Desc", "TODO"));
            }
        };

        // Thread t1 = new Thread(addTask);
        // Thread t2 = new Thread(addTask);
        // t1.start();
        // t2.start();

        // t1.join();
        // t2.join();

        // submit and get futures
        Future<?> future1 = executor.submit(addTask);
        Future<?> future2 = executor.submit(addTask);

        // wait for both to finish
        future1.get();
        future2.get();

        executor.shutdown();

        // wait max 5 seconds for graceful shutdown.
        boolean terminated = executor.awaitTermination(5, TimeUnit.SECONDS);
        assertTrue(terminated, "Executor should terminate cleanly");
        // expected: 2000 tasks, but often less due to race condition
        System.out.println("Number of tasks: " + tasks.size());
        assertTrue(tasks.size() <= 2000, "size should not exceed expented");

    }

    // helper method to create sample data
    private List<Task> createSampleTasks() {
        return List.of(
                Task.of(1L, "Write report", "Q4", "TODO"),
                Task.of(2L, "Review PR", "Bug fix", "IN_PROGRESS"),
                Task.of(3L, "Call client", "Follow up", "TODO"),
                Task.of(4L, "Deploy app", "Production", "DONE"),
                Task.of(5L, "Fix tests", "Coverage", "IN_PROGRESS"),
                Task.of(6L, "Team meeting", "Planning", "TODO"),
                Task.of(7L, "Update docs", "API spec", "DONE"));
    }

    @Test
    void testGroupTasksByStatus() {
        List<Task> tasks = createSampleTasks();

        Map<String, List<Task>> groupedByStatus = tasks.stream().collect(Collectors.groupingBy(Task::getStatus));

        assertEquals(3, groupedByStatus.size(), "Should have 3 distinct statuses");
        assertEquals(3, groupedByStatus.get("TODO").size());
        assertEquals(2, groupedByStatus.get("IN_PROGRESS").size());
        assertEquals(2, groupedByStatus.get("DONE").size());

    }

    @Test
    void testCountTasksPerStatus() {
        List<Task> tasks = createSampleTasks();

        Map<String, Long> countByStatus = tasks.stream()
                .collect(Collectors.groupingBy(
                        Task::getStatus,
                        Collectors.counting()));

        assertEquals(3L, countByStatus.get("TODO"));
        assertEquals(2L, countByStatus.get("IN_PROGRESS"));
        assertEquals(2L, countByStatus.get("DONE"));
    }

    @Test
    void testTitlesGroupedByStatus() {
        List<Task> tasks = createSampleTasks();

        Map<String, List<String>> titlesByStatus = tasks.stream()
                .collect(Collectors.groupingBy(
                        Task::getStatus,
                        Collectors.mapping(Task::getTitle, Collectors.toList())));

        assertEquals(List.of("Write report", "Call client", "Team meeting"),
                titlesByStatus.get("TODO"));
    }

    @Test
    void testPartitionCompletedTasks() {
        List<Task> tasks = createSampleTasks();

        Map<Boolean, List<Task>> partitioned = tasks.stream()
                .collect(Collectors.partitioningBy(Task::isCompleted));

        assertEquals(2, partitioned.get(true).size(), "Completed (DONE)");
        assertEquals(5, partitioned.get(false).size(), "Not Completed");
    }
}
