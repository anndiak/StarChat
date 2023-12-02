package com.starchat.configuration;

import com.intersystems.jdbc.IRIS;
import com.starchat.model.UserPresence;
import com.starchat.repository.UserPresenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final SimpMessagingTemplate messagingTemplate;

    public CustomLogoutSuccessHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Autowired
    private UserPresenceRepository userPresenceRepository;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (authentication != null) {
            String userEmail = authentication.getName();
            System.out.println("User logged out: " + userEmail);

            UserPresence presenceUpdate = new UserPresence();
            presenceUpdate.setEmail(userEmail);
            presenceUpdate.setOnline(false);

            userPresenceRepository.updateUserPresenceStatusByEmail(false, userEmail);

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
