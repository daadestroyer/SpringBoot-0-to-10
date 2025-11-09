package com.example.SpringBootTesting_07.repositories;

import com.example.SpringBootTesting_07.TestContainerConfiguration;
import com.example.SpringBootTesting_07.entities.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainerConfiguration.class)
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;


    @BeforeEach
    void setUp() {
        employee = Employee
                .builder()
                .email("shubhamnigam@gmail.com")
                .name("Shubham Nigam")
                .salary(110000L)
                .build();
    }

    @Test
    void testFindByEmail_whenEmailIsPresent_thenReturnEmployee() {
        // Given
        Employee saved = employeeRepository.save(employee);

        // When
        List<Employee> employeeList = employeeRepository.findByEmail(saved.getEmail());

        // Then
        assertThat(employeeList).isNotNull();
        assertThat(employeeList).size().isGreaterThan(0);
    }

    @Test
    void testFindByEmail_whenEmailIsNotFound_thenReturnEmptyEmployeeList(){
        // given
        String email = "notpresent@gmail.com";

        // when
        List<Employee> employeeList = employeeRepository.findByEmail(email);

        // then
        assertThat(employeeList).isNotNull();
        assertThat(employeeList).isEmpty();

    }
}