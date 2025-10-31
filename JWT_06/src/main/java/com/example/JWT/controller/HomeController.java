package com.example.JWT.controller;

import com.example.JWT.entity.User;
import com.example.JWT.service.JWTService;
import com.example.JWT.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
@RequiredArgsConstructor

public class HomeController {
    private final JWTService jwtService;
    private final UserService userService;
    @GetMapping("/home")
    public String home(@RequestParam("token") String token, Model model) {
        if (!jwtService.isTokenValid(token)) {
            throw new AccessDeniedException("Invalid or expired token");
        }

        Long userIdFromToken = jwtService.getUserIdFromToken(token);
        User user = userService.getUserById(userIdFromToken);
        String email = user.getEmail();
        String name = user.getName();

        model.addAttribute("email", email);
        model.addAttribute("name", name);

        return "home"; // points to templates/home.html
    }
}
