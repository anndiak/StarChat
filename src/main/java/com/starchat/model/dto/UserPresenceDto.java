package com.starchat.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserPresenceDto {

    @JsonProperty("userEmail")
    private String userEmail;

    @JsonProperty("online")
    private boolean isOnline;

    @JsonProperty("lastLogin")
    private LocalDateTime lastLogin;
}
