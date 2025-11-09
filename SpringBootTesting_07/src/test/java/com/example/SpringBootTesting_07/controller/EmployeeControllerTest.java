package com.example.SpringBootTesting_07.controller;

import com.example.SpringBootTesting_07.dto.EmployeeDto;
import com.example.SpringBootTesting_07.exceptions.ResourceNotFoundException;
import com.example.SpringBootTesting_07.services.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    private EmployeeDto employeeDto;

    @BeforeEach
    void setUpt() {
        employeeDto = EmployeeDto
                .builder()
                .id(1L)
                .email("shubham@gmail.com")
                .name("Shubham")
                .salary(300L)
                .build();

    }

    @Test
    @DisplayName("GET /employees/{id} -- when found return 200 and employee JSON")
    void test_getEmployeeById_whenFound_returns200AndEmployee() throws Exception {
        // Arrange
        when(employeeService.getEmployeeById(employeeDto.getId())).thenReturn(employeeDto);

        // Act & Assert
        mockMvc
                .perform(get("/employees/{id}", employeeDto.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(employeeDto.getId().intValue())))
                .andExpect(jsonPath("$.email", is(employeeDto.getEmail())))
                .andExpect(jsonPath("$.name", is(employeeDto.getName())))
                .andExpect(jsonPath("$.salary", is(employeeDto.getSalary().intValue())));
    }

    @Test
    @DisplayName("GET /employees/{id} -- when not return 404")
    void test_getEmployeeById_whenNotFound_returns400AndEmployee() throws Exception {
        // Arrange
        Long missingId = 999L;
        when(employeeService.getEmployeeById(missingId))
                .thenThrow(new ResourceNotFoundException("Employee not found with id: " + missingId));

        // Act & Assert
        mockMvc.perform(get("/employees/{id}", missingId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /employees -- when valid request returns 201 and created employee JSON")
    void test_createNewEmployee_whenValid_returns201andEmployee() throws Exception {
        // Arrange
        when(employeeService.createNewEmployee(any(EmployeeDto.class))).thenReturn(employeeDto);

        // Act & Assert
        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                                                "email": "shubham@gmail.com",
                                                                "name": "Shubham",
                                                                "salary": 300
                                                            }
                                """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(employeeDto.getId().intValue())))
                .andExpect(jsonPath("$.email", is(employeeDto.getEmail())))
                .andExpect(jsonPath("$.name", is(employeeDto.getName())))
                .andExpect(jsonPath("$.salary", is(employeeDto.getSalary().intValue())));
    }

    @Test
    @DisplayName("POST /employees -- when email already exists returns 409 Conflict")
    void test_createNewEmployee_whenEmailAlreadyExists_return409() throws Exception {
        // Arrange
        when(employeeService.createNewEmployee(any(EmployeeDto.class)))
                .thenThrow(new RuntimeException("Employee already exists with email: " + employeeDto.getEmail()));

        // Act & Assert
        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "shubham@gmail.com",
                                    "name": "Shubham",
                                    "salary": 300
                                }
                                """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("PUT /employees/{id} - when valid returns 200 and updated employee JSON")
    void test_updateEmployee_whenValid_returns200AndUpdatedEmployee() throws Exception {
        // Arrange
        Long id = 1L;
        EmployeeDto requestDto = EmployeeDto.builder()
                .id(1L)
                .name("shubham updated")
                .email("shubhamupdated@gmail.com")
                .salary(500L)
                .build();

        EmployeeDto responseDto = EmployeeDto.builder()
                .id(id)
                .email(requestDto.getEmail())
                .name(requestDto.getName())
                .salary(requestDto.getSalary())
                .build();

        when(employeeService.updateEmployee(eq(id), any(EmployeeDto.class))).thenReturn(responseDto);

        String json = """
                {
                  "id": 1,
                  "email": "shubham@gmail.com",
                  "name": "Shubham Updated",
                  "salary": 500
                }
                """;

        // Act & Assert
        mockMvc.perform(put("/employees/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(responseDto.getId().intValue())))
                .andExpect(jsonPath("$.email", is(responseDto.getEmail())))
                .andExpect(jsonPath("$.name", is(responseDto.getName())))
                .andExpect(jsonPath("$.salary", is(responseDto.getSalary().intValue())));
    }

    @Test
    @DisplayName("PUT /employees/{id} - when id not found returns 404 Not Found")
    void test_updateEmployee_whenIdNotFound_returns404() throws Exception {
        // Arrange
        Long id = 999L;
        EmployeeDto requestDto = EmployeeDto.builder()
                .id(id)
                .email("notfound@gmail.com")
                .name("No One")
                .salary(100L)
                .build();

        when(employeeService.updateEmployee(eq(id), any(EmployeeDto.class)))
                .thenThrow(new ResourceNotFoundException("Employee not found with id: " + id));

        String json = """
                {
                  "id": 999,
                  "email": "notfound@gmail.com",
                  "name": "No One",
                  "salary": 100
                }
                """;

        // Act & Assert
        mockMvc.perform(put("/employees/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /employees/{id} - when employee exists returns 204 No Content")
    void test_deleteEmployee_whenExits_returns204() throws Exception {
        // Arrange
        Long id = 1L;
        doNothing().when(employeeService).deleteEmployee(id);

        // Act & Assert
        mockMvc.perform(delete("/employees/{id}", id))
                .andExpect(status().isNoContent());
    }
}