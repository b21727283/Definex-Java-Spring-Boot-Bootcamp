package com.mgumussoy.advancedtaskmanagement.dtos;

import com.mgumussoy.advancedtaskmanagement.enums.ProjectStatus;
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
public class ProjectDTO {
    private Long id;

    @NotNull(message = "Department id cannot be null")
    private Long department_id;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Status cannot be null")
    private ProjectStatus status;

}
