package com.umlpro.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FolderManagementService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FolderManagementService.class);
    public static final Path rootLocation = Paths.get("classes-dir");

    public void createMainFalder() {
        try {
            Files.createDirectory(rootLocation);
        }catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }
}
