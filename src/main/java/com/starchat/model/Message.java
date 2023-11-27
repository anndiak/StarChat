package com.starchat.model;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor @AllArgsConstructor @Getter @Setter @ToString
public class Message {
    private Long id;
    private Long chatRoomId;
    private Long toUserId;
    private Long fromUserId;
    private String text;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Message(Long chatRoomId, Long toUserId, Long fromUserId, String text) {
        this.chatRoomId = chatRoomId;
        this.toUserId = toUserId;
        this.fromUserId = fromUserId;
        this.text = text;
    }
}