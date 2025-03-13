package com.ndm.serve.mappers;

import com.ndm.serve.dtos.role.RoleCUDTO;
import com.ndm.serve.dtos.role.RoleDTO;
import com.ndm.serve.models.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    //    @Mapping(target = "id", source = "id")
//    @Mapping(target = "name", source = "name")
    RoleDTO toRoleDTO(Role role);

    Role toRole(RoleCUDTO roleCUDTO);
}
