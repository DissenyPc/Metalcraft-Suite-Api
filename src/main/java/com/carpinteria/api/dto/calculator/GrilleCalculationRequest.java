package com.carpinteria.api.dto.calculator;

import jakarta.validation.constraints.Positive;

public class GrilleCalculationRequest {

    @Positive(message = "El ancho de hueco de obra debe ser positivo")
    private double openingWidthMm;

    @Positive(message = "El alto de hueco de obra debe ser positivo")
    private double openingHeightMm;

    /**
     * Material: "IRON" (Hierro / Acero) or "ALUMINIUM" (Aluminio).
     */
    private String material = "IRON";

    /**
     * Fixing type:
     * "EMBEDDED" (Embutida / Recibida de obra con garrochas: agrega +100mm por lado = +200mm a ancho y/o alto)
     * "SCREWED" (Atornillada / Solapada: agrega 4 escuadras de fijación)
     * "INTERNAL_FRAME" (Cerco dentro de hueco con fijación química o tornillo inviolable)
     */
    private String fixingType = "SCREWED";

    /**
     * Bar thickness/diameter in mm (e.g., 16mm para redondo de 16, 20mm para tubo cuadrado de 20).
     */
    private Double barThicknessMm = 16.0;

    /**
     * Maximum allowed spacing between bars (security code standard: <= 130mm).
     */
    private Double maxSpacingMm = 130.0;

    /**
     * Overlap/depth added per side if embedded (default: 100mm per side).
     */
    private Double embeddedAdditionPerSideMm = 100.0;

    /**
     * Perimeter frame profile width in mm (e.g. 40mm for 40x20 tube or 40x40 angle).
     */
    private Double frameProfileWidthMm = 40.0;

    /**
     * Add intermediate reinforcing transom if height exceeds 1500mm (null = auto).
     */
    private Boolean includeReinforcingTransom;

    public GrilleCalculationRequest() {
    }

    public GrilleCalculationRequest(double openingWidthMm, double openingHeightMm, String material, String fixingType) {
        this.openingWidthMm = openingWidthMm;
        this.openingHeightMm = openingHeightMm;
        this.material = material;
        this.fixingType = fixingType;
    }

    public double getOpeningWidthMm() {
        return openingWidthMm;
    }

    public void setOpeningWidthMm(double openingWidthMm) {
        this.openingWidthMm = openingWidthMm;
    }

    public double getOpeningHeightMm() {
        return openingHeightMm;
    }

    public void setOpeningHeightMm(double openingHeightMm) {
        this.openingHeightMm = openingHeightMm;
    }

    public String getMaterial() {
        return material != null ? material.toUpperCase() : "IRON";
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getFixingType() {
        return fixingType != null ? fixingType.toUpperCase() : "SCREWED";
    }

    public void setFixingType(String fixingType) {
        this.fixingType = fixingType;
    }

    public Double getBarThicknessMm() {
        return barThicknessMm != null ? barThicknessMm : 16.0;
    }

    public void setBarThicknessMm(Double barThicknessMm) {
        this.barThicknessMm = barThicknessMm;
    }

    public Double getMaxSpacingMm() {
        return maxSpacingMm != null ? maxSpacingMm : 130.0;
    }

    public void setMaxSpacingMm(Double maxSpacingMm) {
        this.maxSpacingMm = maxSpacingMm;
    }

    public Double getEmbeddedAdditionPerSideMm() {
        return embeddedAdditionPerSideMm != null ? embeddedAdditionPerSideMm : 100.0;
    }

    public void setEmbeddedAdditionPerSideMm(Double embeddedAdditionPerSideMm) {
        this.embeddedAdditionPerSideMm = embeddedAdditionPerSideMm;
    }

    public Double getFrameProfileWidthMm() {
        return frameProfileWidthMm != null ? frameProfileWidthMm : 40.0;
    }

    public void setFrameProfileWidthMm(Double frameProfileWidthMm) {
        this.frameProfileWidthMm = frameProfileWidthMm;
    }

    public Boolean getIncludeReinforcingTransom() {
        return includeReinforcingTransom;
    }

    public void setIncludeReinforcingTransom(Boolean includeReinforcingTransom) {
        this.includeReinforcingTransom = includeReinforcingTransom;
    }
}
