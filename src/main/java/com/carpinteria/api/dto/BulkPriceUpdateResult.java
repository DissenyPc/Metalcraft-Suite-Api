package com.carpinteria.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BulkPriceUpdateResult {

    private String profileType; // "ALUMINIUM" or "IRON"
    private String targetSeries;
    private int updatedCount;
    private BigDecimal percentageApplied;
    private BigDecimal fixedAmountApplied;
    private String targetPriceField;
    private String message;
    private LocalDateTime timestamp;

    public BulkPriceUpdateResult() {
        this.timestamp = LocalDateTime.now();
    }

    public BulkPriceUpdateResult(String profileType, String targetSeries, int updatedCount,
                                 BigDecimal percentageApplied, BigDecimal fixedAmountApplied,
                                 String targetPriceField, String message) {
        this.profileType = profileType;
        this.targetSeries = targetSeries;
        this.updatedCount = updatedCount;
        this.percentageApplied = percentageApplied;
        this.fixedAmountApplied = fixedAmountApplied;
        this.targetPriceField = targetPriceField;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getProfileType() {
        return profileType;
    }

    public void setProfileType(String profileType) {
        this.profileType = profileType;
    }

    public String getTargetSeries() {
        return targetSeries;
    }

    public void setTargetSeries(String targetSeries) {
        this.targetSeries = targetSeries;
    }

    public int getUpdatedCount() {
        return updatedCount;
    }

    public void setUpdatedCount(int updatedCount) {
        this.updatedCount = updatedCount;
    }

    public BigDecimal getPercentageApplied() {
        return percentageApplied;
    }

    public void setPercentageApplied(BigDecimal percentageApplied) {
        this.percentageApplied = percentageApplied;
    }

    public BigDecimal getFixedAmountApplied() {
        return fixedAmountApplied;
    }

    public void setFixedAmountApplied(BigDecimal fixedAmountApplied) {
        this.fixedAmountApplied = fixedAmountApplied;
    }

    public String getTargetPriceField() {
        return targetPriceField;
    }

    public void setTargetPriceField(String targetPriceField) {
        this.targetPriceField = targetPriceField;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
