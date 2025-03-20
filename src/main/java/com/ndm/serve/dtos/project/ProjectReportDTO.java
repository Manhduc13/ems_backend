package com.ndm.serve.dtos.project;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectReportDTO {
    String name;
    String startDate;
    String status;
    String budget;
    String leader;
    String size;
}
