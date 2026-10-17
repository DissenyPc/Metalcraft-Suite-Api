package com.carpinteria.api.dto.calculator;

import java.util.ArrayList;
import java.util.List;

public class ShutterCalculationResult {

    private double totalOpeningWidthMm;
    private double totalOpeningHeightMm;

    private double boxHeightMm;
    private String boxModel;

    /**
     * Window frame height after subtracting the compact shutter box.
     */
    private double windowFabricationHeightMm;

    /**
     * Shutter guide rails height.
     */
    private double guideLengthMm;
    private int guideCount = 2;

    private String winderType;
    private double winderDeductionAppliedMm;

    /**
     * Effective width of the shutter curtain (paño de lamas).
     */
    private double shutterSlatCutWidthMm;

    /**
     * Total height of the shutter curtain needed to reach bottom and coil into the box.
     */
    private double shutterCurtainHeightMm;

    private int slatCount;
    private String slatModel;

    private double axleLengthMm;
    private double totalShutterAreaM2;

    private List<String> componentsIncluded = new ArrayList<>();
    private String notes;

    public ShutterCalculationResult() {
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

    public double getBoxHeightMm() {
        return boxHeightMm;
    }

    public void setBoxHeightMm(double boxHeightMm) {
        this.boxHeightMm = boxHeightMm;
    }

    public String getBoxModel() {
        return boxModel;
    }

    public void setBoxModel(String boxModel) {
        this.boxModel = boxModel;
    }

    public double getWindowFabricationHeightMm() {
        return windowFabricationHeightMm;
    }

    public void setWindowFabricationHeightMm(double windowFabricationHeightMm) {
        this.windowFabricationHeightMm = windowFabricationHeightMm;
    }

    public double getGuideLengthMm() {
        return guideLengthMm;
    }

    public void setGuideLengthMm(double guideLengthMm) {
        this.guideLengthMm = guideLengthMm;
    }

    public int getGuideCount() {
        return guideCount;
    }

    public void setGuideCount(int guideCount) {
        this.guideCount = guideCount;
    }

    public String getWinderType() {
        return winderType;
    }

    public void setWinderType(String winderType) {
        this.winderType = winderType;
    }

    public double getWinderDeductionAppliedMm() {
        return winderDeductionAppliedMm;
    }

    public void setWinderDeductionAppliedMm(double winderDeductionAppliedMm) {
        this.winderDeductionAppliedMm = winderDeductionAppliedMm;
    }

    public double getShutterSlatCutWidthMm() {
        return shutterSlatCutWidthMm;
    }

    public void setShutterSlatCutWidthMm(double shutterSlatCutWidthMm) {
        this.shutterSlatCutWidthMm = shutterSlatCutWidthMm;
    }

    public double getShutterCurtainHeightMm() {
        return shutterCurtainHeightMm;
    }

    public void setShutterCurtainHeightMm(double shutterCurtainHeightMm) {
        this.shutterCurtainHeightMm = shutterCurtainHeightMm;
    }

    public int getSlatCount() {
        return slatCount;
    }

    public void setSlatCount(int slatCount) {
        this.slatCount = slatCount;
    }

    public String getSlatModel() {
        return slatModel;
    }

    public void setSlatModel(String slatModel) {
        this.slatModel = slatModel;
    }

    public double getAxleLengthMm() {
        return axleLengthMm;
    }

    public void setAxleLengthMm(double axleLengthMm) {
        this.axleLengthMm = axleLengthMm;
    }

    public double getTotalShutterAreaM2() {
        return totalShutterAreaM2;
    }

    public void setTotalShutterAreaM2(double totalShutterAreaM2) {
        this.totalShutterAreaM2 = totalShutterAreaM2;
    }

    public List<String> getComponentsIncluded() {
        return componentsIncluded;
    }

    public void setComponentsIncluded(List<String> componentsIncluded) {
        this.componentsIncluded = componentsIncluded;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
