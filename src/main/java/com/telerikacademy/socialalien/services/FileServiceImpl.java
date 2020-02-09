package com.telerikacademy.socialalien.services;

import com.telerikacademy.socialalien.configurations.FileStorageProperties;
import com.telerikacademy.socialalien.exceptions.EntityNotFoundException;
import com.telerikacademy.socialalien.models.User;
import com.telerikacademy.socialalien.services.contracts.FileService;
import com.telerikacademy.socialalien.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    private final Path rootLocation;
    private UserService userService;

    @Autowired
    public FileServiceImpl(FileStorageProperties properties, UserService userService) {
        this.rootLocation = Paths.get(properties.getLocation());
        try {
            createLocationIfMissing();
        } catch (IOException e) {
            throw new RuntimeException("Image storage service cannot be instantiated: " + e.getMessage());
        }
        copyFileToLocation("/static/images/profile-default.jpg", rootLocation.resolve("profile-default.jpg"));
        copyFileToLocation("/static/images/cover-default.jpg", rootLocation.resolve("cover-default.jpg"));
        this.userService = userService;
    }

    @RolesAllowed({"USER", "ADMIN"})
    @Transactional
    @Override
    public void store(MultipartFile file, int userId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException(String.format("Failed to save file %s: File is empty. ", file.getOriginalFilename()));
        }
        String filename = String.format("%s_%s", UUID.randomUUID().toString(), StringUtils.cleanPath(file.getOriginalFilename()));
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, this.rootLocation.resolve(filename),
                       StandardCopyOption.REPLACE_EXISTING);
        }
        User user = userService.getUserById(userId).orElseThrow(() -> new EntityNotFoundException(String.format("User with id % d does not exist.", userId)));
        user.setProfilePhoto(filename);
        userService.updateUser(user);
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    private void createLocationIfMissing() throws IOException {
        if(!rootLocation.toFile().exists()){
                Files.createDirectories(this.rootLocation);
        }
    }

    private void copyFileToLocation(String s, Path location) {
        try {
            InputStream is = getClass().getResourceAsStream(s);
            if(is != null) {
                Files.copy(is, location,StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            return;
        }

    }
}
