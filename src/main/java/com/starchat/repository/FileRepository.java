package com.starchat.repository;

import com.starchat.model.UploadedFile;

import java.util.List;

public interface FileRepository {

    UploadedFile addFile(UploadedFile file);

    UploadedFile getFileById(Long id);

    List<UploadedFile> getFilesByMessageId(Long messageId);

    void assignFileToMessage(Long id, Long messageId);
}
