package com.example.SpringBootTesting_07.services.impl;

import com.example.SpringBootTesting_07.TestContainerConfiguration;
import com.example.SpringBootTesting_07.dto.EmployeeDto;
import com.example.SpringBootTesting_07.entities.Employee;
import com.example.SpringBootTesting_07.exceptions.ResourceNotFoundException;
import com.example.SpringBootTesting_07.repositories.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

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
    private EmployeeDto mockUpdateEmployeeDto;

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

        mockUpdateEmployeeDto = EmployeeDto.builder()
                .id(1L)
                .email("shubham@gmail.com") // must match existing email for positive case
                .name("Shubham Updated")
                .salary(500L)
                .build();
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
    void testGetEmployeeById_whenEmployeeIdIsNotPresent_thenThrowException() {
        // arrange
        Long employeeId = 999L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // act
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> employeeService.getEmployeeById(employeeId));

        assertThat(ex.getMessage()).isEqualTo("Employee not found with id: " + employeeId);

        // assert
        verify(employeeRepository, times(1)).findById(employeeId);

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
    void testCreateNewEmployee_whenEmailAlreadyExists_shouldThrowRuntimeException() {
        // assign
        when(employeeRepository.findByEmail(mockEmployeeDto.getEmail())).thenReturn(List.of(mockEmployee));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> employeeService.createNewEmployee(mockEmployeeDto));

        assertThat(ex.getMessage()).contains("Employee already exists with email");

        // verify that save was never called
        verify(employeeRepository, never()).save(any(Employee.class));

    }

    @Test
    void updateEmployee_whenIdExistsAndEmailUnchanged_shouldReturnUpdatedDto() {
        // Arrange
        mockEmployeeDto = EmployeeDto.builder()
                .id(mockEmployee.getId())
                .email(mockEmployee.getEmail())
                .name(mockEmployee.getName())
                .salary(mockEmployee.getSalary())
                .build();

        Long id = mockEmployeeDto.getId();
        when(employeeRepository.findById(id)).thenReturn(Optional.of(mockEmployee));

        // Simulate modelMapper.map(employeeDto, employee) that copies fields into existing employee
        doAnswer(invocation -> {
            EmployeeDto src = invocation.getArgument(0);
            Employee target = invocation.getArgument(1);
            // apply fields that would be mapped (except id/email since email must remain same)
            target.setName(src.getName());
            target.setSalary(src.getSalary());
            return target;
        }).when(modelMapper).map(any(EmployeeDto.class), any(Employee.class));

        // After save, return same employee (with updated values)
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When mapping back to DTO, return a DTO reflecting updated values
        EmployeeDto returnedDto = EmployeeDto.builder()
                .id(id)
                .email(mockEmployee.getEmail())
                .name(mockUpdateEmployeeDto.getName())   // or mockEmployeeDto.getName() depending on what you pass
                .salary(mockUpdateEmployeeDto.getSalary())
                .build();
        doReturn(returnedDto).when(modelMapper).map(any(Employee.class), eq(EmployeeDto.class));

        // Act
        EmployeeDto result = employeeService.updateEmployee(id, mockUpdateEmployeeDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getEmail()).isEqualTo(mockEmployee.getEmail());
        assertThat(result.getName()).isEqualTo(mockUpdateEmployeeDto.getName());
        assertThat(result.getSalary()).isEqualTo(mockUpdateEmployeeDto.getSalary());

        verify(employeeRepository).findById(id);
        verify(modelMapper).map(mockUpdateEmployeeDto, mockEmployee); // mapped into existing entity
        verify(employeeRepository).save(mockEmployee);
    }

    @Test
    void updateEmployee_whenEmailChanged_shouldThrowRuntimeException() {
        // Arrange
        Long id = mockEmployee.getId();
        when(employeeRepository.findById(id)).thenReturn(Optional.of(mockEmployee));

        // DTO with a different email -> should trigger RuntimeException
        EmployeeDto dtoWithDifferentEmail = EmployeeDto.builder()
                .id(id)
                .email("different@gmail.com") // different from mockEmployee.email
                .name("Some Name")
                .salary(123L)
                .build();

        // Act + Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> employeeService.updateEmployee(id, dtoWithDifferentEmail));

        assertThat(ex.getMessage()).isEqualTo("The email of the employee cannot be updated");

        // Verfiy
        verify(employeeRepository).findById(id);
        verify(modelMapper, never()).map(any(EmployeeDto.class), any(Employee.class));
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void test_deleteEmployee_whenIdExists_shouldDelete() {
        // Arrange
        Long id = mockEmployeeDto.getId();
        when(employeeRepository.existsById(id)).thenReturn(true);

        // doNothing is the default for void methods but be explicit
        doNothing().when(employeeRepository).deleteById(id);

        // Act
        employeeService.deleteEmployee(id);

        // Assert
        verify(employeeRepository).existsById(id);
        verify(employeeRepository).deleteById(id);
    }

    @Test
    void test_deleteEmployeeWhenIdNotExists_shouldThrowResourceNotFoundException(){
        // Arrange
        Long id = 999L;
        when(employeeRepository.existsById(id)).thenReturn(false);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> employeeService.deleteEmployee(id));

        assertThat(ex.getMessage()).isEqualTo("Employee not found with id: "+id);

        verify(employeeRepository).existsById(id);
        verify(employeeRepository,never()).deleteById(anyLong());
    }
}