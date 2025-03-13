package com.ndm.serve.dtos.resetPassword;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewPasswordDTO {
    String email;
    String newPassword;
    String confirmPassword;
}
