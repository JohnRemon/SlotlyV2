package com.example.SlotlyV2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequest {

    @Email
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String displayName;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password Size must be greater than 8 characters")
    private String password;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Timezone is required")
    private String timeZone;
}
