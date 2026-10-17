package com.carpinteria.api.dto;

import java.math.BigDecimal;

/**
 * DTO for executing bulk price updates on Aluminium or Iron profiles.
 * Can apply either a percentage change (+5.0 for +5%, -3.0 for -3%)
 * or a fixed delta addition (+0.50 €).
 * Can be restricted to a specific series or applied to the entire table.
 */
public class BulkPriceUpdateRequest {

    /**
     * Optional filter: series name (e.g., "Serie Europea 40", "Tubo Cuadrado").
     * If null or empty, updates all profiles in the table.
     */
    private String series;

    /**
     * Percentage adjustment (e.g. 5.0 for +5%, -10.0 for -10%).
     */
    private BigDecimal percentage;

    /**
     * Optional fixed delta addition (e.g. 0.50 for +0.50€).
     */
    private BigDecimal fixedAmount;

    /**
     * Target field to update:
     * "ALL" (default: all price fields),
     * "pricePerMeter",
     * "pricePerBar",
     * "pricePerKg" (Iron only).
     */
    private String targetPriceField = "ALL";

    public BulkPriceUpdateRequest() {
    }

    public BulkPriceUpdateRequest(String series, BigDecimal percentage, BigDecimal fixedAmount, String targetPriceField) {
        this.series = series;
        this.percentage = percentage;
        this.fixedAmount = fixedAmount;
        this.targetPriceField = targetPriceField != null ? targetPriceField : "ALL";
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public BigDecimal getFixedAmount() {
        return fixedAmount;
    }

    public void setFixedAmount(BigDecimal fixedAmount) {
        this.fixedAmount = fixedAmount;
    }

    public String getTargetPriceField() {
        return targetPriceField;
    }

    public void setTargetPriceField(String targetPriceField) {
        this.targetPriceField = targetPriceField;
    }
}
