package com.umlpro.api.service;

import com.umlpro.api.dto.GenerateRequestDTO;

import com.umlpro.api.languageSupport.CSharpSupport;
import com.umlpro.api.languageSupport.JavaSupport;
import com.umlpro.api.languageSupport.PhpSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;

@Service
public class ClassesManagerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassesManagerService.class);
    private int count = 0;

    private JavaSupport javaSupport;
    private CSharpSupport cSharpSupport;
    private PhpSupport phpSupport;

    ClassesManagerService(JavaSupport javaSupport,
                          CSharpSupport cSharpSupport,
                          PhpSupport phpSupport) {
        this.javaSupport = javaSupport;
        this.cSharpSupport = cSharpSupport;
        this.phpSupport = phpSupport;
    }

    public String generateClasses(GenerateRequestDTO generateRequestDTO) {
        try {
            count++;
            String zipName = "classes" + count + ".zip";

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("deneme");

            File file = new File(String.valueOf(FolderManagementService.rootLocation.resolve(zipName)));

            if (generateRequestDTO.getLanguage().equals("java")) {
                javaSupport.createClasses(file, generateRequestDTO);
            } else if (generateRequestDTO.getLanguage().equals("cSharp")) {
                cSharpSupport.createClasses(file, generateRequestDTO);
            } else if (generateRequestDTO.getLanguage().equals("php")) {
                phpSupport.createClasses(file, generateRequestDTO);
            }

            return zipName;
        }catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public Resource getZip(String name) {
        try {
            Path file = FolderManagementService.rootLocation.resolve(name);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                LOGGER.error("Resource ERROR !");
                return null;
            }
        }catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }
}
