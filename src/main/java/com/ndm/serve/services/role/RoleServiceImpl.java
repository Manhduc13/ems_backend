package com.ndm.serve.services.role;

import com.ndm.serve.dtos.role.RoleDTO;
import com.ndm.serve.exceptions.ResourceNotFoundException;
import com.ndm.serve.mappers.RoleMapper;
import com.ndm.serve.models.Role;
import com.ndm.serve.repositories.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleServiceImpl implements RoleService {

    RoleRepository roleRepository;
    RoleMapper roleMapper;

    @Override
    public List<RoleDTO> getAll() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleDTO).collect(Collectors.toList());
    }

    @Override
    public RoleDTO getById(long id) throws ResourceNotFoundException {
        Role role = roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Role not found for this id: " + id));
        return roleMapper.toRoleDTO(role);
    }

    @Override
    public void deleteById(long id) throws ResourceNotFoundException {
        Role role = roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Role not found for this id: " + id));
        roleRepository.deleteById(id);
    }
}
