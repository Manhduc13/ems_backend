package com.ndm.serve.services.employee;


import com.ndm.serve.models.Account;
import com.ndm.serve.models.Employee;

public interface AccountService {
    Account generateAccount(Employee employee);
}
