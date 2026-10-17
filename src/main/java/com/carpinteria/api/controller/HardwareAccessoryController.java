package com.carpinteria.api.controller;

import com.carpinteria.api.model.HardwareAccessory;
import com.carpinteria.api.service.HardwareAccessoryService;
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
@RequestMapping("/api/hardware-accessories")
public class HardwareAccessoryController {

    private final HardwareAccessoryService service;
    private final CsvImportService csvImportService;
    private final PriceAdjustmentService priceAdjustmentService;

    public HardwareAccessoryController(HardwareAccessoryService service,
                                       CsvImportService csvImportService,
                                       PriceAdjustmentService priceAdjustmentService) {
        this.service = service;
        this.csvImportService = csvImportService;
        this.priceAdjustmentService = priceAdjustmentService;
    }

    @PostMapping(value = "/import-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CsvImportResult> importCsv(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(csvImportService.importHardwareAccessories(file));
    }

    @PostMapping("/bulk-price-update")
    public ResponseEntity<BatchPriceAdjustmentResult> bulkPriceUpdate(
            @Valid @RequestBody BatchPriceAdjustmentRequest request) {
        request.setTargetTable("HARDWARE_ACCESSORIES");
        return ResponseEntity.ok(priceAdjustmentService.adjustPrices(request));
    }

    @GetMapping
    public ResponseEntity<List<HardwareAccessory>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(service.getAll(category, search));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(service.getCategories());
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<HardwareAccessory>> getLowStockAlerts() {
        return ResponseEntity.ok(service.getLowStockAlerts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HardwareAccessory> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/reference/{ref}")
    public ResponseEntity<HardwareAccessory> getByReference(@PathVariable String ref) {
        return ResponseEntity.ok(service.getByReference(ref));
    }

    @PostMapping
    public ResponseEntity<HardwareAccessory> create(@Valid @RequestBody HardwareAccessory item) {
        HardwareAccessory created = service.create(item);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HardwareAccessory> update(
            @PathVariable Long id,
            @Valid @RequestBody HardwareAccessory item) {
        return ResponseEntity.ok(service.update(id, item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
