package com.ndm.serve.services.role;

import com.ndm.serve.dtos.role.RoleDTO;
import com.ndm.serve.exceptions.ResourceNotFoundException;

import java.util.List;

public interface RoleService {
    List<RoleDTO> getAll();

    RoleDTO getById(long id) throws ResourceNotFoundException;

    void deleteById(long id) throws ResourceNotFoundException;
}
