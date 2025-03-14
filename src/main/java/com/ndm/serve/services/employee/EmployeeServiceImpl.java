package com.ndm.serve.services.employee;

import com.ndm.serve.dtos.email.EmailRequestDTO;
import com.ndm.serve.dtos.employee.EmployeeCUDTO;
import com.ndm.serve.dtos.employee.EmployeeDTO;
import com.ndm.serve.dtos.resetPassword.ChangePasswordRequestDTO;
import com.ndm.serve.enums.EmployeeRole;
import com.ndm.serve.exceptions.ResourceNotFoundException;
import com.ndm.serve.mappers.EmployeeMapper;
import com.ndm.serve.models.Account;
import com.ndm.serve.models.Employee;
import com.ndm.serve.models.Role;
import com.ndm.serve.repositories.EmployeeRepository;
import com.ndm.serve.repositories.RoleRepository;
import com.ndm.serve.services.mail.EmailService;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    public Page<EmployeeDTO> searchWithFilter(String keyword, Pageable pageable) {
        Specification<Employee> spec = (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return cb.conjunction();
            }

            String searchPattern = "%" + keyword.trim().toLowerCase() + "%";

            Predicate usernamePredicate = cb.like(cb.lower(root.get("username")), searchPattern);
            Predicate phonePredicate = cb.like(root.get("phone"), searchPattern);
            Predicate emailPredicate = cb.like(cb.lower(root.get("email")), searchPattern);
            Predicate addressPredicate = cb.like(cb.lower(root.get("address")), searchPattern);

            return cb.or(usernamePredicate, phonePredicate, emailPredicate, addressPredicate);
        };

        Page<Employee> employees = employeeRepository.findAll(spec, pageable);

        return employees.map(employeeMapper::toEmployeeDTO);
    }

    public Page<EmployeeDTO> searchWithFilter(String username, String phone, String email, String address, Pageable pageable) {
        Specification<Employee> spec = (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (username != null && !username.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("username"), "%" + username + "%"));
            }
            if (phone != null && !phone.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("phone"), "%" + phone + "%"));
            }
            if (email != null && !email.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("email"), "%" + email + "%"));
            }
            if (address != null && !address.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("address"), "%" + address + "%"));
            }

            return predicate;
        };

        Page<Employee> employees = employeeRepository.findAll(spec, pageable);

        return employees.map(employeeMapper::toEmployeeDTO);
    }

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
    public boolean changeStatus(long id) throws ResourceNotFoundException {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id: " + id));

        Set<Role> roles = employee.getRoles();
        Set<EmployeeRole> roleNames = new HashSet<>();
        for (Role role : roles) {
            roleNames.add(role.getName());
        }
        if (roleNames.contains(EmployeeRole.ADMIN)) {
            return false;
        }
        boolean currentStatus = employee.isActive();
        employee.setActive(!currentStatus);
        employeeRepository.save(employee);
        return true;
    }

    @Override
    public Account changePassword(long id, ChangePasswordRequestDTO request) throws ResourceNotFoundException {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id: " + id));

        if (!passwordEncoder.matches(request.getOldPassword(), employee.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        employee.setPassword(passwordEncoder.encode(request.getNewPassword()));

        Employee updatedPassword = employeeRepository.save(employee);

        return Account.builder()
                .username(updatedPassword.getUsername())
                .password(updatedPassword.getPassword())
                .build();
    }
}
