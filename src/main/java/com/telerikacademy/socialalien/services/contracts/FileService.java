package com.telerikacademy.socialalien.services.contracts;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface FileService {

    void store(MultipartFile file, int userId) throws IOException;
    Path load(String filename);
    void deleteAll();

}
