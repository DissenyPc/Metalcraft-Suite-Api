package com.carpinteria.api.controller;

import com.carpinteria.api.dto.BulkPriceUpdateRequest;
import com.carpinteria.api.dto.BulkPriceUpdateResult;
import com.carpinteria.api.model.AluminiumProfile;
import com.carpinteria.api.service.AluminiumProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.carpinteria.api.dto.csv.CsvImportResult;
import com.carpinteria.api.service.csv.CsvImportService;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/profiles/aluminium")
public class AluminiumProfileController {

    private final AluminiumProfileService service;
    private final CsvImportService csvImportService;

    public AluminiumProfileController(AluminiumProfileService service, CsvImportService csvImportService) {
        this.service = service;
        this.csvImportService = csvImportService;
    }

    @PostMapping(value = "/import-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CsvImportResult> importCsv(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(csvImportService.importAluminiumProfiles(file));
    }

    @GetMapping
    public ResponseEntity<List<AluminiumProfile>> getAll(
            @RequestParam(required = false) String series,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(service.getAll(series, search));
    }

    @GetMapping("/series")
    public ResponseEntity<List<String>> getSeriesList() {
        return ResponseEntity.ok(service.getSeriesList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AluminiumProfile> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/reference/{ref}")
    public ResponseEntity<AluminiumProfile> getByReference(@PathVariable String ref) {
        return ResponseEntity.ok(service.getByReference(ref));
    }

    @PostMapping
    public ResponseEntity<AluminiumProfile> create(@Valid @RequestBody AluminiumProfile profile) {
        AluminiumProfile created = service.create(profile);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AluminiumProfile> update(
            @PathVariable Long id,
            @Valid @RequestBody AluminiumProfile profile) {
        return ResponseEntity.ok(service.update(id, profile));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Bulk price updates for Aluminium Profiles by series or entire table.
     * Supports percentage adjustment (+/- %) and/or fixed delta amount (+/- €).
     */
    @PostMapping("/bulk-price-update")
    public ResponseEntity<BulkPriceUpdateResult> bulkPriceUpdate(
            @RequestBody BulkPriceUpdateRequest request) {
        BulkPriceUpdateResult result = service.bulkUpdatePrices(request);
        return ResponseEntity.ok(result);
    }
}
