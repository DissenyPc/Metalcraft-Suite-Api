package com.carpinteria.api.controller;

import com.carpinteria.api.model.FillingMaterial;
import com.carpinteria.api.service.FillingMaterialService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.carpinteria.api.dto.csv.CsvImportResult;
import com.carpinteria.api.dto.pricing.BatchPriceAdjustmentRequest;
import com.carpinteria.api.dto.pricing.BatchPriceAdjustmentResult;
import com.carpinteria.api.service.csv.CsvImportService;
import com.carpinteria.api.service.pricing.PriceAdjustmentService;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/filling-materials")
public class FillingMaterialController {

    private final FillingMaterialService service;
    private final CsvImportService csvImportService;
    private final PriceAdjustmentService priceAdjustmentService;

    public FillingMaterialController(FillingMaterialService service,
                                     CsvImportService csvImportService,
                                     PriceAdjustmentService priceAdjustmentService) {
        this.service = service;
        this.csvImportService = csvImportService;
        this.priceAdjustmentService = priceAdjustmentService;
    }

    @PostMapping(value = "/import-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CsvImportResult> importCsv(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(csvImportService.importFillingMaterials(file));
    }

    @PostMapping("/bulk-price-update")
    public ResponseEntity<BatchPriceAdjustmentResult> bulkPriceUpdate(
            @Valid @RequestBody BatchPriceAdjustmentRequest request) {
        request.setTargetTable("FILLING_MATERIALS");
        return ResponseEntity.ok(priceAdjustmentService.adjustPrices(request));
    }

    @GetMapping
    public ResponseEntity<List<FillingMaterial>> getAll(
            @RequestParam(required = false) String materialType,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(service.getAll(materialType, search));
    }

    @GetMapping("/types")
    public ResponseEntity<List<String>> getMaterialTypes() {
        return ResponseEntity.ok(service.getMaterialTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FillingMaterial> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/reference/{ref}")
    public ResponseEntity<FillingMaterial> getByReference(@PathVariable String ref) {
        return ResponseEntity.ok(service.getByReference(ref));
    }

    @PostMapping
    public ResponseEntity<FillingMaterial> create(@Valid @RequestBody FillingMaterial item) {
        FillingMaterial created = service.create(item);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FillingMaterial> update(
            @PathVariable Long id,
            @Valid @RequestBody FillingMaterial item) {
        return ResponseEntity.ok(service.update(id, item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
