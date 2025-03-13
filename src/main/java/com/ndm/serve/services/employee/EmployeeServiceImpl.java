package com.ndm.serve.services.employee;

import com.ndm.serve.dtos.email.EmailRequestDTO;
import com.ndm.serve.dtos.employee.EmployeeCUDTO;
import com.ndm.serve.dtos.employee.EmployeeDTO;
import com.ndm.serve.exceptions.ResourceNotFoundException;
import com.ndm.serve.mappers.EmployeeMapper;
import com.ndm.serve.models.Account;
import com.ndm.serve.models.Employee;
import com.ndm.serve.models.Role;
import com.ndm.serve.repositories.EmployeeRepository;
import com.ndm.serve.repositories.RoleRepository;
import com.ndm.serve.services.mail.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeServiceImpl implements EmployeeService {
    EmployeeRepository employeeRepository;
    RoleRepository roleRepository;
    EmployeeMapper employeeMapper;
    PasswordEncoder passwordEncoder;
    AccountService accountService;
    EmailService emailService;

    @Value("${ems.email.template.account-information.name}")
    @NonFinal
    private String accountTemplate;

    @Override
    public EmployeeDTO getById(long id) throws ResourceNotFoundException {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id: " + id));
        return employeeMapper.toEmployeeDTO(employee);
    }

    @Override
    public List<EmployeeDTO> getAll() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream().map(employeeMapper::toEmployeeDTO).collect(Collectors.toList());
    }

    @Override
    public EmployeeDTO create(EmployeeCUDTO request) throws ResourceNotFoundException {
        if (request == null) {
            throw new IllegalArgumentException("Employee create request cannot be null");
        }
        // Check unique email
        Employee existedEmail = employeeRepository.findByEmail(request.getEmail()).orElse(null);
        if (existedEmail != null) {
            throw new IllegalArgumentException("Employee already exists with email: " + request.getEmail());
        }
        // Check unique phone
        Employee existedPhone = employeeRepository.findByPhone(request.getPhone()).orElse(null);
        if (existedPhone != null) {
            throw new IllegalArgumentException("Employee already exists with phone: " + request.getPhone());
        }
        // Convert CUDTO to entity
        Employee employee = employeeMapper.toEmployee(request);
        // Convert role ids to role
        Set<Long> roleIds = request.getRoleIds();
        Set<Role> roles = new HashSet<>();
        for (Long roleId : roleIds) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found for this id: " + roleId));
            roles.add(role);
        }
        employee.setRoles(roles);
        // Generate account and save to entity
        Account newAccount = accountService.generateAccount(employee);
        employee.setUsername(newAccount.getUsername());
        employee.setPassword(passwordEncoder.encode(newAccount.getPassword()));
        employee.setActive(true);
        // Send email
        EmailRequestDTO emailRequestDTO = new EmailRequestDTO();
        emailRequestDTO.setTo(request.getEmail());
        emailRequestDTO.setSubject("Your account");
        emailRequestDTO.setTemplateName(accountTemplate);

        Map<String, Object> model = Map.of(
                "username", newAccount.getUsername(),
                "password", newAccount.getPassword()
        );
        emailRequestDTO.setVariables(model);
        emailService.sendEmailAsync(emailRequestDTO);

        // Save new employee to db
        return employeeMapper.toEmployeeDTO(employeeRepository.save(employee));
    }

    @Override
    public EmployeeDTO update(long id, EmployeeCUDTO request) throws ResourceNotFoundException {
        if (request == null) {
            throw new IllegalArgumentException("Employee update request cannot be null");
        }
        Employee existedEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id: " + id));

        // Check unique email
        Employee existedEmail = employeeRepository.findByEmail(request.getEmail()).orElse(null);
        if (existedEmail != null && existedEmail.getId() != existedEmployee.getId()) {
            throw new IllegalArgumentException("Employee already exists with email: " + request.getEmail());
        }
        // Check unique phone
        Employee existedPhone = employeeRepository.findByPhone(request.getPhone()).orElse(null);
        if (existedPhone != null && existedPhone.getId() != existedEmployee.getId()) {
            throw new IllegalArgumentException("Employee already exists with phone: " + request.getPhone());
        }

        existedEmployee.setFirstName(request.getFirstName());
        existedEmployee.setLastName(request.getLastName());
        existedEmployee.setPhone(request.getPhone());
        existedEmployee.setEmail(request.getEmail());
        existedEmployee.setGender(request.getGender());
        existedEmployee.setAddress(request.getAddress());
        existedEmployee.setDob(request.getDob());

        return employeeMapper.toEmployeeDTO(employeeRepository.save(existedEmployee));
    }

    @Override
    public boolean delete(long id) throws ResourceNotFoundException {
        Employee existedEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id: " + id));

        employeeRepository.delete(existedEmployee);

        return !employeeRepository.existsById(id);
    }

    @Override
    public EmployeeDTO banned(long id) throws ResourceNotFoundException {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id: " + id));

        employee.setActive(false);

        return employeeMapper.toEmployeeDTO(employeeRepository.save(employee));
    }
}
