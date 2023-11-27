package com.starchat.repository.impl;

import com.intersystems.jdbc.IRISConnection;
import com.intersystems.jdbc.IRISPreparedStatement;
import com.intersystems.jdbc.IRISResultSet;
import com.starchat.configuration.PasswordEncoder;
import com.starchat.model.User;
import com.starchat.model.mappers.UserMapper;
import com.starchat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private IRISConnection connection;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User getUserById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (IRISPreparedStatement statement = (IRISPreparedStatement) connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (IRISResultSet resultSet = (IRISResultSet) statement.executeQuery()) {
                if (resultSet.next()) {
                    return UserMapper.map(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching user by ID: " + id, e);
        }

        return null;
    }


    @Override
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (IRISPreparedStatement statement = (IRISPreparedStatement) connection.prepareStatement(sql)) {
            statement.setString(1, email);

            try (IRISResultSet resultSet = (IRISResultSet) statement.executeQuery()) {
                if (resultSet.next()) {
                    return UserMapper.map(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching user by email: " + email, e);
        }

        return null;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        List<User> userList = new ArrayList<>();

        try (IRISPreparedStatement statement = (IRISPreparedStatement) connection.prepareStatement(sql)) {

            try (IRISResultSet resultSet = (IRISResultSet) statement.executeQuery()) {
                if (resultSet.next()) {
                    userList.add(UserMapper.map(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching users: " + e);
        }

        return userList;
    }

    @Override
    public Long getUserIdByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (IRISPreparedStatement statement = (IRISPreparedStatement) connection.prepareStatement(sql)) {
            statement.setString(1, email);

            try (IRISResultSet resultSet = (IRISResultSet) statement.executeQuery()) {
                if (resultSet.next()) {
                    return UserMapper.map(resultSet).getId();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching users: " + e);
        }

        return null;
    }

    @Override
    public void addUser(User user) {
        String sql = "INSERT INTO users (email, phone, first_name, last_name, user_role, password, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            connection.setAutoCommit(false);

            try (IRISPreparedStatement statement = (IRISPreparedStatement) connection.prepareStatement(sql)) {
                statement.setString(1, user.getEmail());
                statement.setString(2, user.getPhone());
                statement.setString(3, user.getFirstName());
                statement.setString(4, user.getLastName());
                statement.setInt(5, user.getRole().getValue());
                statement.setString(6, passwordEncoder.bCryptPasswordEncoder().encode(user.getPassword()));
                statement.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
                statement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));

                statement.executeUpdate();
            }

            connection.commit();

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Error rolling back transaction", rollbackException);
            }
            throw new RuntimeException("Error adding user with ID: " + user.getId(), e);

        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException resetException) {
                throw new RuntimeException("Error resetting auto-commit", resetException);
            }
        }
    }


    @Override
    public void deleteUserById(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try {
            connection.setAutoCommit(false);

            try (IRISPreparedStatement statement = (IRISPreparedStatement) connection.prepareStatement(sql)) {
                statement.setLong(1, id);

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected == 0) {
                    System.out.println("No user found with ID: " + id);
                } else {
                    System.out.println("User with ID " + id + " deleted successfully");
                }
            }

            connection.commit();

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Error rolling back transaction", rollbackException);
            }
            throw new RuntimeException("Error deleting user with ID: " + id, e);

        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException resetException) {
                throw new RuntimeException("Error resetting auto-commit", resetException);
            }
        }
    }

}
