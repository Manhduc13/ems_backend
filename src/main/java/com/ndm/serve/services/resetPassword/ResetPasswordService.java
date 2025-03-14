package com.ndm.serve.services.resetPassword;


import com.ndm.serve.dtos.employee.EmployeeDTO;
import com.ndm.serve.dtos.resetPassword.NewPasswordDTO;
import com.ndm.serve.dtos.resetPassword.ResetPasswordRequestDTO;
import com.ndm.serve.exceptions.ResourceNotFoundException;

public interface ResetPasswordService {
    void sendEmail(ResetPasswordRequestDTO resetPasswordRequestDTO) throws ResourceNotFoundException;

    EmployeeDTO resetPassword(NewPasswordDTO request) throws ResourceNotFoundException;
}
