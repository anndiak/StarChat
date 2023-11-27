package com.starchat.model.mappers;

import com.intersystems.jdbc.IRISResultSet;
import com.starchat.model.UploadedFile;

import java.sql.SQLException;

public class UploadedFileMapper {
    public static UploadedFile map(IRISResultSet rs) throws SQLException {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setId(rs.getLong("id"));
        uploadedFile.setMessageId(rs.getLong("message_id"));
        uploadedFile.setFileName(rs.getString("file_name"));
        uploadedFile.setFilePath(rs.getString("file_path"));
        uploadedFile.setFileType(rs.getString("file_type"));
        uploadedFile.setFileSize(rs.getLong("file_size"));
        uploadedFile.setUploadedAt(rs.getTimestamp("uploaded_at").toLocalDateTime());
        return uploadedFile;
    }
}

