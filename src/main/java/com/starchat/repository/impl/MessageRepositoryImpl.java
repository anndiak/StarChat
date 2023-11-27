package com.starchat.repository.impl;

import com.intersystems.jdbc.IRISConnection;
import com.intersystems.jdbc.IRISPreparedStatement;
import com.intersystems.jdbc.IRISResultSet;
import com.intersystems.jdbc.IRISStatement;
import com.starchat.model.Message;
import com.starchat.model.mappers.MessageMapper;
import com.starchat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class MessageRepositoryImpl implements MessageRepository {

    @Autowired
    private IRISConnection connection;

    @Override
    public Message getMessageById(Long id) {
        String sql = "SELECT * FROM messages WHERE id = ?";

        try (IRISPreparedStatement statement = (IRISPreparedStatement) connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (IRISResultSet resultSet = (IRISResultSet) statement.executeQuery()) {
                if (resultSet.next()) {
                    return MessageMapper.map(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching user by ID: " + id, e);
        }

        return null;
    }

    @Override
    public Message addMessage(Message message) {
        String sql = "INSERT INTO messages (chat_room_id, from_user_id, to_user_id, text, created_at) " +
                "VALUES (?, ?, ?, ?, ?)";

        try {
            connection.setAutoCommit(false);

            try (IRISPreparedStatement statement = (IRISPreparedStatement) connection.prepareStatement(sql, IRISStatement.RETURN_GENERATED_KEYS)) {
                statement.setLong(1, message.getChatRoomId());
                statement.setLong(2, message.getFromUserId());
                statement.setLong(3, message.getToUserId());
                statement.setString(4, message.getText());
                statement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected == 0) {
                    throw new SQLException("Creating message failed, no rows affected.");
                }

                try (IRISResultSet generatedKeys = (IRISResultSet) statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long id = generatedKeys.getLong(1);
                        message.setId(id);
                    } else {
                        throw new SQLException("Creating message failed, no ID obtained.");
                    }
                }
            }

            connection.commit();

            return message;

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Error rolling back transaction", rollbackException);
            }
            throw new RuntimeException("Error adding message", e);

        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException resetException) {
                throw new RuntimeException("Error resetting auto-commit", resetException);
            }
        }
    }

    @Override
    public List<Message> getAllMessagesByChatRoomId(Long id) {
        String sql = "SELECT * FROM messages WHERE chat_room_id = ? ORDER BY created_at ASC";
        List<Message> messages = new ArrayList<>();

        try (IRISPreparedStatement statement = (IRISPreparedStatement) connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (IRISResultSet resultSet = (IRISResultSet) statement.executeQuery()) {
                while (resultSet.next()) {
                    messages.add(MessageMapper.map(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching messages: " + e);
        }

        return messages;
    }

    @Override
    public List<Long> getUserChatIds(Long id) {
        String sql = "SELECT DISTINCT chat_room_id FROM messages WHERE from_user_id = ? OR to_user_id = ? ORDER BY created_at DESC";

        List<Long> chatRoomIds = new ArrayList<>();

        try (IRISPreparedStatement statement = (IRISPreparedStatement) connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.setLong(2, id);

            try (IRISResultSet resultSet = (IRISResultSet) statement.executeQuery()) {
                while (resultSet.next()) {
                    chatRoomIds.add(resultSet.getLong("chat_room_id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching chat room IDs: " + e);
        }

        return chatRoomIds;
    }
}
