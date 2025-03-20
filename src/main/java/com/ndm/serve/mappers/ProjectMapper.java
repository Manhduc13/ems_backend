package com.ndm.serve.mappers;

import com.ndm.serve.dtos.project.ProjectCUDTO;
import com.ndm.serve.dtos.project.ProjectDTO;
import com.ndm.serve.models.Project;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {EmployeeMapper.class})
public interface ProjectMapper {

    ProjectDTO toProjectDTO(Project project);

    Project toProject(ProjectCUDTO projectCUDTO);
}
