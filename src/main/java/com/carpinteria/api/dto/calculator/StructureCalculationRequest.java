package com.carpinteria.api.dto.calculator;

import jakarta.validation.constraints.Positive;

public class StructureCalculationRequest {

    @Positive(message = "El ancho de hueco de obra debe ser positivo")
    private double openingWidthMm;

    @Positive(message = "El alto de hueco de obra debe ser positivo")
    private double openingHeightMm;

    /**
     * Solape / holgura deduction per side (standard: 2.5mm per side -> total 5.0mm on width and height).
     */
    private Double solapePerSideMm = 2.5;

    /**
     * Material type: "ALUMINIUM" or "IRON".
     */
    private String profileMaterial = "ALUMINIUM";

    /**
     * Optional ID or reference to lookup from database.
     */
    private Long profileId;
    private String profileSeries;

    /**
     * Available commercial bar lengths in mm (e.g., 6000, 6500). If null, defaults to 6000mm.
     */
    private Double commercialBarLengthMm;

    public StructureCalculationRequest() {
    }

    public StructureCalculationRequest(double openingWidthMm, double openingHeightMm) {
        this.openingWidthMm = openingWidthMm;
        this.openingHeightMm = openingHeightMm;
        this.solapePerSideMm = 2.5;
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

    public Double getSolapePerSideMm() {
        return solapePerSideMm != null ? solapePerSideMm : 2.5;
    }

    public void setSolapePerSideMm(Double solapePerSideMm) {
        this.solapePerSideMm = solapePerSideMm;
    }

    public String getProfileMaterial() {
        return profileMaterial != null ? profileMaterial : "ALUMINIUM";
    }

    public void setProfileMaterial(String profileMaterial) {
        this.profileMaterial = profileMaterial;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public String getProfileSeries() {
        return profileSeries;
    }

    public void setProfileSeries(String profileSeries) {
        this.profileSeries = profileSeries;
    }

    public Double getCommercialBarLengthMm() {
        return commercialBarLengthMm;
    }

    public void setCommercialBarLengthMm(Double commercialBarLengthMm) {
        this.commercialBarLengthMm = commercialBarLengthMm;
    }
}
