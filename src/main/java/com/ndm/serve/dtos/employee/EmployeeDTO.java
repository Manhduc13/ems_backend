package com.ndm.serve.dtos.employee;

import com.ndm.serve.dtos.role.RoleDTO;
import com.ndm.serve.enums.Gender;
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
public class EmployeeDTO {
    long id;
    String firstName;
    String lastName;
    String phone;
    String email;
    String address;
    Gender gender;
    Date dob;
    String username;
    Set<RoleDTO> roles;
    boolean active;
}
