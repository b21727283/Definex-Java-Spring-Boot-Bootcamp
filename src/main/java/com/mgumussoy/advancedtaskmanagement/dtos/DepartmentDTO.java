package com.mgumussoy.advancedtaskmanagement.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepartmentDTO {
    private Long id;

    @NotBlank(message = "Department name cannot be blank")
    private String departmentName;
}
