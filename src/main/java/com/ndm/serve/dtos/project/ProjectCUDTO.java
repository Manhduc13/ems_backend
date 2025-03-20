package com.ndm.serve.dtos.project;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectCUDTO {
    @NotBlank(message = "Project name is required")
    String name;

    String description;

    @FutureOrPresent
    LocalDate startDate;

    BigDecimal budget;

    long leader_id;
}
