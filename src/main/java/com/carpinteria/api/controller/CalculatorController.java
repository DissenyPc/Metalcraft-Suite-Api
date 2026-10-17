package com.carpinteria.api.controller;

import com.carpinteria.api.dto.calculator.*;
import com.carpinteria.api.service.calculator.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calculator")
public class CalculatorController {

    private final StructureCalculationService structureService;
    private final ShutterCalculationService shutterService;
    private final FixedModuleCalculationService fixedModuleService;
    private final GlazingCalculationService glazingService;
    private final SpecialProductsService specialProductsService;

    public CalculatorController(StructureCalculationService structureService,
                                ShutterCalculationService shutterService,
                                FixedModuleCalculationService fixedModuleService,
                                GlazingCalculationService glazingService,
                                SpecialProductsService specialProductsService) {
        this.structureService = structureService;
        this.shutterService = shutterService;
        this.fixedModuleService = fixedModuleService;
        this.glazingService = glazingService;
        this.specialProductsService = specialProductsService;
    }

    /**
     * 1. Structure Calculation Service:
     * Opening measurements (hueco obra), overlap deductions (-2.5mm per side), closest commercial stock bar calculation.
     */
    @PostMapping("/structure")
    public ResponseEntity<StructureCalculationResult> calculateStructure(
            @Valid @RequestBody StructureCalculationRequest request) {
        return ResponseEntity.ok(structureService.calculate(request));
    }

    /**
     * 2. Built-in Shutter (Persiana compacto / monoblock) Service:
     * Applies width deductions (-30mm for built-in winder) and compact box height adjustments.
     */
    @PostMapping("/shutter")
    public ResponseEntity<ShutterCalculationResult> calculateShutter(
            @Valid @RequestBody ShutterCalculationRequest request) {
        return ResponseEntity.ok(shutterService.calculate(request));
    }

    /**
     * 3. Fixed Modules (Módulos fijos) Service:
     * Perimeter series (shared outer frame + dividing transom T)
     * and Non-perimeter series (independent frames with H coupling profile deductions/additions).
     */
    @PostMapping("/fixed-modules")
    public ResponseEntity<FixedModuleCalculationResult> calculateFixedModule(
            @Valid @RequestBody FixedModuleCalculationRequest request) {
        return ResponseEntity.ok(fixedModuleService.calculate(request));
    }

    /**
     * 4. Lights / Vanos (Glazing / Filling) Service:
     * Dimensions and divisions based on horizontal/vertical split counts, clearances, gasket perimeter, and glazing beads.
     */
    @PostMapping("/glazing")
    public ResponseEntity<GlazingCalculationResult> calculateGlazing(
            @Valid @RequestBody GlazingCalculationRequest request) {
        return ResponseEntity.ok(glazingService.calculate(request));
    }

    /**
     * 5. Special Products - Grilles (Rejas de Hierro y Aluminio):
     * Dynamic bar count (spacing <= 130mm), fixing types (embedded +100mm per side, screwed with 4 angles).
     */
    @PostMapping("/grille")
    public ResponseEntity<GrilleCalculationResult> calculateGrille(
            @Valid @RequestBody GrilleCalculationRequest request) {
        return ResponseEntity.ok(specialProductsService.calculateGrille(request));
    }

    /**
     * 5. Special Products - Railings (Barandillas):
     * Handrail types, posts, bottom rail, base plates, safety infill bars (spacing <= 100mm/130mm per CTE).
     */
    @PostMapping("/railing")
    public ResponseEntity<RailingCalculationResult> calculateRailing(
            @Valid @RequestBody RailingCalculationRequest request) {
        return ResponseEntity.ok(specialProductsService.calculateRailing(request));
    }

    /**
     * 5. Special Products - Mosquito Nets (Mosquiteras):
     * Sliding nets (derived from leaf dimensions), fixed nets, and commercial roll-up, lateral pleated, or practicable kits.
     */
    @PostMapping("/mosquito-net")
    public ResponseEntity<MosquitoNetCalculationResult> calculateMosquitoNet(
            @Valid @RequestBody MosquitoNetCalculationRequest request) {
        return ResponseEntity.ok(specialProductsService.calculateMosquitoNet(request));
    }
}
