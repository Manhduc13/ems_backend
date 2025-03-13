package com.ndm.serve.controllers;

import com.ndm.serve.dtos.role.RoleDTO;
import com.ndm.serve.exceptions.ResourceNotFoundException;
import com.ndm.serve.services.role.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {

    RoleService roleService;

    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAll() {
        return ResponseEntity.ok(roleService.getAll());
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<RoleDTO> getById(@PathVariable long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(roleService.getById(id));
    }
}
