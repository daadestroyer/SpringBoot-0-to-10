package com.example.CachingApp.services;

import com.example.CachingApp.dto.EmployeeDto;
import com.example.CachingApp.entities.Employee;
import com.example.CachingApp.entities.SalaryAccount;

import java.util.List;

public interface SalaryAccountService {
    void createAccount(Employee employee);

    SalaryAccount incrementBalance(Long accountId);

    List<EmployeeDto> getAllEmployee();
}
