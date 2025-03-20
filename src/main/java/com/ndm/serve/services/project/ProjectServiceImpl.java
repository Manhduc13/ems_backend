package com.ndm.serve.services.project;

import com.ndm.serve.dtos.employee.EmployeeDTO;
import com.ndm.serve.dtos.project.ProjectCUDTO;
import com.ndm.serve.dtos.project.ProjectDTO;
import com.ndm.serve.enums.EmployeeRole;
import com.ndm.serve.enums.ProjectStatus;
import com.ndm.serve.exceptions.ResourceNotFoundException;
import com.ndm.serve.mappers.EmployeeMapper;
import com.ndm.serve.mappers.ProjectMapper;
import com.ndm.serve.models.Employee;
import com.ndm.serve.models.Project;
import com.ndm.serve.models.Role;
import com.ndm.serve.repositories.EmployeeRepository;
import com.ndm.serve.repositories.ProjectRepository;
import com.ndm.serve.repositories.RoleRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProjectServiceImpl implements ProjectService {

    ProjectRepository projectRepository;
    EmployeeRepository employeeRepository;
    ProjectMapper projectMapper;
    RoleRepository roleRepository;
    EmployeeMapper employeeMapper;

    private final Collection<EmployeeMapper> employeeMappers;

    @Override
    public ProjectDTO getById(long id) throws ResourceNotFoundException {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found for this id: " + id));
        return projectMapper.toProjectDTO(project);
    }

    @Override
    public List<ProjectDTO> getAll() {
        return projectRepository.findAll().stream().map(projectMapper::toProjectDTO).collect(Collectors.toList());
    }

    @Override
    public Page<ProjectDTO> search(String keyword, Pageable pageable) {
        Specification<Project> spec = (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return cb.conjunction();
            }

            String searchPattern = "%" + keyword.trim().toLowerCase() + "%";

            Predicate namePredicate = cb.like(cb.lower(root.get("name")), searchPattern);
            
            return cb.or(namePredicate);
        };

        Page<Project> projects = projectRepository.findAll(spec, pageable);

        return projects.map(projectMapper::toProjectDTO);
    }

    @Override
    public ProjectDTO create(ProjectCUDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Project create request cannot be null");
        }

        Project existedName = projectRepository.findByName(request.getName()).orElse(null);
        if (existedName != null) {
            throw new IllegalArgumentException("Project already exists");
        }

        Project newProject = projectMapper.toProject(request);
        newProject.setStatus(ProjectStatus.PLANNED);
        newProject.setInsertedAt(ZonedDateTime.now());

        return projectMapper.toProjectDTO(projectRepository.save(newProject));
    }

    @Override
    public ProjectDTO update(long id, ProjectCUDTO request) throws ResourceNotFoundException {
        if (request == null) {
            throw new IllegalArgumentException("Project update request cannot be null");
        }
        Project existedProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found for this id: " + id));

        Project existedName = projectRepository.findByName(request.getName()).orElse(null);
        if (existedName != null && existedProject.getId() != existedName.getId()) {
            throw new IllegalArgumentException("Project name already exists");
        }

        existedProject.setName(request.getName());
        existedProject.setDescription(request.getDescription());
        existedProject.setStartDate(request.getStartDate());
        existedProject.setBudget(request.getBudget());
        existedProject.setLeader_id(existedProject.getLeader_id());
        existedProject.setUpdatedAt(ZonedDateTime.now());

        return projectMapper.toProjectDTO(projectRepository.save(existedProject));
    }

    @Override
    public boolean delete(long id) throws ResourceNotFoundException {
        Project existedProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found for this id: " + id));

        projectRepository.delete(existedProject);
        return !projectRepository.existsById(id);
    }

    @Override
    public ProjectDTO updateStatus(long id, ProjectStatus status) throws ResourceNotFoundException {
        Project existedProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found for this id: " + id));
        existedProject.setStatus(status);
        return projectMapper.toProjectDTO(projectRepository.save(existedProject));
    }

    @Override
    public List<ProjectDTO> findAllByStartDateAfter(Date date) {
        return projectRepository.findAllByStartDateAfter(date).stream().map(projectMapper::toProjectDTO).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> findAllByStartDateBefore(Date date) {
        return projectRepository.findAllByStartDateBefore(date).stream().map(projectMapper::toProjectDTO).collect(Collectors.toList());
    }

    @Override
    public ProjectDTO addMemberToProject(long projectId, long userId) throws ResourceNotFoundException {
        Project existedProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found for this id: " + projectId));

        Employee existedEmployee = employeeRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id: " + userId));

        if (!existedProject.getMembers().contains(existedEmployee)) {
            existedProject.getMembers().add(existedEmployee);
            existedProject = projectRepository.save(existedProject);
        }

        return projectMapper.toProjectDTO(projectRepository.save(existedProject));
    }

    @Override
    public ProjectDTO removeMemberFromProject(long projectId, long userId) throws ResourceNotFoundException {
        Project existedProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found for this id: " + projectId));

        Employee existedEmployee = employeeRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id: " + userId));

        if (existedProject.getMembers().contains(existedEmployee)) {
            existedProject.getMembers().remove(existedEmployee);
            existedProject = projectRepository.save(existedProject);
        }

        return projectMapper.toProjectDTO(projectRepository.save(existedProject));
    }

    @Override
    public ProjectDTO chooseLeader(long projectId, long userId) throws ResourceNotFoundException {
        Project existedProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found for this id: " + projectId));

        Employee existedEmployee = employeeRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id: " + userId));

        Set<Role> employeeRoles = existedEmployee.getRoles();
        Role leaderRole = roleRepository.findByName(EmployeeRole.LEADER);

        if (!employeeRoles.contains(leaderRole)) {
            existedEmployee.getRoles().add(leaderRole);
            employeeRepository.save(existedEmployee);
        }

        existedProject.setLeader_id(existedEmployee.getId());

        return projectMapper.toProjectDTO(projectRepository.save(existedProject));
    }

    @Override
    public List<EmployeeDTO> findMembersOfProject(long projectId) throws ResourceNotFoundException {
        Project existedProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found for this id: " + projectId));
        return existedProject.getMembers().stream().map(employeeMapper::toEmployeeDTO).collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> findMembersNotInProject(long projectId) throws ResourceNotFoundException {
        Project existedProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found for this id: " + projectId));
        List<Employee> allEmployees = employeeRepository.findAll();
        Set<Employee> members = existedProject.getMembers();

        allEmployees.removeIf(members::contains);

        return allEmployees.stream().map(employeeMapper::toEmployeeDTO).collect(Collectors.toList());
    }
}
