package com.carpinteria.api.dto.calculator;

import java.util.ArrayList;
import java.util.List;

public class RailingCalculationResult {

    private double totalLengthMm;
    private double totalHeightMm;

    private String handrailType;
    private String handrailDescription;
    private double handrailLengthMeters;

    private int postCount;
    private double postLengthMm;
    private double totalPostsLengthMeters;

    private int infillBarCount;
    private double infillBarLengthMm;
    private double exactInfillSpacingMm;
    private double totalInfillBarsLengthMeters;

    private double bottomRailLengthMeters;
    private int basePlatesCount;
    private String fixingDescription;

    private List<ProfileCutItem> cutList = new ArrayList<>();
    private String notes;

    public RailingCalculationResult() {
    }

    public double getTotalLengthMm() {
        return totalLengthMm;
    }

    public void setTotalLengthMm(double totalLengthMm) {
        this.totalLengthMm = totalLengthMm;
    }

    public double getTotalHeightMm() {
        return totalHeightMm;
    }

    public void setTotalHeightMm(double totalHeightMm) {
        this.totalHeightMm = totalHeightMm;
    }

    public String getHandrailType() {
        return handrailType;
    }

    public void setHandrailType(String handrailType) {
        this.handrailType = handrailType;
    }

    public String getHandrailDescription() {
        return handrailDescription;
    }

    public void setHandrailDescription(String handrailDescription) {
        this.handrailDescription = handrailDescription;
    }

    public double getHandrailLengthMeters() {
        return handrailLengthMeters;
    }

    public void setHandrailLengthMeters(double handrailLengthMeters) {
        this.handrailLengthMeters = handrailLengthMeters;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public double getPostLengthMm() {
        return postLengthMm;
    }

    public void setPostLengthMm(double postLengthMm) {
        this.postLengthMm = postLengthMm;
    }

    public double getTotalPostsLengthMeters() {
        return totalPostsLengthMeters;
    }

    public void setTotalPostsLengthMeters(double totalPostsLengthMeters) {
        this.totalPostsLengthMeters = totalPostsLengthMeters;
    }

    public int getInfillBarCount() {
        return infillBarCount;
    }

    public void setInfillBarCount(int infillBarCount) {
        this.infillBarCount = infillBarCount;
    }

    public double getInfillBarLengthMm() {
        return infillBarLengthMm;
    }

    public void setInfillBarLengthMm(double infillBarLengthMm) {
        this.infillBarLengthMm = infillBarLengthMm;
    }

    public double getExactInfillSpacingMm() {
        return exactInfillSpacingMm;
    }

    public void setExactInfillSpacingMm(double exactInfillSpacingMm) {
        this.exactInfillSpacingMm = exactInfillSpacingMm;
    }

    public double getTotalInfillBarsLengthMeters() {
        return totalInfillBarsLengthMeters;
    }

    public void setTotalInfillBarsLengthMeters(double totalInfillBarsLengthMeters) {
        this.totalInfillBarsLengthMeters = totalInfillBarsLengthMeters;
    }

    public double getBottomRailLengthMeters() {
        return bottomRailLengthMeters;
    }

    public void setBottomRailLengthMeters(double bottomRailLengthMeters) {
        this.bottomRailLengthMeters = bottomRailLengthMeters;
    }

    public int getBasePlatesCount() {
        return basePlatesCount;
    }

    public void setBasePlatesCount(int basePlatesCount) {
        this.basePlatesCount = basePlatesCount;
    }

    public String getFixingDescription() {
        return fixingDescription;
    }

    public void setFixingDescription(String fixingDescription) {
        this.fixingDescription = fixingDescription;
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
