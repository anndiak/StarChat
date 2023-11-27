package com.starchat.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UploadedFileDto {
    private Long id;
    private String fileName;
    private String fileType;
    private long fileSize;
    private LocalDateTime uploadedAt;
}
