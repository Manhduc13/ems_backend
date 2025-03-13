package com.ndm.serve.services.employee;

import com.ndm.serve.dtos.employee.EmployeeCUDTO;
import com.ndm.serve.dtos.employee.EmployeeDTO;
import com.ndm.serve.exceptions.ResourceNotFoundException;

import java.util.List;

public interface EmployeeService {
    EmployeeDTO getById(long id) throws ResourceNotFoundException;

    List<EmployeeDTO> getAll();

    EmployeeDTO create(EmployeeCUDTO request) throws ResourceNotFoundException;

    EmployeeDTO update(long id, EmployeeCUDTO request) throws ResourceNotFoundException;

    boolean delete(long id) throws ResourceNotFoundException;

    EmployeeDTO banned(long id) throws ResourceNotFoundException;
}
