package com.starchat.model.dto;

import com.starchat.model.UploadedFile;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MessageDto2 {
    private Long id;
    private Long chatRoomId;
    private Long toUserId;
    private Long fromUserId;
    private String text;
    private List<UploadedFile> files;
    private LocalDateTime createdAt = LocalDateTime.now();
}
