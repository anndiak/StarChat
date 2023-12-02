package com.starchat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserPresence {

    @JsonProperty("userEmail")
    private String email;

    @JsonProperty("online")
    private boolean isOnline;

    @JsonProperty("lastLogin")
    private LocalDateTime lastLogin;
}
