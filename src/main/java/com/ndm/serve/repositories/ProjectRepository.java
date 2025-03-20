package com.ndm.serve.repositories;

import com.ndm.serve.enums.ProjectStatus;
import com.ndm.serve.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {
    Optional<Project> findByName(String name);

    List<Project> findAllByStartDateAfter(Date date);

    List<Project> findAllByStartDateBefore(Date date);

    List<Project> findByStatus(ProjectStatus status);
}
