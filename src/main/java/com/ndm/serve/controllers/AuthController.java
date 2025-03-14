package com.ndm.serve.controllers;

import com.ndm.serve.dtos.auth.LoginRequestDTO;
import com.ndm.serve.dtos.auth.LoginResponseDTO;
import com.ndm.serve.dtos.employee.EmployeeInfoDTO;
import com.ndm.serve.exceptions.ResourceNotFoundException;
import com.ndm.serve.services.auth.AuthService;
import com.ndm.serve.services.token.TokenService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthService authService;
    AuthenticationManagerBuilder authenticationManagerBuilder;
    TokenService tokenService;

    // Login API
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO, BindingResult bindingResult) throws ResourceNotFoundException {
        // Validate request
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        // Create authentication token with username and password
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequestDTO.getUsername(), loginRequestDTO.getPassword());

        // Authenticate user
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // Set authentication to Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String accessToken = tokenService.generateToken(authentication);

        EmployeeInfoDTO employeeInfoDTO = authService.getEmployeeInfo(loginRequestDTO.getUsername());

        // create response
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setAccessToken(accessToken);
        loginResponseDTO.setEmployeeInfoDTO(employeeInfoDTO);

        return ResponseEntity.ok(loginResponseDTO);
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestBody String token) {
        boolean isValid = tokenService.validateToken(token);
        Map<String, Boolean> response = new HashMap<>();
        if (isValid) {
            response.put("verify", Boolean.TRUE);
        } else {
            response.put("verify", Boolean.FALSE);
        }
        return ResponseEntity.ok(response);
    }

}
