package com.ndm.serve.controllers;

import com.ndm.serve.dtos.employee.EmployeeCUDTO;
import com.ndm.serve.dtos.employee.EmployeeDTO;
import com.ndm.serve.dtos.resetPassword.ChangePasswordRequestDTO;
import com.ndm.serve.exceptions.ResourceNotFoundException;
import com.ndm.serve.services.employee.EmployeeService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/employees")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeController {
    EmployeeService employeeService;

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
                                    BindingResult bindingResult) throws ResourceNotFoundException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        EmployeeDTO employeeDTO = employeeService.create(request);
        System.out.println(employeeDTO);
        return ResponseEntity.ok(employeeDTO);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id,
                                    @RequestBody @Valid EmployeeCUDTO request,
                                    BindingResult bindingResult) throws ResourceNotFoundException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        return ResponseEntity.ok(employeeService.update(id, request));
    }

    @PutMapping("/banned/{id}")
    public ResponseEntity<?> banned(@PathVariable("id") Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(employeeService.banned(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) throws ResourceNotFoundException {
        employeeService.delete(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
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
}
