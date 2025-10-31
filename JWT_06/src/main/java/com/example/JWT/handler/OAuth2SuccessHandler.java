package com.example.JWT.handler;

import com.example.JWT.entity.Role;
import com.example.JWT.entity.User;
import com.example.JWT.service.JWTService;
import com.example.JWT.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final JWTService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) token.getPrincipal();
        log.info("OAuth2 User Email : " + oAuth2User.getAttribute("email"));

        // based on email check if user is present or not if not create their user and logged in him
        String email = oAuth2User.getAttribute("email");
        User user = userService.getUserByEmail(email);

        // means user is new and we need to register user
        if (user == null) {
            String randomPassword = UUID.randomUUID().toString();
            String encoded = passwordEncoder.encode(randomPassword);

            user = User
                    .builder()
                    .name(oAuth2User.getAttribute("name"))
                    .email(email)
                    .password(encoded)
                    .roles(Collections.singletonList(Role.USER))
                    .build();
            user = userService.saveUser(user);
        }
        // if in case we have user already registered, then we will create access token and refresh token
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // setting refresh token in cookie
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        // sending access token in the frontend also
        // redirects to frontend (example)
        String frontEndUrl = "http://localhost:9001/home?token=" + accessToken;
        getRedirectStrategy().sendRedirect(request, response, frontEndUrl);
    }
}
