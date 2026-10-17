package com.carpinteria.api.dto.calculator;

import java.util.ArrayList;
import java.util.List;

public class FixedModuleCalculationResult {

    private String seriesType;
    private String modulePosition;

    private double totalOpeningWidthMm;
    private double totalOpeningHeightMm;

    // Dimensions of the main operating window
    private double mainWindowWidthMm;
    private double mainWindowHeightMm;

    // Dimensions of the fixed module
    private double fixedModuleWidthMm;
    private double fixedModuleHeightMm;

    // Specific profiles
    private Double transomLengthMm;
    private String transomDescription;

    private Double couplingHProfileLengthMm;
    private String couplingProfileDescription;

    private List<ProfileCutItem> frameCuts = new ArrayList<>();
    private String notes;

    public FixedModuleCalculationResult() {
    }

    public String getSeriesType() {
        return seriesType;
    }

    public void setSeriesType(String seriesType) {
        this.seriesType = seriesType;
    }

    public String getModulePosition() {
        return modulePosition;
    }

    public void setModulePosition(String modulePosition) {
        this.modulePosition = modulePosition;
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

    public double getMainWindowWidthMm() {
        return mainWindowWidthMm;
    }

    public void setMainWindowWidthMm(double mainWindowWidthMm) {
        this.mainWindowWidthMm = mainWindowWidthMm;
    }

    public double getMainWindowHeightMm() {
        return mainWindowHeightMm;
    }

    public void setMainWindowHeightMm(double mainWindowHeightMm) {
        this.mainWindowHeightMm = mainWindowHeightMm;
    }

    public double getFixedModuleWidthMm() {
        return fixedModuleWidthMm;
    }

    public void setFixedModuleWidthMm(double fixedModuleWidthMm) {
        this.fixedModuleWidthMm = fixedModuleWidthMm;
    }

    public double getFixedModuleHeightMm() {
        return fixedModuleHeightMm;
    }

    public void setFixedModuleHeightMm(double fixedModuleHeightMm) {
        this.fixedModuleHeightMm = fixedModuleHeightMm;
    }

    public Double getTransomLengthMm() {
        return transomLengthMm;
    }

    public void setTransomLengthMm(Double transomLengthMm) {
        this.transomLengthMm = transomLengthMm;
    }

    public String getTransomDescription() {
        return transomDescription;
    }

    public void setTransomDescription(String transomDescription) {
        this.transomDescription = transomDescription;
    }

    public Double getCouplingHProfileLengthMm() {
        return couplingHProfileLengthMm;
    }

    public void setCouplingHProfileLengthMm(Double couplingHProfileLengthMm) {
        this.couplingHProfileLengthMm = couplingHProfileLengthMm;
    }

    public String getCouplingProfileDescription() {
        return couplingProfileDescription;
    }

    public void setCouplingProfileDescription(String couplingProfileDescription) {
        this.couplingProfileDescription = couplingProfileDescription;
    }

    public List<ProfileCutItem> getFrameCuts() {
        return frameCuts;
    }

    public void setFrameCuts(List<ProfileCutItem> frameCuts) {
        this.frameCuts = frameCuts;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
