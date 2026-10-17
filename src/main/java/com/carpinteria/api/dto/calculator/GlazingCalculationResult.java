package com.carpinteria.api.dto.calculator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GlazingCalculationResult {

    private double frameWidthMm;
    private double frameHeightMm;
    private int horizontalSplits;
    private int verticalSplits;
    private int totalPanesCount;

    private List<GlassPaneItem> panes = new ArrayList<>();

    private double totalGlassAreaM2;
    private double totalGasketPerimeterMeters;
    private double totalGlazingBeadLengthMeters;

    private String fillingMaterialName;
    private BigDecimal estimatedGlassCost;
    private String notes;

    public GlazingCalculationResult() {
    }

    public double getFrameWidthMm() {
        return frameWidthMm;
    }

    public void setFrameWidthMm(double frameWidthMm) {
        this.frameWidthMm = frameWidthMm;
    }

    public double getFrameHeightMm() {
        return frameHeightMm;
    }

    public void setFrameHeightMm(double frameHeightMm) {
        this.frameHeightMm = frameHeightMm;
    }

    public int getHorizontalSplits() {
        return horizontalSplits;
    }

    public void setHorizontalSplits(int horizontalSplits) {
        this.horizontalSplits = horizontalSplits;
    }

    public int getVerticalSplits() {
        return verticalSplits;
    }

    public void setVerticalSplits(int verticalSplits) {
        this.verticalSplits = verticalSplits;
    }

    public int getTotalPanesCount() {
        return totalPanesCount;
    }

    public void setTotalPanesCount(int totalPanesCount) {
        this.totalPanesCount = totalPanesCount;
    }

    public List<GlassPaneItem> getPanes() {
        return panes;
    }

    public void setPanes(List<GlassPaneItem> panes) {
        this.panes = panes;
    }

    public double getTotalGlassAreaM2() {
        return totalGlassAreaM2;
    }

    public void setTotalGlassAreaM2(double totalGlassAreaM2) {
        this.totalGlassAreaM2 = totalGlassAreaM2;
    }

    public double getTotalGasketPerimeterMeters() {
        return totalGasketPerimeterMeters;
    }

    public void setTotalGasketPerimeterMeters(double totalGasketPerimeterMeters) {
        this.totalGasketPerimeterMeters = totalGasketPerimeterMeters;
    }

    public double getTotalGlazingBeadLengthMeters() {
        return totalGlazingBeadLengthMeters;
    }

    public void setTotalGlazingBeadLengthMeters(double totalGlazingBeadLengthMeters) {
        this.totalGlazingBeadLengthMeters = totalGlazingBeadLengthMeters;
    }

    public String getFillingMaterialName() {
        return fillingMaterialName;
    }

    public void setFillingMaterialName(String fillingMaterialName) {
        this.fillingMaterialName = fillingMaterialName;
    }

    public BigDecimal getEstimatedGlassCost() {
        return estimatedGlassCost;
    }

    public void setEstimatedGlassCost(BigDecimal estimatedGlassCost) {
        this.estimatedGlassCost = estimatedGlassCost;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
