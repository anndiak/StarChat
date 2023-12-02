package com.starchat.configuration;

import com.intersystems.jdbc.IRIS;
import com.starchat.model.dto.UserPresenceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final SimpMessagingTemplate messagingTemplate;

    public CustomLogoutSuccessHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Autowired
    private IRIS irisNative;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (authentication != null) {
            String userEmail = authentication.getName();
            System.out.println("User logged out: " + userEmail);

            UserPresenceDto presenceUpdate = new UserPresenceDto();
            presenceUpdate.setUserEmail(userEmail);
            presenceUpdate.setOnline(false);

            irisNative.set(false, "user", "isActive", userEmail);

            messagingTemplate.convertAndSendToUser(
                    userEmail,
                    "/queue/presence",
                    presenceUpdate
            );
        }

        // Redirect to the default target URL after successful logout
        response.sendRedirect("/");
    }
}
