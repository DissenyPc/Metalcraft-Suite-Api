package com.carpinteria.api.dto.calculator;

import jakarta.validation.constraints.Positive;

public class GlazingCalculationRequest {

    @Positive(message = "El ancho del marco debe ser positivo")
    private double frameWidthMm;

    @Positive(message = "El alto del marco debe ser positivo")
    private double frameHeightMm;

    /**
     * Horizontal splits / leaf count (default: 2 for 2-leaf sliding window).
     */
    private int horizontalSplits = 2;

    /**
     * Vertical splits / rows (default: 1 for standard single-height window).
     */
    private int verticalSplits = 1;

    /**
     * Window configuration type: "SLIDING" (corredera), "CASEMENT" (practicable/abatible), "FIXED_GRID" (fijo con parteluces).
     */
    private String windowType = "SLIDING";

    /**
     * Outer frame profile visual width (default: 45mm).
     */
    private Double frameProfileWidthMm = 45.0;

    /**
     * Sash/leaf profile width (default: 50mm for sliding sash, 60mm for casement).
     */
    private Double sashProfileWidthMm = 50.0;

    /**
     * Overlap between sliding leaves (cruce central de hojas, default: 40mm).
     */
    private Double slidingOverlapMm = 40.0;

    /**
     * Intermediate dividing profile width (parteluz / travesaño, default: 40mm).
     */
    private Double transomWidthMm = 40.0;

    /**
     * Glass clearance / calzo de acristalamiento deduction in mm (standard: 4mm per side = 8mm total).
     */
    private Double glassClearanceMm = 8.0;

    /**
     * Optional ID of the FillingMaterial to quote from database.
     */
    private Long fillingMaterialId;

    public GlazingCalculationRequest() {
    }

    public GlazingCalculationRequest(double frameWidthMm, double frameHeightMm, int horizontalSplits, int verticalSplits) {
        this.frameWidthMm = frameWidthMm;
        this.frameHeightMm = frameHeightMm;
        this.horizontalSplits = horizontalSplits;
        this.verticalSplits = verticalSplits;
    }

    public double getFrameWidthMm() {
        return frameWidthMm;
    }

    public void setFrameWidthMm(double frameWidthMm) {
        this.frameWidthMm = frameWidthMm;
    }

    public double getFrameHeightMm() {
        return frameHeightMm;
    }

    public void setFrameHeightMm(double frameHeightMm) {
        this.frameHeightMm = frameHeightMm;
    }

    public int getHorizontalSplits() {
        return horizontalSplits > 0 ? horizontalSplits : 1;
    }

    public void setHorizontalSplits(int horizontalSplits) {
        this.horizontalSplits = horizontalSplits;
    }

    public int getVerticalSplits() {
        return verticalSplits > 0 ? verticalSplits : 1;
    }

    public void setVerticalSplits(int verticalSplits) {
        this.verticalSplits = verticalSplits;
    }

    public String getWindowType() {
        return windowType != null ? windowType.toUpperCase() : "SLIDING";
    }

    public void setWindowType(String windowType) {
        this.windowType = windowType;
    }

    public Double getFrameProfileWidthMm() {
        return frameProfileWidthMm != null ? frameProfileWidthMm : 45.0;
    }

    public void setFrameProfileWidthMm(Double frameProfileWidthMm) {
        this.frameProfileWidthMm = frameProfileWidthMm;
    }

    public Double getSashProfileWidthMm() {
        return sashProfileWidthMm != null ? sashProfileWidthMm : 50.0;
    }

    public void setSashProfileWidthMm(Double sashProfileWidthMm) {
        this.sashProfileWidthMm = sashProfileWidthMm;
    }

    public Double getSlidingOverlapMm() {
        return slidingOverlapMm != null ? slidingOverlapMm : 40.0;
    }

    public void setSlidingOverlapMm(Double slidingOverlapMm) {
        this.slidingOverlapMm = slidingOverlapMm;
    }

    public Double getTransomWidthMm() {
        return transomWidthMm != null ? transomWidthMm : 40.0;
    }

    public void setTransomWidthMm(Double transomWidthMm) {
        this.transomWidthMm = transomWidthMm;
    }

    public Double getGlassClearanceMm() {
        return glassClearanceMm != null ? glassClearanceMm : 8.0;
    }

    public void setGlassClearanceMm(Double glassClearanceMm) {
        this.glassClearanceMm = glassClearanceMm;
    }

    public Long getFillingMaterialId() {
        return fillingMaterialId;
    }

    public void setFillingMaterialId(Long fillingMaterialId) {
        this.fillingMaterialId = fillingMaterialId;
    }
}
