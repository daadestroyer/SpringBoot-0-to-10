package com.example.JWT.service;

import com.example.JWT.dto.JwtTokenResponse;
import com.example.JWT.dto.LoginDto;
import com.example.JWT.dto.SignUpDto;
import com.example.JWT.dto.UserDto;
import com.example.JWT.entity.Role;
import com.example.JWT.entity.User;
import com.example.JWT.repository.UserRepository;
import com.sun.jdi.event.ExceptionEvent;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final SessionService sessionService;

    public UserDto signUp(SignUpDto signUpDto) {
        if (userRepository.findByEmail(signUpDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email " + signUpDto.getEmail() + " already exists");
        }
        User user = User.builder()
                .email(signUpDto.getEmail())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .name(signUpDto.getName())
                .roles(signUpDto.getRoles())
                .build();

        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDto.class);
    }

    public JwtTokenResponse login(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );
            User user = (User) authentication.getPrincipal();
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            sessionService.generateNewSession(user,refreshToken);
            return new JwtTokenResponse(user.getId(),accessToken,refreshToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public JwtTokenResponse refreshToken(String refreshToken) {
        // check if the refresh token is valid or not
        Long userId = jwtService.getUserIdFromToken(refreshToken);

        // check if session is valid or not(by checking if refresh token is present in db or not we can check)
        sessionService.validateSession(refreshToken);

        User user = userService.getUserById(userId);
        String accessToken = jwtService.generateAccessToken(user);
        return new JwtTokenResponse(user.getId(),accessToken,refreshToken);
    }
}
