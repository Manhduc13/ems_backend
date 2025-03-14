package com.ndm.serve.services.employee;

import com.ndm.serve.dtos.employee.EmployeeCUDTO;
import com.ndm.serve.dtos.employee.EmployeeDTO;
import com.ndm.serve.dtos.resetPassword.ChangePasswordRequestDTO;
import com.ndm.serve.exceptions.ResourceNotFoundException;
import com.ndm.serve.models.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {
    Page<EmployeeDTO> searchWithFilter(String keyword, Pageable pageable);

    EmployeeDTO getById(long id) throws ResourceNotFoundException;

    List<EmployeeDTO> getAll();

    EmployeeDTO create(EmployeeCUDTO request) throws ResourceNotFoundException;

    EmployeeDTO update(long id, EmployeeCUDTO request) throws ResourceNotFoundException;

    boolean delete(long id) throws ResourceNotFoundException;

    EmployeeDTO banned(long id) throws ResourceNotFoundException;

    Account changePassword(long id, ChangePasswordRequestDTO request) throws ResourceNotFoundException;
}
