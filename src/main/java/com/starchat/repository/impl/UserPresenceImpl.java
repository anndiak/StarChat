package com.starchat.repository.impl;

import com.intersystems.jdbc.IRISConnection;
import com.intersystems.jdbc.IRISPreparedStatement;
import com.intersystems.jdbc.IRISResultSet;
import com.intersystems.jdbc.IRISStatement;
import com.starchat.model.UserPresence;
import com.starchat.model.mappers.UserPresenceMapper;
import com.starchat.repository.UserPresenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class UserPresenceImpl implements UserPresenceRepository {

    @Autowired
    private IRISConnection connection;

    @Override
    public UserPresence getUserPresenceByEmail(String email) {
        String sql = "SELECT * FROM user_presences WHERE email = ?";

        try (IRISPreparedStatement statement = (IRISPreparedStatement) connection.prepareStatement(sql)) {
            statement.setString(1, email);

            try (IRISResultSet resultSet = (IRISResultSet) statement.executeQuery()) {
                if (resultSet.next()) {
                    return UserPresenceMapper.map(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching user by email: " + email, e);
        }

        return null;
    }

    @Override
    public void updateUserPresenceStatusByEmail(boolean isActive, String email) {
        String sql = "UPDATE user_presences SET is_online = ? WHERE email = ?";

        try {
            connection.setAutoCommit(false);

            try (IRISPreparedStatement statement = (IRISPreparedStatement) connection.prepareStatement(sql)) {
                statement.setInt(1, isActive ? 1 : 0);
                statement.setString(2, email);

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected == 0) {
                    System.out.println("No userPresence found with email: " + email);
                } else {
                    System.out.println("userPresence with email " + email + " updated successfully");
                }
            }

            connection.commit();

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Error rolling back transaction", rollbackException);
            }
            throw new RuntimeException("Error updating userPresence with email: " + email, e);

        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException resetException) {
                throw new RuntimeException("Error resetting auto-commit", resetException);
            }
        }
    }

    @Override
    public void updateUserPresenceLastLoginByEmail(LocalDateTime dateTime, String email) {
        String sql = "UPDATE user_presences SET last_login = ? WHERE email = ?";

        try {
            connection.setAutoCommit(false);

            try (IRISPreparedStatement statement = (IRISPreparedStatement) connection.prepareStatement(sql)) {
                statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                statement.setString(2, email);

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected == 0) {
                    System.out.println("No userPresence found with email: " + email);
                } else {
                    System.out.println("userPresence with email " + email + " updated successfully");
                }
            }

            connection.commit();

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Error rolling back transaction", rollbackException);
            }
            throw new RuntimeException("Error updating userPresence with email: " + email, e);

        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException resetException) {
                throw new RuntimeException("Error resetting auto-commit", resetException);
            }
        }
    }

    @Override
    public UserPresence addUserPresence(UserPresence userPresence) {
        String sql = "INSERT INTO user_presences (email, is_online, last_login) " +
                "VALUES (?, ?, ?)";

        try {
            connection.setAutoCommit(false);

            try (IRISPreparedStatement statement = (IRISPreparedStatement) connection.prepareStatement(sql, IRISStatement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, userPresence.getEmail());
                statement.setInt(2, userPresence.isOnline() ? 1 : 0);
                statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected == 0) {
                    throw new SQLException("Creating user presence failed, no rows affected.");
                }
            }

            connection.commit();

            return userPresence;

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
}
