package com.example.CachingApp.services.impl;


import com.example.CachingApp.dto.EmployeeDto;
import com.example.CachingApp.entities.Employee;
import com.example.CachingApp.entities.SalaryAccount;
import com.example.CachingApp.repositories.EmployeeRepository;
import com.example.CachingApp.repositories.SalaryAccountRepository;
import com.example.CachingApp.services.SalaryAccountService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class SalaryAccountServiceImpl implements SalaryAccountService {

    private final SalaryAccountRepository salaryAccountRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Override
    public void createAccount(Employee employee) {

//        if(employee.getName().equals("Anuj")) throw new RuntimeException("Anuj is not allowed");

        SalaryAccount salaryAccount = SalaryAccount.builder()
//                .employee(employee)
                .balance(BigDecimal.ZERO)
                .build();

        salaryAccountRepository.save(salaryAccount);
    }

    @Override
    @Transactional
    public SalaryAccount incrementBalance(Long accountId) {

        SalaryAccount salaryAccount = salaryAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        BigDecimal prevBalance = salaryAccount.getBalance();
        BigDecimal newBalance = prevBalance.add(BigDecimal.valueOf(1L));

        salaryAccount.setBalance(newBalance);

        return salaryAccountRepository.save(salaryAccount);
    }

    @Override
    public List<EmployeeDto> getAllEmployee() {
        List<Employee> employeeList = employeeRepository.findAll();
        return employeeList
                .stream()
                .map((element) -> modelMapper.map(element, EmployeeDto.class))
                .collect(Collectors.toList());
    }
}

