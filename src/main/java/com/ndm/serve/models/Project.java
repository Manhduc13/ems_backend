package com.ndm.serve.models;

import com.ndm.serve.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.TimeZoneColumn;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "projects")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false, unique = true)
    String name;

    String description;

    Date startDate;

    ProjectStatus status;

    BigDecimal budget;

    @TimeZoneColumn
    @Column(nullable = false, columnDefinition = "DATETIMEOFFSET")
    ZonedDateTime insertedAt;

    @TimeZoneColumn
    @Column(columnDefinition = "DATETIMEOFFSET")
    ZonedDateTime updatedAt;

    @ManyToMany
    @JoinTable(
            name = "project_employee",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id"))
    Set<Employee> employees;
}
