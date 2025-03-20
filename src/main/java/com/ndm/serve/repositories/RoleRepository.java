package com.ndm.serve.repositories;

import com.ndm.serve.enums.EmployeeRole;
import com.ndm.serve.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(EmployeeRole name);
}
