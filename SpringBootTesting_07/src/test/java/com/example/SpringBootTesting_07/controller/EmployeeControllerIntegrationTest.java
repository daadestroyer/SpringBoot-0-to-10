package com.example.SpringBootTesting_07.controller;

import com.example.SpringBootTesting_07.TestContainerConfiguration;
import com.example.SpringBootTesting_07.dto.EmployeeDto;
import com.example.SpringBootTesting_07.entities.Employee;
import com.example.SpringBootTesting_07.repositories.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // run real server of random port
@AutoConfigureWebTestClient(timeout = "100000")
@Import(TestContainerConfiguration.class)
class EmployeeControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    EmployeeRepository employeeRepository;

    private Employee testEmployee;

    private EmployeeDto employeeDto;

    @BeforeEach
    void setUp() {
        // ensure clean DB
        employeeRepository.deleteAll();

        testEmployee = Employee.builder()
                .name("Shubham")
                .email("shubham@gmail.com")
                .salary(100L)
                .build();

        employeeDto = EmployeeDto.builder()
                .name("Shubham")
                .email("shubham@gmail.com")
                .salary(100L)
                .build();
    }

    @Test
    void test_getEmployeeById_whenEmployeeIdIsCorrect() {
        Employee savedEmployee = employeeRepository.save(testEmployee);

        EmployeeDto savedEmployeeDto = EmployeeDto.builder().id(savedEmployee.getId())
                .name(savedEmployee.getName())
                .email(savedEmployee.getEmail())
                .salary(savedEmployee.getSalary())
                .build();

        webTestClient.get()
                .uri("/employees/{id}", savedEmployee.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmployeeDto.class)
                .isEqualTo(savedEmployeeDto);
    }

    @Test
    void test_getEmployeeById_whenEmployeeIdIsNotCorrect_thenThrowException() {
        Long nonExistingId = 999L;
        webTestClient.get()
                .uri("/employees/{id}", nonExistingId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenCreateValidEmployee_thenReturns201AndPersisted() {
        webTestClient.post()
                .uri("/employees")
                .bodyValue(employeeDto)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void whenCreateNewEmployee_thenReturn500() {
        // we save one employee already
        Employee savedEmployee = employeeRepository.save(testEmployee);

        // and trying to save one test employee same
        webTestClient.post()
                .uri("/employees")
                .bodyValue(employeeDto)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testDeleteEmployeeById_whenEmployeeIdIsCorrect_thenDeleteEmployee() {
        Employee savedEmployee = employeeRepository.save(testEmployee);
        webTestClient.delete()
                .uri("/employees/{id}", savedEmployee.getId())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class);
    }

    @Test
    void testUpdateEmployee_whenEmployeeExist_thenUpdateThatEmployee() {
        Employee savedEmployee = employeeRepository.save(testEmployee);
        webTestClient.put()
                .uri("/employees/{id}",savedEmployee.getId())
                .bodyValue(employeeDto)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testUpdateEmployee_whenEmployeeIdNotExist_thenThrowException() {
        Long missingId = 999L;
        webTestClient.put()
                .uri("/employees/{id}",missingId)
                .bodyValue(employeeDto)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeleteEmployee_whenEmployeeExist_thenDeleteEmployee() {
        Employee savedEmployee = employeeRepository.save(testEmployee);
        webTestClient.delete()
                .uri("/employees/{id}", savedEmployee.getId())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class);
    }

    @Test
    void testDeleteEmployee_whenEmployeeDoesNotExist_thenThrowException() {
        Long id = 999L;
        webTestClient.delete()
                .uri("/employees/{id}", id)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(Void.class);
    }


}