package com.carpinteria.api.dto.calculator;

import java.util.ArrayList;
import java.util.List;

public class MosquitoNetCalculationResult {

    private String type;
    private String description;
    private String profileColor;

    private double openingWidthMm;
    private double openingHeightMm;

    /**
     * Net fabrication dimensions.
     */
    private double netWidthMm;
    private double netHeightMm;
    private double meshAreaM2;

    private boolean intermediateTransomIncluded;
    private double intermediateTransomLengthMm;

    private List<ProfileCutItem> profileCuts = new ArrayList<>();
    private List<String> accessories = new ArrayList<>();
    private String notes;

    public MosquitoNetCalculationResult() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfileColor() {
        return profileColor;
    }

    public void setProfileColor(String profileColor) {
        this.profileColor = profileColor;
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

    public double getNetWidthMm() {
        return netWidthMm;
    }

    public void setNetWidthMm(double netWidthMm) {
        this.netWidthMm = netWidthMm;
    }

    public double getNetHeightMm() {
        return netHeightMm;
    }

    public void setNetHeightMm(double netHeightMm) {
        this.netHeightMm = netHeightMm;
    }

    public double getMeshAreaM2() {
        return meshAreaM2;
    }

    public void setMeshAreaM2(double meshAreaM2) {
        this.meshAreaM2 = meshAreaM2;
    }

    public boolean isIntermediateTransomIncluded() {
        return intermediateTransomIncluded;
    }

    public void setIntermediateTransomIncluded(boolean intermediateTransomIncluded) {
        this.intermediateTransomIncluded = intermediateTransomIncluded;
    }

    public double getIntermediateTransomLengthMm() {
        return intermediateTransomLengthMm;
    }

    public void setIntermediateTransomLengthMm(double intermediateTransomLengthMm) {
        this.intermediateTransomLengthMm = intermediateTransomLengthMm;
    }

    public List<ProfileCutItem> getProfileCuts() {
        return profileCuts;
    }

    public void setProfileCuts(List<ProfileCutItem> profileCuts) {
        this.profileCuts = profileCuts;
    }

    public List<String> getAccessories() {
        return accessories;
    }

    public void setAccessories(List<String> accessories) {
        this.accessories = accessories;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
