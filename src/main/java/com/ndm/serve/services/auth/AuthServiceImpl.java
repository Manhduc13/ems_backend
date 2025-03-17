package com.ndm.serve.services.auth;

import com.ndm.serve.dtos.employee.EmployeeInfoDTO;
import com.ndm.serve.exceptions.ResourceNotFoundException;
import com.ndm.serve.mappers.EmployeeMapper;
import com.ndm.serve.models.Employee;
import com.ndm.serve.models.Role;
import com.ndm.serve.repositories.EmployeeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService, UserDetailsService {

    EmployeeRepository employeeRepository;
    EmployeeMapper employeeMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByUsername(username).orElse(null);
        if (employee == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if (!employee.isActive()) {
            throw new UsernameNotFoundException("Your account has been disabled");
        }
        Set<GrantedAuthority> authorities = employee.getRoles().stream()
                .map(role -> "ROLE_" + role.getName())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return new User(employee.getUsername(), employee.getPassword(),
                authorities);
    }

    @Override
    public EmployeeInfoDTO getEmployeeInfo(String username) throws ResourceNotFoundException {
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this username: " + username));

        Set<String> roleNames = new HashSet<>();
        Set<Role> roles = employee.getRoles();
        roles.forEach(role -> {
            roleNames.add(role.getName().toString());
        });

        return EmployeeInfoDTO.builder()
                .id(employee.getId())
                .email(employee.getEmail())
                .username(employee.getUsername())
                .isActive(employee.isActive())
                .roles(roleNames)
                .avatar(employee.getAvatar())
                .build();
    }
}
