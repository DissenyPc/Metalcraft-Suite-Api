package com.carpinteria.api.dto.calculator;

import jakarta.validation.constraints.Positive;

public class MosquitoNetCalculationRequest {

    /**
     * Mosquito net type:
     * "SLIDING" (Corredera - derived from leaf dimensions)
     * "FIXED" (Fija con clip o grapas giratorias)
     * "ROLL_UP" (Enrollable vertical de cajón comercial)
     * "LATERAL_PLISADA" (Plisada lateral para puertas/balconeras)
     * "PRACTICABLE" (Abatible con marco y bisagras de muelle)
     */
    private String type = "SLIDING";

    @Positive(message = "El ancho de hueco debe ser positivo")
    private double openingWidthMm;

    @Positive(message = "El alto de hueco debe ser positivo")
    private double openingHeightMm;

    /**
     * Number of leaves of the window (default: 2, used for sliding net leaf sizing).
     */
    private int windowLeafCount = 2;

    /**
     * Overlap between sliding window leaves (default: 40mm).
     */
    private Double slidingOverlapMm = 40.0;

    /**
     * Runner track clearance deduction for sliding mosquito net (default: 10mm).
     */
    private Double runnerDeductionMm = 10.0;

    /**
     * Profile finish color (e.g., "BLANCO", "PLATA", "BRONCE", "RAL 7016").
     */
    private String profileColor = "BLANCO";

    public MosquitoNetCalculationRequest() {
    }

    public MosquitoNetCalculationRequest(String type, double openingWidthMm, double openingHeightMm) {
        this.type = type;
        this.openingWidthMm = openingWidthMm;
        this.openingHeightMm = openingHeightMm;
    }

    public String getType() {
        return type != null ? type.toUpperCase() : "SLIDING";
    }

    public void setType(String type) {
        this.type = type;
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

    public int getWindowLeafCount() {
        return windowLeafCount > 0 ? windowLeafCount : 2;
    }

    public void setWindowLeafCount(int windowLeafCount) {
        this.windowLeafCount = windowLeafCount;
    }

    public Double getSlidingOverlapMm() {
        return slidingOverlapMm != null ? slidingOverlapMm : 40.0;
    }

    public void setSlidingOverlapMm(Double slidingOverlapMm) {
        this.slidingOverlapMm = slidingOverlapMm;
    }

    public Double getRunnerDeductionMm() {
        return runnerDeductionMm != null ? runnerDeductionMm : 10.0;
    }

    public void setRunnerDeductionMm(Double runnerDeductionMm) {
        this.runnerDeductionMm = runnerDeductionMm;
    }

    public String getProfileColor() {
        return profileColor != null ? profileColor : "BLANCO";
    }

    public void setProfileColor(String profileColor) {
        this.profileColor = profileColor;
    }
}
