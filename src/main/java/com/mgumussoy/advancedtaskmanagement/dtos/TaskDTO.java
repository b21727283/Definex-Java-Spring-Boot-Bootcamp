package com.mgumussoy.advancedtaskmanagement.dtos;

import com.mgumussoy.advancedtaskmanagement.enums.TaskPriority;
import com.mgumussoy.advancedtaskmanagement.enums.TaskState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDTO {
    private Long id;

    @NotBlank(message = "Title cannot be blank")
    private String userStory;

    @NotBlank(message = "Description cannot be blank")
    private String acceptanceCriteria;

    @NotNull(message = "State cannot be null")
    private TaskState state;

    @NotNull(message = "Priority cannot be null")
    private TaskPriority priority;

    @NotNull(message = "Assignee id cannot be null")
    private Long assigneeId;

    @NotNull(message = "Project id cannot be null")
    private Long projectId;

    private String reasonForStateChange;
}
