package com.example.JWT.filter;

import com.example.JWT.entity.User;
import com.example.JWT.service.JWTService;
import com.example.JWT.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JWTService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestTokenHeader = request.getHeader("Authorization");
        // "Bearer asfsfsfsfsfsf"

        if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
        }
        String token = requestTokenHeader.split("Bearer ")[1];
        Long userId = jwtService.getUserIdFromToken(token);

        // only proceed inside if userId is not null and user details is not already present in SecurityContext
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            User user = userService.getUserById(userId);

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), null, null);

            // this contain details of user like IP Address, SessionID etc.
            // important for setting up rateLimiting or DDOS Attack
            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
        filterChain.doFilter(request, response);
    }
}
