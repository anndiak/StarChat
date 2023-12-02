package com.starchat.configuration;

import com.intersystems.jdbc.IRIS;
import com.starchat.model.UserPresence;
import com.starchat.repository.UserPresenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserPresenceRepository userPresenceRepository;

    private final SimpMessagingTemplate messagingTemplate;

    public CustomLoginSuccessHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String userEmail = authentication.getName();
        System.out.println("User logged in: " + userEmail);

        UserPresence presenceUpdate = new UserPresence();
        presenceUpdate.setEmail(userEmail);
        presenceUpdate.setOnline(true);
        presenceUpdate.setLastLogin(LocalDateTime.now());

        if (userPresenceRepository.getUserPresenceByEmail(userEmail) == null) {
            userPresenceRepository.addUserPresence(presenceUpdate);
        } else {
            userPresenceRepository.updateUserPresenceStatusByEmail(true, userEmail);
            userPresenceRepository.updateUserPresenceLastLoginByEmail(LocalDateTime.now(), userEmail);
        }

        messagingTemplate.convertAndSendToUser(
                userEmail,
                "/queue/presence",
                presenceUpdate
        );

        // Redirect to the default target URL after successful login
        response.sendRedirect("/");
    }
}