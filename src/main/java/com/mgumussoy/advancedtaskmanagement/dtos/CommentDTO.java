package com.mgumussoy.advancedtaskmanagement.dtos;

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
public class CommentDTO {
    private Long id;

    @NotBlank(message = "Text cannot be blank")
    private String text;

    @NotNull(message = "Author id cannot be null")
    private Long authorId;

    @NotNull(message = "Task id cannot be null")
    private Long taskId;
}
