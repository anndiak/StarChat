package com.starchat.controller;

import com.intersystems.jdbc.IRIS;
import com.starchat.model.Message;
import com.starchat.model.User;
import com.starchat.model.dto.UserPresenceDto;
import com.starchat.repository.ChatRoomRepository;
import com.starchat.repository.MessageRepository;
import com.starchat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RequestMapping("/api")
@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private IRIS irisNative;

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

    @GetMapping("/user/activeness")
    public ResponseEntity<UserPresenceDto> getUserLoginStatus1(
            @RequestParam Long chatId,
            @RequestParam Long fromId) {

            Long toUserId = messageRepository.getAllMessagesByChatRoomId(chatId)
                    .stream()
                    .filter(m -> Objects.equals(m.getFromUserId(), fromId))
                    .findFirst().get().getToUserId();

        String email = userRepository.getUserById(toUserId).getEmail();
        Boolean status = irisNative.getBoolean("user", "isActive", email);
        UserPresenceDto userPresenceDto = new UserPresenceDto();
        userPresenceDto.setUserEmail(email);
        userPresenceDto.setOnline(status);
        String str = irisNative.getString("user", "LastLogin", email);
        userPresenceDto.setLastLogin(LocalDateTime.parse(str));

        return new ResponseEntity<>(userPresenceDto, HttpStatus.OK);
    }
}
