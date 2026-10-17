package com.carpinteria.api.dto.csv;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CsvImportResult {

    private String targetTable;
    private int totalRowsProcessed;
    private int insertedCount;
    private int updatedCount;
    private int errorCount;
    private List<String> errorMessages = new ArrayList<>();
    private String message;
    private LocalDateTime timestamp;

    public CsvImportResult() {
        this.timestamp = LocalDateTime.now();
    }

    public CsvImportResult(String targetTable) {
        this.targetTable = targetTable;
        this.timestamp = LocalDateTime.now();
    }

    public String getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    public int getTotalRowsProcessed() {
        return totalRowsProcessed;
    }

    public void setTotalRowsProcessed(int totalRowsProcessed) {
        this.totalRowsProcessed = totalRowsProcessed;
    }

    public int getInsertedCount() {
        return insertedCount;
    }

    public void setInsertedCount(int insertedCount) {
        this.insertedCount = insertedCount;
    }

    public int getUpdatedCount() {
        return updatedCount;
    }

    public void setUpdatedCount(int updatedCount) {
        this.updatedCount = updatedCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public void addErrorMessage(String error) {
        this.errorMessages.add(error);
        this.errorCount = this.errorMessages.size();
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
