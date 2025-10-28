package com.example.JWT.dto;

import com.example.JWT.entity.Role;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class SignUpDto {
    private String email;
    private String password;
    private String name;
    private List<Role> roles;
}
