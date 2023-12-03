package com.starchat.controller;

import com.intersystems.jdbc.IRIS;
import com.starchat.model.ChatRoom;
import com.starchat.model.Message;
import com.starchat.model.dto.MessageDto2;
import com.starchat.repository.ChatRoomRepository;
import com.starchat.repository.FileRepository;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private IRIS irisNative;

    public org.springframework.security.core.userdetails.User getAuthenticatedUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (org.springframework.security.core.userdetails.User)auth.getPrincipal();
    }

    @RequestMapping("/messages")
    public ResponseEntity<List<MessageDto2>> getMessages(@RequestParam("room") Long roomId,
                                                     @RequestParam("offset") int offset,
                                                     @RequestParam("limit") int limit){
        List<MessageDto2> messages = messageRepository.getAllMessagesByChatRoomId(roomId)
                .stream()
                .map(this::convertToMessageDto2)
                .collect(Collectors.toList());

        int fromIndex = messages.size() - limit - offset;
        int toIndex = messages.size() - offset;

        if (fromIndex < 0 && toIndex < 0) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }

        if (fromIndex < 0 && toIndex >= 0) {
            fromIndex = 0;
            messages = messages.subList(fromIndex, toIndex);
        }

        if (fromIndex <= toIndex && fromIndex > 0) {
            messages = messages.subList(fromIndex, toIndex);
        }

        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @RequestMapping("/user_chats")
    public ResponseEntity<List<ChatRoom>> getChats(){
        Long meId = userRepository.getUserByEmail(getAuthenticatedUser().getUsername()).getId();
        List<ChatRoom> userChats = messageRepository.getUserChatIds(meId)
                .stream()
                .map(chatId -> chatRoomRepository.getChatRoomById(chatId))
                .collect(Collectors.toList());

        return new ResponseEntity<>(userChats, HttpStatus.OK);
    }

    public  MessageDto2 convertToMessageDto2(Message message) {
        MessageDto2 messageDto2 = new MessageDto2();
        messageDto2.setId(message.getId());
        messageDto2.setChatRoomId(message.getChatRoomId());
        messageDto2.setToUserId(message.getToUserId());
        messageDto2.setFromUserId(message.getFromUserId());
        messageDto2.setText(message.getText());
        messageDto2.setCreatedAt(message.getCreatedAt());

        messageDto2.setFiles(fileRepository.getFilesByMessageId(message.getId()));

        return messageDto2;
    }

    @RequestMapping("/chatgpt")
    public ResponseEntity<String> getChatGptResponse(@RequestParam("request") String request) {
        String response = irisNative.classMethodString("%Net.Remote.Gateway", "%RemoteService", "localhost", 55555, "ChatGPT", request);
        return ResponseEntity.ok(response);
    }
}
