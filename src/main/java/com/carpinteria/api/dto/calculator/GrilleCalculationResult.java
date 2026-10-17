package com.carpinteria.api.dto.calculator;

import java.util.ArrayList;
import java.util.List;

public class GrilleCalculationResult {

    private String material;
    private String fixingType;

    private double openingWidthMm;
    private double openingHeightMm;

    /**
     * Fabrication dimensions (including embedded additions if applicable).
     */
    private double fabricationWidthMm;
    private double fabricationHeightMm;

    private double embeddedAddedWidthMm;
    private double embeddedAddedHeightMm;

    private double innerWidthMm;
    private double innerHeightMm;

    /**
     * Calculated number of vertical bars.
     */
    private int barCount;

    /**
     * Number of spaces between bars.
     */
    private int spaceCount;

    /**
     * Actual clear spacing between bars (guaranteed <= 130mm).
     */
    private double exactSpacingMm;

    private double singleBarLengthMm;
    private double totalBarsLengthMeters;
    private double totalFrameProfileMeters;

    private int reinforcingTransomCount;
    private double reinforcingTransomLengthMm;

    /**
     * Fixings details (e.g. 4 angles if screwed).
     */
    private int fixingAnglesCount;
    private String fixingDescription;

    private Double estimatedWeightKg;
    private List<ProfileCutItem> cutList = new ArrayList<>();
    private String notes;

    public GrilleCalculationResult() {
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getFixingType() {
        return fixingType;
    }

    public void setFixingType(String fixingType) {
        this.fixingType = fixingType;
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

    public double getEmbeddedAddedWidthMm() {
        return embeddedAddedWidthMm;
    }

    public void setEmbeddedAddedWidthMm(double embeddedAddedWidthMm) {
        this.embeddedAddedWidthMm = embeddedAddedWidthMm;
    }

    public double getEmbeddedAddedHeightMm() {
        return embeddedAddedHeightMm;
    }

    public void setEmbeddedAddedHeightMm(double embeddedAddedHeightMm) {
        this.embeddedAddedHeightMm = embeddedAddedHeightMm;
    }

    public double getInnerWidthMm() {
        return innerWidthMm;
    }

    public void setInnerWidthMm(double innerWidthMm) {
        this.innerWidthMm = innerWidthMm;
    }

    public double getInnerHeightMm() {
        return innerHeightMm;
    }

    public void setInnerHeightMm(double innerHeightMm) {
        this.innerHeightMm = innerHeightMm;
    }

    public int getBarCount() {
        return barCount;
    }

    public void setBarCount(int barCount) {
        this.barCount = barCount;
    }

    public int getSpaceCount() {
        return spaceCount;
    }

    public void setSpaceCount(int spaceCount) {
        this.spaceCount = spaceCount;
    }

    public double getExactSpacingMm() {
        return exactSpacingMm;
    }

    public void setExactSpacingMm(double exactSpacingMm) {
        this.exactSpacingMm = exactSpacingMm;
    }

    public double getSingleBarLengthMm() {
        return singleBarLengthMm;
    }

    public void setSingleBarLengthMm(double singleBarLengthMm) {
        this.singleBarLengthMm = singleBarLengthMm;
    }

    public double getTotalBarsLengthMeters() {
        return totalBarsLengthMeters;
    }

    public void setTotalBarsLengthMeters(double totalBarsLengthMeters) {
        this.totalBarsLengthMeters = totalBarsLengthMeters;
    }

    public double getTotalFrameProfileMeters() {
        return totalFrameProfileMeters;
    }

    public void setTotalFrameProfileMeters(double totalFrameProfileMeters) {
        this.totalFrameProfileMeters = totalFrameProfileMeters;
    }

    public int getReinforcingTransomCount() {
        return reinforcingTransomCount;
    }

    public void setReinforcingTransomCount(int reinforcingTransomCount) {
        this.reinforcingTransomCount = reinforcingTransomCount;
    }

    public double getReinforcingTransomLengthMm() {
        return reinforcingTransomLengthMm;
    }

    public void setReinforcingTransomLengthMm(double reinforcingTransomLengthMm) {
        this.reinforcingTransomLengthMm = reinforcingTransomLengthMm;
    }

    public int getFixingAnglesCount() {
        return fixingAnglesCount;
    }

    public void setFixingAnglesCount(int fixingAnglesCount) {
        this.fixingAnglesCount = fixingAnglesCount;
    }

    public String getFixingDescription() {
        return fixingDescription;
    }

    public void setFixingDescription(String fixingDescription) {
        this.fixingDescription = fixingDescription;
    }

    public Double getEstimatedWeightKg() {
        return estimatedWeightKg;
    }

    public void setEstimatedWeightKg(Double estimatedWeightKg) {
        this.estimatedWeightKg = estimatedWeightKg;
    }

    public List<ProfileCutItem> getCutList() {
        return cutList;
    }

    public void setCutList(List<ProfileCutItem> cutList) {
        this.cutList = cutList;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
