package com.ndm.serve.repositories;

import com.ndm.serve.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByUsername(String username);

    Optional<Employee> findByPhone(String phone);
}
