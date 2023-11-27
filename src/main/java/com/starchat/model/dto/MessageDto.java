package com.starchat.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MessageDto {

    @JsonProperty("chatId")
    private Long chatId;

    @JsonProperty("text")
    private String text;

    @JsonProperty("senderEmail")
    private String senderEmail;

    @JsonProperty("receiverEmail")
    private String receiverEmail;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonProperty("files")
    private List<UploadedFileDto> files;
}
