package com.cheapflights.tickets.controller;

import com.cheapflights.tickets.domain.dto.ResponseMessageDTO;
import com.cheapflights.tickets.service.ImportDataService;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api/v1/import")
@Log
public class ImportDataController {

    private final ImportDataService importDataService;

    public ImportDataController(ImportDataService newImportDataService) {
        this.importDataService = newImportDataService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/airports")
    public ResponseEntity<ResponseMessageDTO> uploadAirports(@RequestParam(name = "file") MultipartFile file) {
        File savedFile = importDataService.saveFile(file);
        importDataService.loadAirportsAndCities(savedFile)
                .exceptionally(this::asyncExceptionHandler);
        return ResponseEntity.ok(new ResponseMessageDTO("Parsing of " + file.getOriginalFilename() + " has started and it will take a couple of seconds up to a one minute [Async]."));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/routes")
    public ResponseEntity<ResponseMessageDTO> uploadRoutes(@RequestParam("file") MultipartFile file) {
        File savedFile = importDataService.saveFile(file);
        importDataService.loadRoutes(savedFile)
                .exceptionally(this::asyncExceptionHandler);
        return ResponseEntity.ok(new ResponseMessageDTO("Parsing of " + file.getOriginalFilename() + " has started and it will take a couple of seconds up to a one minute [Async]."));
    }

    private Void asyncExceptionHandler(Throwable e) {
        log.severe(e.getMessage());
        return null;
    }
}
