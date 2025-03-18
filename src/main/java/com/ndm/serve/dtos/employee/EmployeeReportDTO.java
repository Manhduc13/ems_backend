package com.ndm.serve.dtos.employee;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeReportDTO {
    String firstName;
    String lastName;
    String phone;
    String email;
    Date dob;
    String username;
    String roles;
}
