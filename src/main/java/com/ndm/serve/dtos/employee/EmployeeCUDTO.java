package com.ndm.serve.dtos.employee;

import com.ndm.serve.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeCUDTO {
    @NotBlank(message = "First name is mandatory")
    String firstName;

    @NotBlank(message = "Last name is mandatory")
    String lastName;

    @NotBlank(message = "Email is mandatory")
    @Pattern(regexp = "^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,4}$", message = "Invalid email format")
    String email;

    @NotBlank(message = "Phone is mandatory")
    @Pattern(regexp = "^(?:\\+84|0)(3[2-9]|5[2689]|7[06789]|8[1-9]|9[0-9])\\d{7}$", message = "Invalid phone number format")
    String phone;

    @NotNull(message = "Gender is mandatory")
    Gender gender;

    String address;

    @PastOrPresent
    Date dob;

    @NotNull(message = "Role is mandatory")
    Set<Long> roleIds;
}
