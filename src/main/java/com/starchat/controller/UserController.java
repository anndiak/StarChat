package com.starchat.controller;

import com.starchat.model.Message;
import com.starchat.model.User;
import com.starchat.repository.MessageRepository;
import com.starchat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api")
@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;


    public org.springframework.security.core.userdetails.User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            return (org.springframework.security.core.userdetails.User)auth.getPrincipal();
        }
        throw new ClassCastException("Principal is not of type User");
    }

    @RequestMapping(value = "me")
    public Long getAuthenticatedUserId(){
        return userRepository.getUserByEmail(getAuthenticatedUser().getUsername()).getId();
    }

    @RequestMapping(value = "me/email")
    public String getAuthenticatedUserEmail(){
        return getAuthenticatedUser().getUsername();
    }


    @RequestMapping(value = "/user_data")
    public ResponseEntity<User> getChatUserData(@RequestParam("chatId") Long chatId){
        Long meId = getAuthenticatedUserId();
        List<Message> messages = new ArrayList<>(messageRepository.getAllMessagesByChatRoomId(chatId));

        Message message = messages.get(0);

        User user = null;

        if (!meId.equals(message.getToUserId())) {
            user = userRepository.getUserById(message.getToUserId());
        } else if (!meId.equals(message.getFromUserId())) {
            user = userRepository.getUserById(message.getFromUserId());
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
