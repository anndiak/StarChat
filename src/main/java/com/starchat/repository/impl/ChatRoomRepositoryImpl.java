package com.starchat.repository.impl;

import com.intersystems.jdbc.IRISConnection;
import com.intersystems.jdbc.IRISPreparedStatement;
import com.intersystems.jdbc.IRISResultSet;
import com.intersystems.jdbc.IRISStatement;
import com.starchat.model.ChatRoom;
import com.starchat.model.mappers.ChatRoomMapper;
import com.starchat.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ChatRoomRepositoryImpl implements ChatRoomRepository {

    @Autowired
    private IRISConnection connection;

    @Override
    public ChatRoom getChatRoomById(Long id) {
        String sql = "SELECT * FROM chat_rooms WHERE id = ?";

        try (IRISPreparedStatement statement = (IRISPreparedStatement) connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (IRISResultSet resultSet = (IRISResultSet) statement.executeQuery()) {
                if (resultSet.next()) {
                    return ChatRoomMapper.map(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching user by ID: " + id, e);
        }

        return null;
    }

    @Override
    public ChatRoom addChatRoom(ChatRoom chatRoom) {
        String sql = "INSERT INTO chat_rooms (topic, created_at, updated_at) " +
                "VALUES (?, ?, ?)";

        try {
            connection.setAutoCommit(false);

            try (IRISPreparedStatement statement = (IRISPreparedStatement) connection.prepareStatement(sql, IRISStatement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, chatRoom.getTopic());
                statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected == 0) {
                    throw new SQLException("Creating chat room failed, no rows affected.");
                }

                try (IRISResultSet generatedKeys = (IRISResultSet) statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long id = generatedKeys.getLong(1);
                        chatRoom.setId(id);
                    } else {
                        throw new SQLException("Creating chat room failed, no ID obtained.");
                    }
                }
            }

            connection.commit();

            return chatRoom;

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Error rolling back transaction", rollbackException);
            }
            throw new RuntimeException("Error adding chat room", e);

        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException resetException) {
                throw new RuntimeException("Error resetting auto-commit", resetException);
            }
        }
    }


    @Override
    public void deleteChatRoomById(Long id) {
        String sql = "DELETE FROM chat_rooms WHERE id = ?";

        try {
            connection.setAutoCommit(false);

            try (IRISPreparedStatement statement = (IRISPreparedStatement) connection.prepareStatement(sql)) {
                statement.setLong(1, id);

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected == 0) {
                    System.out.println("No char found with ID: " + id);
                } else {
                    System.out.println("Chat with ID " + id + " deleted successfully");
                }
            }

            connection.commit();

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Error rolling back transaction", rollbackException);
            }
            throw new RuntimeException("Error deleting chat with ID: " + id, e);

        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException resetException) {
                throw new RuntimeException("Error resetting auto-commit", resetException);
            }
        }
    }

    @Override
    public List<ChatRoom> getAllChatRooms() {
        String sql = "SELECT * FROM chat_rooms ORDER BY updated_at";
        List<ChatRoom> chatRooms = new ArrayList<>();

        try (IRISPreparedStatement statement = (IRISPreparedStatement) connection.prepareStatement(sql)) {

            try (IRISResultSet resultSet = (IRISResultSet) statement.executeQuery()) {
                if (resultSet.next()) {
                    chatRooms.add(ChatRoomMapper.map(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching chats: " + e);
        }

        return chatRooms;
    }

    @Override
    public void updateUpdatedAtById(Long id, LocalDateTime updatedAt) {
        String sql = "UPDATE chat_rooms SET updated_at = ? WHERE id = ?";

        try {
            connection.setAutoCommit(false);

            try (IRISPreparedStatement statement = (IRISPreparedStatement) connection.prepareStatement(sql)) {
                statement.setString(1, updatedAt.toString());
                statement.setLong(2, id);

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected == 0) {
                    System.out.println("No chatRoom found with ID: " + id);
                } else {
                    System.out.println("Chat with ID " + id + " updated successfully");
                }
            }

            connection.commit();

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Error rolling back transaction", rollbackException);
            }
            throw new RuntimeException("Error updating chat with ID: " + id, e);

        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException resetException) {
                throw new RuntimeException("Error resetting auto-commit", resetException);
            }
        }
    }
}
