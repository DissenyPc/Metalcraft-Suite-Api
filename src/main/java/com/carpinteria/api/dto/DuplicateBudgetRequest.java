package com.carpinteria.api.dto;

import java.time.LocalDate;

public class DuplicateBudgetRequest {

    /**
     * Optional custom budget reference number for the clone.
     * If null or empty, a generated reference like "{originalRef}-COPIA-{timestamp}" will be used.
     */
    private String newBudgetNumber;

    /**
     * Optional new title. If null, "{originalTitle} (Copia)" will be used.
     */
    private String newTitle;

    /**
     * Optional target client ID. If null, the original client will be retained.
     */
    private Long targetClientId;

    /**
     * Optional new issue date. Defaults to LocalDate.now() if null.
     */
    private LocalDate newIssueDate;

    /**
     * Validity in days from the new issue date. Default: 30 days.
     */
    private Integer validityDays = 30;

    public DuplicateBudgetRequest() {
    }

    public DuplicateBudgetRequest(String newBudgetNumber, String newTitle, Long targetClientId) {
        this.newBudgetNumber = newBudgetNumber;
        this.newTitle = newTitle;
        this.targetClientId = targetClientId;
    }

    public String getNewBudgetNumber() {
        return newBudgetNumber;
    }

    public void setNewBudgetNumber(String newBudgetNumber) {
        this.newBudgetNumber = newBudgetNumber;
    }

    public String getNewTitle() {
        return newTitle;
    }

    public void setNewTitle(String newTitle) {
        this.newTitle = newTitle;
    }

    public Long getTargetClientId() {
        return targetClientId;
    }

    public void setTargetClientId(Long targetClientId) {
        this.targetClientId = targetClientId;
    }

    public LocalDate getNewIssueDate() {
        return newIssueDate;
    }

    public void setNewIssueDate(LocalDate newIssueDate) {
        this.newIssueDate = newIssueDate;
    }

    public Integer getValidityDays() {
        return validityDays;
    }

    public void setValidityDays(Integer validityDays) {
        this.validityDays = validityDays;
    }
}
