package com.carpinteria.api.dto.calculator;

/**
 * Represents an individual profile piece to be cut.
 */
public class ProfileCutItem {

    private String description;
    private double lengthMm;
    private int quantity;
    private String cutAngle; // "45° INGLETE", "90° RECTO"

    public ProfileCutItem() {
    }

    public ProfileCutItem(String description, double lengthMm, int quantity, String cutAngle) {
        this.description = description;
        this.lengthMm = lengthMm;
        this.quantity = quantity;
        this.cutAngle = cutAngle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLengthMm() {
        return lengthMm;
    }

    public void setLengthMm(double lengthMm) {
        this.lengthMm = lengthMm;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCutAngle() {
        return cutAngle;
    }

    public void setCutAngle(String cutAngle) {
        this.cutAngle = cutAngle;
    }
}
