package com.carpinteria.api.dto.pricing;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BatchPriceAdjustmentResult {

    private String targetTable;
    private String filterApplied;
    private int updatedRecordsCount;
    private BigDecimal percentageApplied;
    private String priceFieldTargeted;
    private String message;
    private LocalDateTime timestamp;

    public BatchPriceAdjustmentResult() {
        this.timestamp = LocalDateTime.now();
    }

    public BatchPriceAdjustmentResult(String targetTable, String filterApplied, int updatedRecordsCount,
                                      BigDecimal percentageApplied, String priceFieldTargeted, String message) {
        this.targetTable = targetTable;
        this.filterApplied = filterApplied;
        this.updatedRecordsCount = updatedRecordsCount;
        this.percentageApplied = percentageApplied;
        this.priceFieldTargeted = priceFieldTargeted;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    public String getFilterApplied() {
        return filterApplied;
    }

    public void setFilterApplied(String filterApplied) {
        this.filterApplied = filterApplied;
    }

    public int getUpdatedRecordsCount() {
        return updatedRecordsCount;
    }

    public void setUpdatedRecordsCount(int updatedRecordsCount) {
        this.updatedRecordsCount = updatedRecordsCount;
    }

    public BigDecimal getPercentageApplied() {
        return percentageApplied;
    }

    public void setPercentageApplied(BigDecimal percentageApplied) {
        this.percentageApplied = percentageApplied;
    }

    public String getPriceFieldTargeted() {
        return priceFieldTargeted;
    }

    public void setPriceFieldTargeted(String priceFieldTargeted) {
        this.priceFieldTargeted = priceFieldTargeted;
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
