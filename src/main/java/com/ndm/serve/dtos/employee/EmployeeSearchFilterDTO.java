package com.ndm.serve.dtos.employee;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeSearchFilterDTO {
    String username;
    String phone;
    String email;
    String address;
}
