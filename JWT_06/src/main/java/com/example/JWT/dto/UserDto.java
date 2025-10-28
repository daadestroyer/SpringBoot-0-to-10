package com.example.JWT.dto;

import com.example.JWT.entity.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private Set<Role> roles; // add this
}

