package com.ndm.serve.controllers;

import com.ndm.serve.dtos.employee.EmployeeCUDTO;
import com.ndm.serve.dtos.employee.EmployeeDTO;
import com.ndm.serve.dtos.resetPassword.ChangePasswordRequestDTO;
import com.ndm.serve.exceptions.ResourceNotFoundException;
import com.ndm.serve.services.employee.EmployeeService;
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

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/employees")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeController {

    EmployeeService employeeService;
    PagedResourcesAssembler<EmployeeDTO> pagedResourcesAssembler;

    @GetMapping("/search")
    public ResponseEntity<?> searchWithFilter(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "username") String sortBy,
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

        Page<EmployeeDTO> employeeDTOS = employeeService.searchWithFilter(keyword, pageable);

        // Convert to paged model
        var pagedModel = pagedResourcesAssembler.toModel(employeeDTOS);
        Collection<EntityModel<EmployeeDTO>> data = pagedModel.getContent();
        var links = pagedModel.getLinks();

        var response = new CustomPagedResponse<EntityModel<EmployeeDTO>>(data, pagedModel.getMetadata(), links);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAll() {
        return ResponseEntity.ok(employeeService.getAll());
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<EmployeeDTO> getById(@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(employeeService.getById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid EmployeeCUDTO request,
                                    BindingResult bindingResult) throws ResourceNotFoundException, IOException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        EmployeeDTO employeeDTO = employeeService.create(request);
        return ResponseEntity.ok(employeeDTO);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id,
                                    @RequestBody @Valid EmployeeCUDTO request,
                                    BindingResult bindingResult) throws ResourceNotFoundException, IOException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        return ResponseEntity.ok(employeeService.update(id, request));
    }

    @PutMapping("/changeStatus/{id}")
    public ResponseEntity<?> banned(@PathVariable("id") Long id) throws ResourceNotFoundException {
        boolean success = employeeService.changeStatus(id);
        Map<String, Boolean> response = new HashMap<>();
        if (success) {
            response.put("changed", Boolean.TRUE);
        } else {
            response.put("changed", Boolean.FALSE);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) throws ResourceNotFoundException {
        Map<String, Boolean> response = new HashMap<>();
        if (employeeService.delete(id)) {
            response.put("deleted", Boolean.TRUE);
        } else {
            response.put("deleted", Boolean.FALSE);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/changePassword/{id}")
    public ResponseEntity<?> changePassword(
            @PathVariable("id") long id,
            @RequestBody @Valid ChangePasswordRequestDTO request,
            BindingResult bindingResult
    ) throws ResourceNotFoundException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        return ResponseEntity.ok(employeeService.changePassword(id, request));
    }

    @GetMapping("/myInfo")
    public ResponseEntity<EmployeeDTO> getMyInfo() {
        return ResponseEntity.ok(employeeService.getCurrentEmployee());
    }
}
