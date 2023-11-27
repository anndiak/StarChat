package com.starchat.configuration;

import com.starchat.model.dto.UserPresenceDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final SimpMessagingTemplate messagingTemplate;

    public CustomLoginSuccessHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String userEmail = authentication.getName();
        System.out.println("User logged in: " + userEmail);

        UserPresenceDto presenceUpdate = new UserPresenceDto();
        presenceUpdate.setUserEmail(userEmail);
        presenceUpdate.setOnline(true);

        messagingTemplate.convertAndSendToUser(
                userEmail,
                "/queue/presence",
                presenceUpdate
        );

        // Redirect to the default target URL after successful login
        response.sendRedirect("/");
    }
}