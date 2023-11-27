package com.starchat.controller;

import com.starchat.model.ChatRoom;
import com.starchat.model.Message;
import com.starchat.model.dto.MessageDto;
import com.starchat.model.dto.UploadedFileDto;
import com.starchat.repository.ChatRoomRepository;
import com.starchat.repository.FileRepository;
import com.starchat.repository.MessageRepository;
import com.starchat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @MessageMapping("/chat")
    public void send(MessageDto messageDto) {
        Message message = new Message();
        message.setText(messageDto.getText());
        message.setChatRoomId(chatRoomRepository.getChatRoomById(messageDto.getChatId()).getId());
        message.setToUserId(userRepository.getUserByEmail(messageDto.getReceiverEmail()).getId());
        message.setFromUserId(userRepository.getUserByEmail(messageDto.getSenderEmail()).getId());
        message.setCreatedAt(messageDto.getCreatedAt());
        message = messageRepository.addMessage(message);
        chatRoomRepository.updateUpdatedAtById(message.getChatRoomId(), LocalDateTime.now());

        List<UploadedFileDto> files = messageDto.getFiles();
        if (files != null && files.size() > 0) {
            for (UploadedFileDto fileDto : files) {
                fileRepository.assignFileToMessage(fileDto.getId(), message.getId());
            }
        }

        messagingTemplate.convertAndSendToUser(
                messageDto.getReceiverEmail(),
                "/queue/messages",
                messageDto
        );
    }

    @PostMapping("/chat")
    public ResponseEntity handleChatPost(@RequestParam String fromEmail, @RequestParam String toEmail, @RequestParam String topic) {

        Long toUserId = userRepository.getUserIdByEmail(toEmail);

        if (toUserId == null) {
            return ResponseEntity.notFound().build();
        }
        Long fromUserId = userRepository.getUserIdByEmail(fromEmail);

        ChatRoom chat = new ChatRoom();
        chat.setTopic(topic);
        chat = chatRoomRepository.addChatRoom(chat);

        messageRepository.addMessage(new Message(chat.getId(), toUserId, fromUserId, "<i>" + "User " + fromEmail + " has created chat." + "</i>"));
        messageRepository.addMessage(new Message(chat.getId(), fromUserId, toUserId, "<i>" + "User " + toEmail + " has created chat." + "</i>"));
        chatRoomRepository.updateUpdatedAtById(chat.getId(), LocalDateTime.now());
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
