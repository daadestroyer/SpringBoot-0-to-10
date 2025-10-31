package com.example.JWT.config;

import com.example.JWT.filter.JwtAuthenticationFilter;
import com.example.JWT.handler.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // if you store JWT in cookies, re-evaluate CSRF

                // allow session only when needed (OAuth2 needs it)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        // public endpoints
                        .requestMatchers("/public/**", "/auth/**").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**", "/oauth2/authorization/**","/home").permitAll()

                        // more specific: only ADMIN can create posts
                        .requestMatchers(HttpMethod.POST, "/posts/create-post").hasRole("ADMIN")

                        // allow GET (and other non-creating operations) to authenticated USER or ADMIN
                        .requestMatchers(HttpMethod.GET, "/posts/**").hasAnyRole("USER", "ADMIN")

                        // any other request must be authenticated
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2config ->
                        oauth2config
                                .failureUrl("/login?error=true")
                                .successHandler(oAuth2SuccessHandler) //
                )
                .logout(logout->logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .deleteCookies("refreshToken")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}