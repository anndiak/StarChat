package com.starchat.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserPresenceDto {

    @JsonProperty("chatId")
    private String chatId;

    @JsonProperty("userEmail")
    private String userEmail;

    @JsonProperty("online")
    private boolean online;
}
