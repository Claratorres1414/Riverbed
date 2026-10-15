package com.project.riverbed.test.validation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ValidationRequest(
        @NotBlank String name,
        @Email @NotBlank String email
) {
}
