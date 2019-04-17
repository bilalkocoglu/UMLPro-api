package com.umlpro.api.controller;

import com.umlpro.api.dto.GeneralResponseDTO;
import com.umlpro.api.dto.GenerateRequestDTO;
import com.umlpro.api.dto.GenerateResponseDTO;
import com.umlpro.api.service.ClassesManagerService;
import com.umlpro.api.service.FolderManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = GenerateController.END_POINT)
public class GenerateController {
    public static final String END_POINT = "/generate";
    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateController.class);

    private FolderManagementService folderManagementService;
    private ClassesManagerService classesManagerService;

    GenerateController(FolderManagementService folderManagementService,
                       ClassesManagerService classesManagerService){
        this.folderManagementService = folderManagementService;
        this.classesManagerService = classesManagerService;
    }

    @PostMapping(
            value = ""
    )
    public ResponseEntity createClasses(@RequestBody GenerateRequestDTO generateRequestDTO) throws IOException {
        LOGGER.info(generateRequestDTO.toString());

        String zipName = classesManagerService.generateClasses(generateRequestDTO);

        if (zipName ==  null) {
            return ResponseEntity.badRequest().body(
                    GeneralResponseDTO
                            .builder()
                            .message("An error occurred during the operation. Please try again later.")
                            .build()
            );
        }

        Resource file = classesManagerService.getZip(zipName);

        if (file == null) {
            return ResponseEntity.badRequest().body(
                    GeneralResponseDTO
                            .builder()
                            .message("An error occurred during the operation. Please try again later.")
                            .build()
            );
        }
        LOGGER.info(file.getFilename());
        return ResponseEntity.ok().body(
                GenerateResponseDTO.builder().fileName(file.getFilename()).build()
        );
    }

    @PostMapping(
            value = "/zip",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GenerateResponseDTO> getZip(Model model, @RequestBody GenerateResponseDTO generateResponseDTO) {
        List<String> files = new ArrayList<String>();
        files.add(generateResponseDTO.getFileName());
        List<String> fileNames = files
                .stream().map(fileName -> MvcUriComponentsBuilder
                        .fromMethodName(GenerateController.class, "getFile", fileName).build().toString())
                .collect(Collectors.toList());
        LOGGER.info(fileNames.toString());
        return ResponseEntity.ok().body(
                GenerateResponseDTO.builder().fileName(fileNames.get(0)).build()
        );
    }

    @GetMapping("download/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = classesManagerService.getZip(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }


    @GetMapping(
            value = "/runService"
    )
    public ResponseEntity deneme(){
        //proje heroku üzerinde çalışacağından arayüz tarafında herhangi bir constructor içinde çağırılacak.
        //amaç kullanıcı uml diyagramını oluştururken arka planda herokunun projeyi ayağa kaldırması.
        LOGGER.info("Run Service");
        return ResponseEntity.ok("service run");
    }


}
