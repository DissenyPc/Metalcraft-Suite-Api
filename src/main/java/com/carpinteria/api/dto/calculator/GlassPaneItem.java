package com.carpinteria.api.dto.calculator;

import java.util.ArrayList;
import java.util.List;

public class GlassPaneItem {

    private int paneIndex;
    private String label;
    private double cutWidthMm;
    private double cutHeightMm;
    private double areaM2;
    private double perimeterMeters;

    private List<ProfileCutItem> glazingBeads = new ArrayList<>();

    public GlassPaneItem() {
    }

    public GlassPaneItem(int paneIndex, String label, double cutWidthMm, double cutHeightMm) {
        this.paneIndex = paneIndex;
        this.label = label;
        this.cutWidthMm = cutWidthMm;
        this.cutHeightMm = cutHeightMm;
        this.areaM2 = Math.round((cutWidthMm * cutHeightMm / 1_000_000.0) * 1000.0) / 1000.0;
        this.perimeterMeters = Math.round((2.0 * (cutWidthMm + cutHeightMm) / 1000.0) * 100.0) / 100.0;
    }

    public int getPaneIndex() {
        return paneIndex;
    }

    public void setPaneIndex(int paneIndex) {
        this.paneIndex = paneIndex;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getCutWidthMm() {
        return cutWidthMm;
    }

    public void setCutWidthMm(double cutWidthMm) {
        this.cutWidthMm = cutWidthMm;
    }

    public double getCutHeightMm() {
        return cutHeightMm;
    }

    public void setCutHeightMm(double cutHeightMm) {
        this.cutHeightMm = cutHeightMm;
    }

    public double getAreaM2() {
        return areaM2;
    }

    public void setAreaM2(double areaM2) {
        this.areaM2 = areaM2;
    }

    public double getPerimeterMeters() {
        return perimeterMeters;
    }

    public void setPerimeterMeters(double perimeterMeters) {
        this.perimeterMeters = perimeterMeters;
    }

    public List<ProfileCutItem> getGlazingBeads() {
        return glazingBeads;
    }

    public void setGlazingBeads(List<ProfileCutItem> glazingBeads) {
        this.glazingBeads = glazingBeads;
    }
}
