package com.example.SpringBootTesting_07.services.impl;

import com.example.SpringBootTesting_07.TestContainerConfiguration;
import com.example.SpringBootTesting_07.dto.EmployeeDto;
import com.example.SpringBootTesting_07.entities.Employee;
import com.example.SpringBootTesting_07.repositories.EmployeeRepository;
import com.example.SpringBootTesting_07.services.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainerConfiguration.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService; // employeeRepository is dependent on employeeService

    private Employee mockEmployee;

    private EmployeeDto mockEmployeeDto;

    @BeforeEach
    void setUp() {
        mockEmployee = Employee
                .builder()
                .id(1L)
                .email("shubham@gmail.com")
                .name("Shubham")
                .salary(300L)
                .build();

        mockEmployeeDto = modelMapper.map(mockEmployee, EmployeeDto.class);
    }

    @Test
    void testGetEmployeeById_whenEmployeeIdIsPresent_thenReturnEmployee() {
        // assign
        when(employeeRepository.findById(mockEmployee.getId())).thenReturn(Optional.of(mockEmployee));

        // act
        EmployeeDto employeeDto = employeeService.getEmployeeById(mockEmployee.getId());

        // assert
        assertThat(employeeDto.getId()).isEqualTo(mockEmployee.getId());
        assertThat(employeeDto.getEmail()).isEqualTo(mockEmployee.getEmail());
        assertThat(employeeDto).isNotNull();
        verify(employeeRepository).findById(mockEmployee.getId()); // here we are checking if findById() of employeeRepository is being call or not
        // verify(employeeRepository).save(null); // here we are checking save method in employeeRepository is being called or not, which is actually not called
        // verify(employeeRepository,times(2)).findById(id); // here we are checking that if findById method inside employeeRepository is being called 2 times or not
    }

    @Test
    void testCreateNewEmployee_whenValidEmployee_ThenCreateNewEmployee() {
        // assign
        when(employeeRepository.findByEmail(anyString())).thenReturn(List.of());
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);

        // act
        EmployeeDto employeeDto = employeeService.createNewEmployee(mockEmployeeDto);

        // assert
        ArgumentCaptor<Employee> employeeArgumentCaptor = ArgumentCaptor.forClass(Employee.class);


        assertThat(employeeDto).isNotNull();
        assertThat(employeeDto.getId()).isEqualTo(1L);
        assertThat(employeeDto.getEmail()).isEqualTo(employeeDto.getEmail());
        verify(employeeRepository).save(employeeArgumentCaptor.capture()); // here we are verifying what ever value we are passing that is intended or not

        Employee captorValue = employeeArgumentCaptor.getValue();
        assertThat(captorValue.getEmail()).isEqualTo(mockEmployeeDto.getEmail());

    }

    @Test
    void createNewEmployee_whenEmailAlreadyExists_shouldThrowRuntimeException() {
        // assign
        when(employeeRepository.findByEmail(mockEmployeeDto.getEmail())).thenReturn(List.of(mockEmployee));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> employeeService.createNewEmployee(mockEmployeeDto));

        assertThat(ex.getMessage()).contains("Employee already exists with email");

        // verify that save was never called
        verify(employeeRepository,never()).save(any(Employee.class));

    }
}