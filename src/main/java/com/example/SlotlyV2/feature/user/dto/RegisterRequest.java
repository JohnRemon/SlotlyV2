package com.example.SlotlyV2.feature.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class RegisterRequest {
    @Email(message = "Please provide a valid email")
    @NotBlank(message = "Email is required")
    private final String email;

    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private final String displayName;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password Size must be greater than 8 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&].*$", message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character (@$!%*?&)")
    private final String password;

    @NotBlank(message = "First name is required")
    private final String firstName;

    @NotBlank(message = "Last name is required")
    private final String lastName;

    @NotBlank(message = "Timezone is required")
    private final String timeZone;
}
