package com.carpinteria.api.controller;

import com.carpinteria.api.dto.pricing.BatchPriceAdjustmentRequest;
import com.carpinteria.api.dto.pricing.BatchPriceAdjustmentResult;
import com.carpinteria.api.service.pricing.PriceAdjustmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pricing")
public class PriceAdjustmentController {

    private final PriceAdjustmentService priceAdjustmentService;

    public PriceAdjustmentController(PriceAdjustmentService priceAdjustmentService) {
        this.priceAdjustmentService = priceAdjustmentService;
    }

    /**
     * Batch Price Adjustment Endpoint:
     * Increases or decreases prices (+/- %) across an entire table
     * or filtered by a specific series/category.
     */
    @PostMapping("/batch-adjust")
    public ResponseEntity<BatchPriceAdjustmentResult> batchAdjustPrices(
            @Valid @RequestBody BatchPriceAdjustmentRequest request) {
        return ResponseEntity.ok(priceAdjustmentService.adjustPrices(request));
    }
}
