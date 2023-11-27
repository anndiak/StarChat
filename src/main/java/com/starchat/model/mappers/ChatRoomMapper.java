package com.starchat.model.mappers;

import com.intersystems.jdbc.IRISResultSet;
import com.starchat.model.ChatRoom;

import java.sql.SQLException;

public class ChatRoomMapper {
    public static ChatRoom map(IRISResultSet rs) throws SQLException {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(rs.getLong("id"));
        chatRoom.setTopic(rs.getString("topic"));
        chatRoom.setCreated_at(rs.getTimestamp("created_at").toLocalDateTime());
        chatRoom.setUpdated_at(rs.getTimestamp("updated_at").toLocalDateTime());
        return chatRoom;
    }
}
