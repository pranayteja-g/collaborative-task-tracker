package com.taskmanager.model;
import lombok.*;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Task {

    private final Long id;
    private String title;
    private String description;
    private String status; // TODO, IN_PROGRESS, DONE

    public boolean isCompleted(){
        return "DONE".equals(status);
    }

}
