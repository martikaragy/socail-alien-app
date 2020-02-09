package com.telerikacademy.socialalien.controllers;


import com.telerikacademy.socialalien.exceptions.EntityNotFoundException;
import com.telerikacademy.socialalien.services.contracts.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
public class ImageUploadController {

    private final FileService fileService;

    @Autowired
    public ImageUploadController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/files/{filename}")
    @ResponseBody
    public byte[] getImage(@PathVariable String filename) throws IOException {
        Path filePath = fileService.load(filename);
        if(filePath == null){
            throw new IOException("Empty file path.");
        }
        return Files.readAllBytes(filePath);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleMissingEntity() {
        return "notFound";
    }

}
