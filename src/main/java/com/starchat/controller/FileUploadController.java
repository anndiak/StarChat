package com.starchat.controller;
import com.starchat.model.UploadedFile;
import com.starchat.repository.FileRepository;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class FileUploadController {

    @Value("#{systemEnvironment['UPLOAD_DIR']}")
    private String UPLOAD_DIR;

    @Autowired
    private FileRepository fileRepository;

    @PostMapping("/upload")
    public ResponseEntity<List<UploadedFile>> handleFileUpload(@RequestParam("files") MultipartFile[] files) {
        List<String> uploadedFileNames = new ArrayList<>();
        List<UploadedFile> uploadedFiles = new ArrayList<>();

        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            for (MultipartFile file : files) {

                // Create a unique file name to avoid conflicts
                String uniqueFileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
                Path targetPath = Paths.get(UPLOAD_DIR, uniqueFileName);

                try (InputStream inputStream = file.getInputStream()) {

                    // Save the file to the target directory
                    Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    uploadedFileNames.add(uniqueFileName);
                }

                // Save file reference to the database
                UploadedFile uploadedFile = new UploadedFile();
                uploadedFile.setFilePath(targetPath.toString());
                uploadedFile.setFileName(uniqueFileName);
                uploadedFile.setFileSize(file.getSize());
                uploadedFile.setFileType(file.getContentType());
                uploadedFile.setUploadedAt(LocalDateTime.now());
                UploadedFile savedFile = fileRepository.addFile(uploadedFile);
                uploadedFiles.add(savedFile);
            }

            return ResponseEntity.ok(uploadedFiles);
        } catch (IOException ex) {
            throw new InternalException(ex.getMessage());
        }
    }
}