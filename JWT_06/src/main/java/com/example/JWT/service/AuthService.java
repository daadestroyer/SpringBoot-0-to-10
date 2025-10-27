package com.example.JWT.service;

import com.example.JWT.dto.LoginDto;
import com.example.JWT.dto.SignUpDto;
import com.example.JWT.dto.UserDto;
import com.example.JWT.entity.User;
import com.example.JWT.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public UserDto signUp(SignUpDto signUpDto) {
        Optional<User> optionalUser = userRepository.findByEmail(signUpDto.getEmail());
        if(optionalUser.isPresent()){
            throw new BadCredentialsException("User with email "+signUpDto.getEmail()+" already present");
        }
        User userToCreate = modelMapper.map(signUpDto, User.class);
        userToCreate.setPassword(passwordEncoder.encode(userToCreate.getPassword()));
        User savedUser = userRepository.save(userToCreate);
        return modelMapper.map(savedUser,UserDto.class);
    }

    public String login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        User user = (User)authentication.getPrincipal();
        return jwtService.generateAccessToken(user);
    }
}
