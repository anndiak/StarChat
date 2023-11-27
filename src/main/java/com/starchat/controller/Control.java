package com.starchat.controller;

import com.starchat.model.ChatRoom;
import com.starchat.model.User;
import com.starchat.repository.ChatRoomRepository;
import com.starchat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Control {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity<List<User>> getAllUsers(){
        return new ResponseEntity(userRepository.getAllUsers(), HttpStatus.OK);
    }
    @RequestMapping(value = "/chat_room", method = RequestMethod.GET)
    public ResponseEntity<List<ChatRoom>> getAllChatRooms(){
        return new ResponseEntity(chatRoomRepository.getAllChatRooms(), HttpStatus.OK);
    }

}
