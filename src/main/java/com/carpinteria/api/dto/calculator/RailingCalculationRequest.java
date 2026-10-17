package com.carpinteria.api.dto.calculator;

import jakarta.validation.constraints.Positive;

public class RailingCalculationRequest {

    @Positive(message = "La longitud de la barandilla debe ser positiva")
    private double lengthMm;

    @Positive(message = "La altura de la barandilla debe ser positiva")
    private double heightMm = 1000.0; // Standard 1000mm or 1100mm per CTE

    /**
     * Handrail type:
     * "ROUND_TUBE_50" (Tubo redondo 50mm)
     * "MOLDED_60X20" (Pasamanos moldurado de hierro o aluminio)
     * "RECTANGULAR_60X30" (Tubo rectangular 60x30mm)
     * "NONE" (Sin pasamanos especial / tubo estándar)
     */
    private String handrailType = "ROUND_TUBE_50";

    /**
     * Infill vertical bar thickness in mm (e.g. 16mm or 20mm).
     */
    private Double infillBarThicknessMm = 16.0;

    /**
     * Maximum clear spacing between vertical infill bars (standard CTE: <= 100mm or 130mm).
     */
    private Double maxBarSpacingMm = 100.0;

    /**
     * Maximum spacing between main structural posts/pillars in mm (standard: 1000mm - 1200mm).
     */
    private Double maxPostSpacingMm = 1000.0;

    /**
     * Post profile size in mm (e.g. 40mm for 40x40 tube).
     */
    private Double postProfileSizeMm = 40.0;

    /**
     * Fixing type: "SURFACE_PLATE" (Placa de anclaje base para atornillar con taco químico),
     * "EMBEDDED" (Embutido en forjado con taladro y mortero/resina),
     * "SIDE_FACADE" (Anclaje lateral a zanco/forjado con pletinas).
     */
    private String fixingType = "SURFACE_PLATE";

    public RailingCalculationRequest() {
    }

    public RailingCalculationRequest(double lengthMm, double heightMm, String handrailType) {
        this.lengthMm = lengthMm;
        this.heightMm = heightMm;
        this.handrailType = handrailType;
    }

    public double getLengthMm() {
        return lengthMm;
    }

    public void setLengthMm(double lengthMm) {
        this.lengthMm = lengthMm;
    }

    public double getHeightMm() {
        return heightMm > 0 ? heightMm : 1000.0;
    }

    public void setHeightMm(double heightMm) {
        this.heightMm = heightMm;
    }

    public String getHandrailType() {
        return handrailType != null ? handrailType.toUpperCase() : "ROUND_TUBE_50";
    }

    public void setHandrailType(String handrailType) {
        this.handrailType = handrailType;
    }

    public Double getInfillBarThicknessMm() {
        return infillBarThicknessMm != null ? infillBarThicknessMm : 16.0;
    }

    public void setInfillBarThicknessMm(Double infillBarThicknessMm) {
        this.infillBarThicknessMm = infillBarThicknessMm;
    }

    public Double getMaxBarSpacingMm() {
        return maxBarSpacingMm != null ? maxBarSpacingMm : 100.0;
    }

    public void setMaxBarSpacingMm(Double maxBarSpacingMm) {
        this.maxBarSpacingMm = maxBarSpacingMm;
    }

    public Double getMaxPostSpacingMm() {
        return maxPostSpacingMm != null ? maxPostSpacingMm : 1000.0;
    }

    public void setMaxPostSpacingMm(Double maxPostSpacingMm) {
        this.maxPostSpacingMm = maxPostSpacingMm;
    }

    public Double getPostProfileSizeMm() {
        return postProfileSizeMm != null ? postProfileSizeMm : 40.0;
    }

    public void setPostProfileSizeMm(Double postProfileSizeMm) {
        this.postProfileSizeMm = postProfileSizeMm;
    }

    public String getFixingType() {
        return fixingType != null ? fixingType.toUpperCase() : "SURFACE_PLATE";
    }

    public void setFixingType(String fixingType) {
        this.fixingType = fixingType;
    }
}
