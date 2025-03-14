package com.ndm.serve.dtos.resetPassword;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewPasswordDTO {
    @NotBlank(message = "Email is required")
    String email;
    @NotBlank(message = "New password is required")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()\\-=+<>?])[A-Za-z\\d!@#$%^&*()\\-=+<>?]{8,12}$", message = "Incorrect password format")
    String newPassword;
    @NotBlank(message = "Confirm password is required")
    String confirmPassword;
}
