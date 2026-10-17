package com.carpinteria.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing Iron / Steel Profiles.
 * Stored in a separate table 'iron_profiles'.
 * Supports individual edits and bulk price updates by table or series.
 */
@Entity
@Table(name = "iron_profiles")
public class IronProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La referencia es obligatoria")
    @Column(nullable = false, unique = true, length = 100)
    private String reference;

    @NotBlank(message = "El nombre o descripción es obligatorio")
    @Column(nullable = false, length = 200)
    private String name;

    /**
     * Series or profile category (e.g., "Tubo Cuadrado", "Tubo Rectangular", "Pletina", "Ángulo", "Viga UPN", "Redondo Macizo").
     */
    @NotBlank(message = "La serie/tipo es obligatoria")
    @Column(name = "series", nullable = false, length = 100)
    private String series;

    /**
     * Wall thickness in mm (espesor en mm, p. ej. 1.5, 2.0, 3.0, 4.0).
     */
    @PositiveOrZero(message = "El espesor debe ser positivo")
    @Column(name = "thickness_mm", precision = 10, scale = 2)
    private BigDecimal thicknessMm;

    /**
     * Standard bar length in meters (default 6.0m).
     */
    @NotNull(message = "La longitud en metros es obligatoria")
    @PositiveOrZero(message = "La longitud debe ser positiva")
    @Column(name = "length_meters", precision = 10, scale = 2)
    private BigDecimal lengthMeters = new BigDecimal("6.00");

    /**
     * Weight per linear meter in kg/m.
     */
    @PositiveOrZero(message = "El peso por metro debe ser positivo")
    @Column(name = "weight_per_meter", precision = 10, scale = 3)
    private BigDecimal weightPerMeter;

    /**
     * Price per kilogram (€/kg). Often the base pricing unit for steel/iron.
     */
    @PositiveOrZero(message = "El precio por kg debe ser positivo")
    @Column(name = "price_per_kg", precision = 10, scale = 2)
    private BigDecimal pricePerKg;

    /**
     * Price per linear meter (€/m).
     */
    @NotNull(message = "El precio por metro es obligatorio")
    @PositiveOrZero(message = "El precio por metro debe ser positivo")
    @Column(name = "price_per_meter", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerMeter;

    /**
     * Price per complete bar (€/barra).
     */
    @NotNull(message = "El precio por barra es obligatorio")
    @PositiveOrZero(message = "El precio por barra debe ser positivo")
    @Column(name = "price_per_bar", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerBar;

    /**
     * Current stock in whole bars.
     */
    @PositiveOrZero
    @Column(name = "stock_bars")
    private Integer stockBars = 0;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public IronProfile() {
    }

    public IronProfile(String reference, String name, String series, BigDecimal thicknessMm,
                       BigDecimal lengthMeters, BigDecimal weightPerMeter,
                       BigDecimal pricePerKg, BigDecimal pricePerMeter, BigDecimal pricePerBar, Integer stockBars) {
        this.reference = reference;
        this.name = name;
        this.series = series;
        this.thicknessMm = thicknessMm;
        this.lengthMeters = lengthMeters;
        this.weightPerMeter = weightPerMeter;
        this.pricePerKg = pricePerKg;
        this.pricePerMeter = pricePerMeter;
        this.pricePerBar = pricePerBar;
        this.stockBars = stockBars;
        this.active = true;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.active == null) {
            this.active = true;
        }
        if (this.stockBars == null) {
            this.stockBars = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public BigDecimal getThicknessMm() {
        return thicknessMm;
    }

    public void setThicknessMm(BigDecimal thicknessMm) {
        this.thicknessMm = thicknessMm;
    }

    public BigDecimal getLengthMeters() {
        return lengthMeters;
    }

    public void setLengthMeters(BigDecimal lengthMeters) {
        this.lengthMeters = lengthMeters;
    }

    public BigDecimal getWeightPerMeter() {
        return weightPerMeter;
    }

    public void setWeightPerMeter(BigDecimal weightPerMeter) {
        this.weightPerMeter = weightPerMeter;
    }

    public BigDecimal getPricePerKg() {
        return pricePerKg;
    }

    public void setPricePerKg(BigDecimal pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public BigDecimal getPricePerMeter() {
        return pricePerMeter;
    }

    public void setPricePerMeter(BigDecimal pricePerMeter) {
        this.pricePerMeter = pricePerMeter;
    }

    public BigDecimal getPricePerBar() {
        return pricePerBar;
    }

    public void setPricePerBar(BigDecimal pricePerBar) {
        this.pricePerBar = pricePerBar;
    }

    public Integer getStockBars() {
        return stockBars;
    }

    public void setStockBars(Integer stockBars) {
        this.stockBars = stockBars;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
