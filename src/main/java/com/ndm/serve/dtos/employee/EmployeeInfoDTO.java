package com.ndm.serve.dtos.employee;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeInfoDTO {
    long id;
    String email;
    String username;
    Set<String> roles;
    boolean isActive;
    String avatar;
}
