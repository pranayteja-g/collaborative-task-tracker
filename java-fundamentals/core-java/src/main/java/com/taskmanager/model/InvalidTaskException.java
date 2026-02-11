package com.taskmanager.model;

public class InvalidTaskException extends RuntimeException{

    public InvalidTaskException(String message) {
        super(message);
    }
}
