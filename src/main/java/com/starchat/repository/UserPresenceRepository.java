package com.starchat.repository;

import com.starchat.model.UserPresence;

import java.time.LocalDateTime;

public interface UserPresenceRepository {

    UserPresence getUserPresenceByEmail(String email);

    void updateUserPresenceStatusByEmail(boolean isActive, String email);

    void updateUserPresenceLastLoginByEmail(LocalDateTime dateTime, String email);

    UserPresence addUserPresence(UserPresence userPresence);
}
