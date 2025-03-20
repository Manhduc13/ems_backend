package com.ndm.serve.controllers;

import com.ndm.serve.dtos.employee.EmployeeDTO;
import com.ndm.serve.dtos.project.ProjectCUDTO;
import com.ndm.serve.dtos.project.ProjectDTO;
import com.ndm.serve.enums.ProjectStatus;
import com.ndm.serve.exceptions.ResourceNotFoundException;
import com.ndm.serve.services.project.ProjectService;
import com.ndm.serve.utils.CustomPagedResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/projects")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProjectController {
    ProjectService projectService;
    PagedResourcesAssembler<ProjectDTO> pagedResourcesAssembler;

    @GetMapping("/getById/{id}")
    public ResponseEntity<ProjectDTO> getById(@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(projectService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAll() {
        return ResponseEntity.ok(projectService.getAll());
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchWithFilter(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "5") int size
    ) {
        Pageable pageable = null;
        // Determine sorting order
        if (order.equalsIgnoreCase("asc")) {
            pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        } else {
            pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        }

        Page<ProjectDTO> projectDTOS = projectService.search(keyword, pageable);

        // Convert to paged model
        var pagedModel = pagedResourcesAssembler.toModel(projectDTOS);
        Collection<EntityModel<ProjectDTO>> data = pagedModel.getContent();
        var links = pagedModel.getLinks();

        var response = new CustomPagedResponse<EntityModel<ProjectDTO>>(data, pagedModel.getMetadata(), links);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid ProjectCUDTO request,
                                    BindingResult bindingResult) throws ResourceNotFoundException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        return ResponseEntity.ok(projectService.create(request));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody @Valid ProjectCUDTO request,
                                    BindingResult bindingResult) throws ResourceNotFoundException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        return ResponseEntity.ok(projectService.update(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws ResourceNotFoundException {
        Map<String, Boolean> response = new HashMap<>();
        if (projectService.delete(id)) {
            response.put("deleted", Boolean.TRUE);
        } else {
            response.put("deleted", Boolean.FALSE);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/updateStatus/{id}/{status}")
    public ResponseEntity<ProjectDTO> updateStatus(@PathVariable("id") long id,
                                                   @PathVariable("status") ProjectStatus status) throws ResourceNotFoundException {
        return ResponseEntity.ok(projectService.updateStatus(id, status));
    }

    @PutMapping("/addMember/{projectId}/{employeeId}")
    public ResponseEntity<?> addMember(@PathVariable("projectId") long projectId,
                                       @PathVariable("employeeId") long employeeId) throws ResourceNotFoundException {
        return ResponseEntity.ok(projectService.addMemberToProject(projectId, employeeId));
    }

    @PutMapping("/removeMember/{projectId}/{employeeId}")
    public ResponseEntity<?> removeMember(@PathVariable("projectId") long projectId,
                                          @PathVariable("employeeId") long employeeId) throws ResourceNotFoundException {
        return ResponseEntity.ok(projectService.removeMemberFromProject(projectId, employeeId));
    }

    @PutMapping("/chooseLeader/{projectId}/{employeeId}")
    public ResponseEntity<?> chooseLeader(@PathVariable("projectId") long projectId,
                                          @PathVariable("employeeId") long employeeId) throws ResourceNotFoundException {
        return ResponseEntity.ok(projectService.chooseLeader(projectId, employeeId));
    }

    @GetMapping("/getMembers/{id}")
    public ResponseEntity<List<EmployeeDTO>> getMembers(@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(projectService.findMembersOfProject(id));
    }
    
    @GetMapping("/getNotMembers/{id}")
    public ResponseEntity<List<EmployeeDTO>> getNotMembers(@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(projectService.findMembersNotInProject(id));
    }
}
