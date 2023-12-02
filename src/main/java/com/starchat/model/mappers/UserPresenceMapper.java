package com.starchat.model.mappers;

import com.intersystems.jdbc.IRISResultSet;
import com.starchat.model.UserPresence;

import java.sql.SQLException;

public class UserPresenceMapper {
    public static UserPresence map(IRISResultSet rs) throws SQLException {
        UserPresence userPresence = new UserPresence();
        userPresence.setEmail(rs.getString("email"));
        userPresence.setOnline(rs.getInt("is_online") == 1);
        userPresence.setLastLogin(rs.getTimestamp("last_login").toLocalDateTime());
        return userPresence;
    }
}
