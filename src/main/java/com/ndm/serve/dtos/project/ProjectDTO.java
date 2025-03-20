package com.ndm.serve.dtos.project;

import com.ndm.serve.dtos.employee.EmployeeDTO;
import com.ndm.serve.enums.ProjectStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDTO {
    long id;

    String name;

    String description;

    LocalDate startDate;

    ProjectStatus status;

    BigDecimal budget;

    Set<EmployeeDTO> members;

    long leader_id;
}
