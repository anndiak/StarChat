package com.starchat.model;

import lombok.*;

import java.time.LocalDateTime;


@NoArgsConstructor @AllArgsConstructor @Getter @Setter @ToString
public class UploadedFile {
    private Long id;
    private Long messageId;
    private String fileName;
    private String filePath;
    private String fileType;
    private long fileSize;
    private LocalDateTime uploadedAt;
}
