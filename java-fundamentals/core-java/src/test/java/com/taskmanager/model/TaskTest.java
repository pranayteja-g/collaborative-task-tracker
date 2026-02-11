package com.taskmanager.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    void testTaskEqualityBasedOnId(){
        // Arrange
        Task task1 = new Task(1L, "Buy groceries", "Milk, eggs, bread", "TODO");
        Task task2 = new Task(1L, "Buy groceries", "Different desc", "DONE");
        
        Task task3 = new Task(2L, "Buy groceries", "Milk, eggs, bread", "TODO");

        // Act & Assert
        assertEquals(task1, task2); // should be equal because same ID
        assertNotEquals(task1, task3); // different ID -> not equal
        assertEquals(task1.hashCode(), task2.hashCode()); // hashcode must match for same ID 
    }
}
 
