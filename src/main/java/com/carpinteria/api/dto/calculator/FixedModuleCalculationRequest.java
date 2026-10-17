package com.carpinteria.api.dto.calculator;

import jakarta.validation.constraints.Positive;

public class FixedModuleCalculationRequest {

    @Positive(message = "El ancho total debe ser positivo")
    private double totalOpeningWidthMm;

    @Positive(message = "El alto total debe ser positivo")
    private double totalOpeningHeightMm;

    /**
     * Position of the fixed module: "TOP" (fijo superior/montante), "BOTTOM" (fijo inferior),
     * "LATERAL_LEFT" (fijo lateral izquierdo), "LATERAL_RIGHT" (fijo lateral derecho).
     */
    private String modulePosition = "TOP";

    /**
     * Dimension of the fixed section in mm (height if TOP/BOTTOM, width if LATERAL).
     */
    @Positive(message = "La dimensión del fijo debe ser positiva")
    private double fixedDimensionMm;

    /**
     * Series type:
     * "PERIMETER" (serie perimetral con marco exterior compartido y travesaño/parteluz T)
     * "NON_PERIMETER" (serie no perimetral con marcos independientes y perfil de unión H).
     */
    private String seriesType = "PERIMETER";

    /**
     * Width of intermediate dividing transom profile ('T') for perimeter series (default: 40mm).
     */
    private Double transomProfileWidthMm = 40.0;

    /**
     * Thickness of H-coupling profile between independent frames (default: 6mm).
     */
    private Double couplingProfileWidthMm = 6.0;

    /**
     * Main frame profile border width (default: 45mm).
     */
    private Double frameProfileWidthMm = 45.0;

    public FixedModuleCalculationRequest() {
    }

    public FixedModuleCalculationRequest(double totalOpeningWidthMm, double totalOpeningHeightMm,
                                         String modulePosition, double fixedDimensionMm, String seriesType) {
        this.totalOpeningWidthMm = totalOpeningWidthMm;
        this.totalOpeningHeightMm = totalOpeningHeightMm;
        this.modulePosition = modulePosition;
        this.fixedDimensionMm = fixedDimensionMm;
        this.seriesType = seriesType;
    }

    public double getTotalOpeningWidthMm() {
        return totalOpeningWidthMm;
    }

    public void setTotalOpeningWidthMm(double totalOpeningWidthMm) {
        this.totalOpeningWidthMm = totalOpeningWidthMm;
    }

    public double getTotalOpeningHeightMm() {
        return totalOpeningHeightMm;
    }

    public void setTotalOpeningHeightMm(double totalOpeningHeightMm) {
        this.totalOpeningHeightMm = totalOpeningHeightMm;
    }

    public String getModulePosition() {
        return modulePosition != null ? modulePosition.toUpperCase() : "TOP";
    }

    public void setModulePosition(String modulePosition) {
        this.modulePosition = modulePosition;
    }

    public double getFixedDimensionMm() {
        return fixedDimensionMm;
    }

    public void setFixedDimensionMm(double fixedDimensionMm) {
        this.fixedDimensionMm = fixedDimensionMm;
    }

    public String getSeriesType() {
        return seriesType != null ? seriesType.toUpperCase() : "PERIMETER";
    }

    public void setSeriesType(String seriesType) {
        this.seriesType = seriesType;
    }

    public Double getTransomProfileWidthMm() {
        return transomProfileWidthMm != null ? transomProfileWidthMm : 40.0;
    }

    public void setTransomProfileWidthMm(Double transomProfileWidthMm) {
        this.transomProfileWidthMm = transomProfileWidthMm;
    }

    public Double getCouplingProfileWidthMm() {
        return couplingProfileWidthMm != null ? couplingProfileWidthMm : 6.0;
    }

    public void setCouplingProfileWidthMm(Double couplingProfileWidthMm) {
        this.couplingProfileWidthMm = couplingProfileWidthMm;
    }

    public Double getFrameProfileWidthMm() {
        return frameProfileWidthMm != null ? frameProfileWidthMm : 45.0;
    }

    public void setFrameProfileWidthMm(Double frameProfileWidthMm) {
        this.frameProfileWidthMm = frameProfileWidthMm;
    }
}
