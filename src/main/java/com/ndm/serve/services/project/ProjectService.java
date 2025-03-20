package com.ndm.serve.services.project;

import com.ndm.serve.dtos.employee.EmployeeDTO;
import com.ndm.serve.dtos.project.ProjectCUDTO;
import com.ndm.serve.dtos.project.ProjectDTO;
import com.ndm.serve.enums.ProjectStatus;
import com.ndm.serve.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface ProjectService {
    ProjectDTO getById(long id) throws ResourceNotFoundException;

    List<ProjectDTO> getAll();

    Page<ProjectDTO> search(String keyword, Pageable pageable);

    ProjectDTO create(ProjectCUDTO request);

    ProjectDTO update(long id, ProjectCUDTO request) throws ResourceNotFoundException;

    boolean delete(long id) throws ResourceNotFoundException;

    ProjectDTO updateStatus(long id, ProjectStatus status) throws ResourceNotFoundException;

    List<ProjectDTO> findAllByStartDateAfter(Date date);

    List<ProjectDTO> findAllByStartDateBefore(Date date);

    ProjectDTO addMemberToProject(long projectId, long userId) throws ResourceNotFoundException;

    ProjectDTO removeMemberFromProject(long projectId, long userId) throws ResourceNotFoundException;

    ProjectDTO chooseLeader(long projectId, long userId) throws ResourceNotFoundException;

    List<EmployeeDTO> findMembersOfProject(long projectId) throws ResourceNotFoundException;

    List<EmployeeDTO> findMembersNotInProject(long projectId) throws ResourceNotFoundException;
}
