package com.example.SpringBootTesting_07.repositories;

import com.example.SpringBootTesting_07.entities.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    // inject container properties into Spring Environment before context refresh
    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        // ensure schema is created for tests
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "true");
    }

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