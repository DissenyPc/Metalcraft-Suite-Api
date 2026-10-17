package com.carpinteria.api.dto.calculator;

import jakarta.validation.constraints.Positive;

public class ShutterCalculationRequest {

    @Positive(message = "El ancho de hueco de obra debe ser positivo")
    private double openingWidthMm;

    @Positive(message = "El alto de hueco de obra debe ser positivo")
    private double openingHeightMm;

    /**
     * Compact box height in mm (cajón compacto).
     * Common sizes: 155, 185, 200 mm.
     * If null, auto-selected based on total opening height.
     */
    private Double boxHeightMm;

    /**
     * Winder type: "BUILT_IN" (empotrado en guía / pasacintas), "SURFACE" (abatible exterior), "MOTORIZED" (motor tubular).
     * Default: "BUILT_IN".
     */
    private String winderType = "BUILT_IN";

    /**
     * Deduction for built-in winder in mm. Standard: 30mm.
     */
    private Double builtInWinderDeductionMm = 30.0;

    /**
     * Slat model / type: "ALUMINIO_TERMICO_39", "ALUMINIO_TERMICO_43", "ALUMINIO_EXTRUSION_SEGURIDAD", "PVC_40".
     */
    private String slatType = "ALUMINIO_TERMICO_39";

    /**
     * Visible slat coverage height in mm (e.g., 39mm or 43mm).
     */
    private Double slatCoverHeightMm = 39.0;

    /**
     * Guide profile depth deduction per side (e.g. 20mm per side).
     */
    private Double guideDeductionMm = 40.0;

    public ShutterCalculationRequest() {
    }

    public ShutterCalculationRequest(double openingWidthMm, double openingHeightMm) {
        this.openingWidthMm = openingWidthMm;
        this.openingHeightMm = openingHeightMm;
        this.winderType = "BUILT_IN";
        this.builtInWinderDeductionMm = 30.0;
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

    public Double getBoxHeightMm() {
        return boxHeightMm;
    }

    public void setBoxHeightMm(Double boxHeightMm) {
        this.boxHeightMm = boxHeightMm;
    }

    public String getWinderType() {
        return winderType != null ? winderType : "BUILT_IN";
    }

    public void setWinderType(String winderType) {
        this.winderType = winderType;
    }

    public Double getBuiltInWinderDeductionMm() {
        return builtInWinderDeductionMm != null ? builtInWinderDeductionMm : 30.0;
    }

    public void setBuiltInWinderDeductionMm(Double builtInWinderDeductionMm) {
        this.builtInWinderDeductionMm = builtInWinderDeductionMm;
    }

    public String getSlatType() {
        return slatType != null ? slatType : "ALUMINIO_TERMICO_39";
    }

    public void setSlatType(String slatType) {
        this.slatType = slatType;
    }

    public Double getSlatCoverHeightMm() {
        return slatCoverHeightMm != null ? slatCoverHeightMm : 39.0;
    }

    public void setSlatCoverHeightMm(Double slatCoverHeightMm) {
        this.slatCoverHeightMm = slatCoverHeightMm;
    }

    public Double getGuideDeductionMm() {
        return guideDeductionMm != null ? guideDeductionMm : 40.0;
    }

    public void setGuideDeductionMm(Double guideDeductionMm) {
        this.guideDeductionMm = guideDeductionMm;
    }
}
