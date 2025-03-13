package com.ndm.serve.dtos.resetPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResetPasswordRequestDTO {
    @Email
    @NotBlank(message = "Email is required")
    @Pattern(regexp = "^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,4}$", message = "Invalid email format")
    String email;
}
