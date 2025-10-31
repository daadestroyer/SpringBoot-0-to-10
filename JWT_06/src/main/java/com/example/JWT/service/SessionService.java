package com.example.JWT.service;

import com.example.JWT.entity.Session;
import com.example.JWT.entity.User;
import com.example.JWT.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {
    private final SessionRepository sessionRepository;
    private final int SESSION_LIMIT = 2;

    public void generateNewSession(User user, String refreshToken) {
        // get all the session for this user
        List<Session> userSession = sessionRepository.findByUser(user);
        if (userSession.size() == 2) {
            // reached the session limit, so remove least recently used session
            userSession.sort(Comparator.comparing(Session::getLastUsedAt));

            Session leastRecentlyUsedSession = userSession.getFirst();
            log.info("Deleted least recently used session");
            sessionRepository.delete(leastRecentlyUsedSession);
        }
        // else we are less than the session limit so create one session
        Session newSession = Session.builder()
                .user(user)
                .refreshToken(refreshToken)
//                .lastUsedAt(LocalDateTime.now())
                .build();
        log.info("We are less than session limit so creating new session for this user");
        sessionRepository.save(newSession);
    }

    public void validateSession(String refreshToken) {
        Session session = sessionRepository
                .findByRefreshToken(refreshToken)
                .orElseThrow(() -> new SessionAuthenticationException("Session not found for refreshToken"));
        log.info("User session validated");
        // updating last used time when every time we are using refresh token

        session.setLastUsedAt(LocalDateTime.now());
        sessionRepository.save(session);
        log.info("Saved last used session");
    }
}
