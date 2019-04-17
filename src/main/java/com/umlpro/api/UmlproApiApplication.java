package com.umlpro.api;

import com.umlpro.api.service.FolderManagementService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.annotation.Resource;

@CrossOrigin
@SpringBootApplication
public class UmlproApiApplication implements CommandLineRunner {

	@Resource
	FolderManagementService folderManagementService;

	public static void main(String[] args) {
		SpringApplication.run(UmlproApiApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		folderManagementService.deleteAll();
		folderManagementService.createMainFalder();
	}
}
