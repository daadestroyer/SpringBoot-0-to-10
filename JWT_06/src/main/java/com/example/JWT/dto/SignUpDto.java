package com.example.JWT.dto;

import com.example.JWT.entity.Role;
import lombok.Data;

import java.util.Set;

@Data
public class SignUpDto {
    private String email;
    private String password;
    private String name;
    private Set<Role> roles; // optional; if null, default will be USER
}
