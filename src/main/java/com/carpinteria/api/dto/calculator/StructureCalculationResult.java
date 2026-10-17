package com.carpinteria.api.dto.calculator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StructureCalculationResult {

    private double openingWidthMm;
    private double openingHeightMm;
    private double solapePerSideMm;
    private double totalSolapeWidthDeductionMm;
    private double totalSolapeHeightDeductionMm;
    private double fabricationWidthMm;
    private double fabricationHeightMm;

    private List<ProfileCutItem> cuts = new ArrayList<>();

    private double totalProfileLengthMm;
    private double totalProfileLengthMeters;

    private double selectedCommercialBarLengthMm;
    private int commercialBarsNeeded;
    private double totalCommercialStockLengthMm;
    private double wasteMeters;
    private double wastePercentage;

    private String profileInfo;
    private BigDecimal estimatedCost;
    private String notes;

    public StructureCalculationResult() {
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

    public double getSolapePerSideMm() {
        return solapePerSideMm;
    }

    public void setSolapePerSideMm(double solapePerSideMm) {
        this.solapePerSideMm = solapePerSideMm;
    }

    public double getTotalSolapeWidthDeductionMm() {
        return totalSolapeWidthDeductionMm;
    }

    public void setTotalSolapeWidthDeductionMm(double totalSolapeWidthDeductionMm) {
        this.totalSolapeWidthDeductionMm = totalSolapeWidthDeductionMm;
    }

    public double getTotalSolapeHeightDeductionMm() {
        return totalSolapeHeightDeductionMm;
    }

    public void setTotalSolapeHeightDeductionMm(double totalSolapeHeightDeductionMm) {
        this.totalSolapeHeightDeductionMm = totalSolapeHeightDeductionMm;
    }

    public double getFabricationWidthMm() {
        return fabricationWidthMm;
    }

    public void setFabricationWidthMm(double fabricationWidthMm) {
        this.fabricationWidthMm = fabricationWidthMm;
    }

    public double getFabricationHeightMm() {
        return fabricationHeightMm;
    }

    public void setFabricationHeightMm(double fabricationHeightMm) {
        this.fabricationHeightMm = fabricationHeightMm;
    }

    public List<ProfileCutItem> getCuts() {
        return cuts;
    }

    public void setCuts(List<ProfileCutItem> cuts) {
        this.cuts = cuts;
    }

    public double getTotalProfileLengthMm() {
        return totalProfileLengthMm;
    }

    public void setTotalProfileLengthMm(double totalProfileLengthMm) {
        this.totalProfileLengthMm = totalProfileLengthMm;
    }

    public double getTotalProfileLengthMeters() {
        return totalProfileLengthMeters;
    }

    public void setTotalProfileLengthMeters(double totalProfileLengthMeters) {
        this.totalProfileLengthMeters = totalProfileLengthMeters;
    }

    public double getSelectedCommercialBarLengthMm() {
        return selectedCommercialBarLengthMm;
    }

    public void setSelectedCommercialBarLengthMm(double selectedCommercialBarLengthMm) {
        this.selectedCommercialBarLengthMm = selectedCommercialBarLengthMm;
    }

    public int getCommercialBarsNeeded() {
        return commercialBarsNeeded;
    }

    public void setCommercialBarsNeeded(int commercialBarsNeeded) {
        this.commercialBarsNeeded = commercialBarsNeeded;
    }

    public double getTotalCommercialStockLengthMm() {
        return totalCommercialStockLengthMm;
    }

    public void setTotalCommercialStockLengthMm(double totalCommercialStockLengthMm) {
        this.totalCommercialStockLengthMm = totalCommercialStockLengthMm;
    }

    public double getWasteMeters() {
        return wasteMeters;
    }

    public void setWasteMeters(double wasteMeters) {
        this.wasteMeters = wasteMeters;
    }

    public double getWastePercentage() {
        return wastePercentage;
    }

    public void setWastePercentage(double wastePercentage) {
        this.wastePercentage = wastePercentage;
    }

    public String getProfileInfo() {
        return profileInfo;
    }

    public void setProfileInfo(String profileInfo) {
        this.profileInfo = profileInfo;
    }

    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(BigDecimal estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
