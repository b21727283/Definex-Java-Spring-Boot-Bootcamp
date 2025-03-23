package com.mgumussoy.advancedtaskmanagement.dtos;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String username, @NotBlank String password) {
}
