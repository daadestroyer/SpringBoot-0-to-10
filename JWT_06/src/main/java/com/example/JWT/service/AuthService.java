package com.example.JWT.service;

import com.example.JWT.dto.LoginDto;
import com.example.JWT.dto.SignUpDto;
import com.example.JWT.dto.UserDto;
import com.example.JWT.entity.Role;
import com.example.JWT.entity.User;
import com.example.JWT.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private  ModelMapper modelMapper;

    public UserDto signUp(SignUpDto signUpDto) {
        if (userRepository.findByEmail(signUpDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email " + signUpDto.getEmail() + " already exists");
        }

        // Default role is USER if none provided
        Set<Role> roles = (signUpDto.getRoles() == null || signUpDto.getRoles().isEmpty())
                ? Set.of(Role.USER)
                : signUpDto.getRoles();

        User user = User.builder()
                .email(signUpDto.getEmail())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .name(signUpDto.getName())
                .roles(roles)
                .build();

        User savedUser = userRepository.save(user);

        // Map to UserDto

        return modelMapper.map(savedUser, UserDto.class);
    }
    public String login(LoginDto loginDto) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );

            // Principal is your User (implements UserDetails)
            Object principal = auth.getPrincipal();
            User user;
            if (principal instanceof User) {
                user = (User) principal;
            } else {
                // As a fallback, load user from DB
                user = userRepository.findByEmail(loginDto.getEmail())
                        .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
            }

            // generate token using the JWTService (includes roles claim etc.)
            return jwtService.generateAccessToken(user);

        } catch (BadCredentialsException ex) {
            // rethrow so controller / exception handler can map to 401
            throw ex;
        } catch (AuthenticationException ex) {
            // convert other authentication exceptions to BadCredentials for simplicity
            throw new BadCredentialsException("Invalid credentials");
        }
    }
}
