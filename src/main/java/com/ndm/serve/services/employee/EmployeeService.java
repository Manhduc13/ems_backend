package com.ndm.serve.services.employee;

import com.ndm.serve.dtos.employee.EmployeeCUDTO;
import com.ndm.serve.dtos.employee.EmployeeDTO;
import com.ndm.serve.dtos.resetPassword.ChangePasswordRequestDTO;
import com.ndm.serve.exceptions.ResourceNotFoundException;
import com.ndm.serve.models.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface EmployeeService {
    Page<EmployeeDTO> searchWithFilter(String keyword, Pageable pageable);

    EmployeeDTO getById(long id) throws ResourceNotFoundException;

    List<EmployeeDTO> getAll();

    EmployeeDTO create(EmployeeCUDTO request) throws ResourceNotFoundException, IOException;

    EmployeeDTO update(long id, EmployeeCUDTO request) throws ResourceNotFoundException, IOException;

    boolean delete(long id) throws ResourceNotFoundException;

    boolean changeStatus(long id) throws ResourceNotFoundException;

    Account changePassword(long id, ChangePasswordRequestDTO request) throws ResourceNotFoundException;

    EmployeeDTO getCurrentEmployee();
}
