package com.ndm.serve.services.auth;

import com.ndm.serve.dtos.employee.EmployeeInfoDTO;
import com.ndm.serve.exceptions.ResourceNotFoundException;

public interface AuthService {
    EmployeeInfoDTO getEmployeeInfo(String username) throws ResourceNotFoundException;
}
