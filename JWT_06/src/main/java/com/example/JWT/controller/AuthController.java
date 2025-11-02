package com.example.JWT.controller;

import com.example.JWT.dto.JwtTokenResponse;
import com.example.JWT.dto.LoginDto;
import com.example.JWT.dto.SignUpDto;
import com.example.JWT.dto.UserDto;
import com.example.JWT.service.AuthService;
import com.example.JWT.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpDto signUpDto){
        log.info("Signup Dto details"+signUpDto);
        UserDto userDto =  authService.signUp(signUpDto);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto, HttpServletRequest request, HttpServletResponse response){
        JwtTokenResponse jwtTokenResponse = authService.login(loginDto);

        // setting refresh token in cookie also
        Cookie cookie = new Cookie("refreshToken", jwtTokenResponse.getRefreshToken());
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return new ResponseEntity<>(jwtTokenResponse,HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String refreshToken = Arrays
                .stream(cookies)
                .filter(cookie -> cookie.getName().equals("refreshToken"))
                .findFirst()
                .map(cookie -> cookie.getValue())
                .orElseThrow(() -> new AuthenticationServiceException("Refresh token not found inside the Cookies"));

        JwtTokenResponse jwtTokenResponse = authService.refreshToken(refreshToken);
        return new ResponseEntity<>(jwtTokenResponse,HttpStatus.OK);
    }
}
