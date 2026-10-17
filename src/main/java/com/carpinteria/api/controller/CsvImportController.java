package com.carpinteria.api.controller;

import com.carpinteria.api.dto.csv.CsvImportResult;
import com.carpinteria.api.service.csv.CsvImportService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/import/csv")
public class CsvImportController {

    private final CsvImportService csvService;

    public CsvImportController(CsvImportService csvService) {
        this.csvService = csvService;
    }

    @PostMapping(value = "/aluminium-profiles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CsvImportResult> importAluminiumProfiles(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(csvService.importAluminiumProfiles(file));
    }

    @PostMapping(value = "/iron-profiles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CsvImportResult> importIronProfiles(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(csvService.importIronProfiles(file));
    }

    @PostMapping(value = "/hardware-accessories", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CsvImportResult> importHardwareAccessories(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(csvService.importHardwareAccessories(file));
    }

    @PostMapping(value = "/filling-materials", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CsvImportResult> importFillingMaterials(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(csvService.importFillingMaterials(file));
    }
}
