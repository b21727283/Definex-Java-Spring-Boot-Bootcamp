package com.mgumussoy.advancedtaskmanagement.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    @NotNull(message = "Id cannot be null")
    private Long id;

    @NotBlank(message = "Username cannot be blank")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @ToString.Exclude
    private String password;

    @Builder.Default
    private List<String> authorities = new ArrayList<>();

    @NotNull(message = "Enabled cannot be null")
    private Long departmentId;
}
