package com.carpinteria.api.dto.pricing;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class BatchPriceAdjustmentRequest {

    /**
     * Target table:
     * "ALUMINIUM_PROFILES"
     * "IRON_PROFILES"
     * "HARDWARE_ACCESSORIES"
     * "FILLING_MATERIALS"
     */
    @NotBlank(message = "La tabla objetivo es obligatoria (ALUMINIUM_PROFILES, IRON_PROFILES, HARDWARE_ACCESSORIES, FILLING_MATERIALS)")
    private String targetTable;

    /**
     * Optional filter (e.g., series name for profiles, category for hardware, materialType for filling).
     * If null, empty or "ALL", the price adjustment is applied across the ENTIRE table.
     */
    private String seriesFilter;

    /**
     * Percentage adjustment (e.g., +5.0 for +5%, -3.5 for -3.5%).
     */
    @NotNull(message = "El porcentaje es obligatorio")
    private BigDecimal percentage;

    /**
     * Specific price field or "ALL" (default).
     */
    private String priceField = "ALL";

    public BatchPriceAdjustmentRequest() {
    }

    public BatchPriceAdjustmentRequest(String targetTable, String seriesFilter, BigDecimal percentage) {
        this.targetTable = targetTable;
        this.seriesFilter = seriesFilter;
        this.percentage = percentage;
        this.priceField = "ALL";
    }

    public String getTargetTable() {
        return targetTable != null ? targetTable.toUpperCase() : null;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    public String getSeriesFilter() {
        return seriesFilter;
    }

    public void setSeriesFilter(String seriesFilter) {
        this.seriesFilter = seriesFilter;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public String getPriceField() {
        return priceField != null ? priceField.toUpperCase() : "ALL";
    }

    public void setPriceField(String priceField) {
        this.priceField = priceField;
    }
}
