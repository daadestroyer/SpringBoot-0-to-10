package com.example.JWT.filter;

import com.example.JWT.entity.User;
import com.example.JWT.service.JWTService;
import com.example.JWT.service.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = extractTokenFromBearerToken(request);

        // If no token found, continue filter chain (no authentication set)
        if (token == null) {
            filterChain.doFilter(request, response);
            return; // IMPORTANT: return after delegating to chain
        }

        try {
            // validate token (signature + expiration)
            if (!jwtService.isTokenValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            Long userId = jwtService.getUserIdFromToken(token);
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Option A: prefer roles from token (fast, no DB hit)
                List<String> roleNames = jwtService.getRolesFromToken(token);
                Collection<GrantedAuthority> authorities = roleNames.stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                        .collect(Collectors.toSet());

                // Optionally load the full user details from DB (keeps latest roles/state)
                User user = userService.getUserById(userId);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(user, null,
                                (authorities.isEmpty() ? user.getAuthorities() : authorities));

                // this contain details of user like IP Address, SessionID etc.
                // important for setting up rateLimiting or DDOS Attack
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException | IllegalArgumentException ex) {
            // Token invalid/expired/malformed: do not set authentication, continue the chain.
            // Optionally you can log here for debugging (avoid logging tokens).
            // logger.debug("Invalid JWT: {}", ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Resolve token from Authorization header ("Bearer <token>") or cookie "token".
     */
    private String extractTokenFromBearerToken(HttpServletRequest request) {
        // 1) Authorization header
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            // header is "Bearer <token>"
            return header.substring(7).trim();
        }

        // 2) Cookie fallback
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("token".equals(c.getName()) && c.getValue() != null && !c.getValue().isBlank()) {
                    return c.getValue();
                }
            }
        }

        return null;
    }
}