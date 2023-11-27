package com.starchat.model.mappers;

import com.intersystems.jdbc.IRISResultSet;
import com.starchat.model.Message;

import java.sql.SQLException;

public class MessageMapper {
    public static Message map(IRISResultSet rs) throws SQLException {
        Message message = new Message();
        message.setId(rs.getLong("id"));
        message.setChatRoomId(rs.getLong("chat_room_id"));
        message.setToUserId(rs.getLong("to_user_id"));
        message.setFromUserId(rs.getLong("from_user_id"));
        message.setText(rs.getString("text"));
        message.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return message;
    }
}
