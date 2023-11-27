package com.starchat.repository.impl;

import com.intersystems.jdbc.IRISConnection;
import com.intersystems.jdbc.IRISPreparedStatement;
import com.intersystems.jdbc.IRISResultSet;
import com.intersystems.jdbc.IRISStatement;
import com.starchat.model.UploadedFile;
import com.starchat.model.mappers.UploadedFileMapper;
import com.starchat.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileRepositoryImpl implements FileRepository {

    @Autowired
    private IRISConnection connection;

    @Override
    public UploadedFile addFile(UploadedFile file) {
        String sql = "INSERT INTO uploaded_files (file_name, file_path, file_type, file_size, uploaded_at) " +
                "VALUES (?, ?, ?, ?, ?)";

        try {
            connection.setAutoCommit(false);

            try (IRISPreparedStatement statement = (IRISPreparedStatement) connection.prepareStatement(sql, IRISStatement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, file.getFileName());
                statement.setString(2, file.getFilePath());
                statement.setString(3, file.getFileType());
                statement.setLong(4, file.getFileSize());
                statement.setTimestamp(5, Timestamp.valueOf(file.getUploadedAt()));

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected == 0) {
                    throw new RuntimeException("Error adding uploaded file with ID: " + file.getId());
                }

                try (IRISResultSet generatedKeys = (IRISResultSet) statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long generatedId = generatedKeys.getLong(1);
                        file.setId(generatedId);
                    } else {
                        throw new RuntimeException("Error retrieving generated keys for uploaded file");
                    }
                }
            }

            connection.commit();
            return file;

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Error rolling back transaction", rollbackException);
            }
            throw new RuntimeException("Error adding uploaded file with ID: " + file.getId(), e);

        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException resetException) {
                throw new RuntimeException("Error resetting auto-commit", resetException);
            }
        }
    }

    @Override
    public UploadedFile getFileById(Long id) {
        String sql = "SELECT * FROM uploaded_files WHERE id = ?";

        try (IRISPreparedStatement statement = (IRISPreparedStatement) connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (IRISResultSet resultSet = (IRISResultSet) statement.executeQuery()) {
                if (resultSet.next()) {
                    return UploadedFileMapper.map(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching file by ID: " + id, e);
        }

        return null;
    }

    @Override
    public List<UploadedFile> getFilesByMessageId(Long messageId) {
        String sql = "SELECT * FROM uploaded_files WHERE message_id = ?";
        List<UploadedFile> files = new ArrayList<>();

        try (IRISPreparedStatement statement = (IRISPreparedStatement) connection.prepareStatement(sql)) {
            statement.setLong(1, messageId);

            try (IRISResultSet resultSet = (IRISResultSet) statement.executeQuery()) {
                while (resultSet.next()) {
                    files.add(UploadedFileMapper.map(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching files per message: " + e);
        }

        return files;
    }

    @Override
    public void assignFileToMessage(Long id, Long messageId) {
        String sql = "UPDATE uploaded_files SET message_id = ? WHERE id = ?";

        try {
            connection.setAutoCommit(false);

            try (IRISPreparedStatement statement = (IRISPreparedStatement) connection.prepareStatement(sql)) {
                statement.setString(1, messageId.toString());
                statement.setLong(2, id);

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected == 0) {
                    System.out.println("No file found with ID: " + id);
                } else {
                    System.out.println("File with ID " + id + " updated successfully");
                }
            }

            connection.commit();

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Error rolling back transaction", rollbackException);
            }
            throw new RuntimeException("Error updating file with ID: " + id, e);

        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException resetException) {
                throw new RuntimeException("Error resetting auto-commit", resetException);
            }
        }
    }
}

