package com.cheapflights.tickets.controller;

import com.cheapflights.tickets.service.ImportDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/import")
public class ImportDataController {

    private final ImportDataService importDataService;

    public ImportDataController(ImportDataService newImportDataService) {
        this.importDataService = newImportDataService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/airports")
    public ResponseEntity uploadAirports(@RequestParam("file") MultipartFile file) {
        importDataService.loadAirports(file);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/routes")
    public ResponseEntity uploadRoutes(@RequestParam("file") MultipartFile file) {
        importDataService.loadRoutes(file);
        return ResponseEntity.ok().build();
    }
}
