package com.ndm.serve.services.employee;

import com.ndm.serve.models.Account;
import com.ndm.serve.models.Employee;
import com.ndm.serve.repositories.EmployeeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AccountServiceImpl implements AccountService {
    EmployeeRepository employeeRepository;
    private SecureRandom random = new SecureRandom();

    @Override
    public Account generateAccount(Employee employee) {
        Account account = new Account();
        String fullName = employee.getFirstName() + " " + employee.getLastName();
        account.setUsername(generateUsername(fullName));
        account.setPassword(generatePassword());
        return account;
    }

    private String generateUsername(String fullName) {
        String[] nameParts = fullName.split(" ");

        String firstName = nameParts[nameParts.length - 1];
        StringBuilder initials = new StringBuilder();
        for (int i = 0; i < nameParts.length - 1; i++) {
            initials.append(nameParts[i].charAt(0));
        }
        String baseUsername = firstName + initials.toString();
        if (employeeRepository.findByUsername(baseUsername).isPresent()) {
            int count = 1;
            while (true) {
                String username = baseUsername + count;
                if (employeeRepository.findByUsername(username).isEmpty()) {
                    return username;
                }
                count++;
            }
        } else {
            return baseUsername;
        }
    }

    private String generatePassword() {
        String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
        String DIGITS = "0123456789";
        String SPECIAL_CHARACTERS = "!@#$%^&*()-_=+<>?";
        String ALL_CHARACTERS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARACTERS;

        int length = 8 + random.nextInt(5);
        StringBuilder password = new StringBuilder(length);
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));

        for (int i = 4; i < length; i++) {
            password.append(ALL_CHARACTERS.charAt(random.nextInt(ALL_CHARACTERS.length())));
        }
        return new String(password);
    }
}
