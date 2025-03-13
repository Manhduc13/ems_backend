package com.ndm.serve.dtos.role;

import com.ndm.serve.enums.EmployeeRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleCUDTO {
    EmployeeRole name;
}
