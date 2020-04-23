package com.cheapflights.tickets.controller;

import com.cheapflights.tickets.domain.dto.ResponseMessageDTO;
import com.cheapflights.tickets.service.FileService;
import com.cheapflights.tickets.service.ImportDataService;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api/v1/import")
@Log
public class ImportDataController {

    private final ImportDataService importDataService;
    private final FileService fileService;

    public ImportDataController(ImportDataService newImportDataService, FileService fileService) {
        this.importDataService = newImportDataService;
        this.fileService = fileService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/airports")
    public ResponseEntity<ResponseMessageDTO> uploadAirports(@RequestParam(name = "file") MultipartFile file) {
        File savedFile = fileService.saveFile(file);
        importDataService.loadAirportsAndCities(savedFile)
                .exceptionally(this::asyncExceptionHandler);
        return ResponseEntity.ok(new ResponseMessageDTO("Parsing of " + file.getOriginalFilename() + " has started and it will take a couple of seconds up to a one minute [Async]."));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/routes")
    public ResponseEntity<ResponseMessageDTO> uploadRoutes(@RequestParam("file") MultipartFile file) {
        File savedFile = fileService.saveFile(file);
        importDataService.loadRoutes(savedFile)
                .exceptionally(this::asyncExceptionHandler);
        return ResponseEntity.ok(new ResponseMessageDTO("Parsing of " + file.getOriginalFilename() + " has started and it will take a couple of seconds up to a one minute [Async]."));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/status")
    public ResponseEntity<ResponseMessageDTO> checkFileStatus(@RequestParam("filename") String fileName) {
        return ResponseEntity.ok(new ResponseMessageDTO(fileService.getParsingStatus(fileName).toString()));
    }

    private Void asyncExceptionHandler(Throwable e) {
        log.severe(e.getMessage());
        return null;
    }
}
