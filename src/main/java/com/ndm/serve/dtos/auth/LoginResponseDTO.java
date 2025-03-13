package com.ndm.serve.dtos.auth;

import com.ndm.serve.dtos.employee.EmployeeInfoDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginResponseDTO {
    String accessToken;

    EmployeeInfoDTO employeeInfoDTO;
}
