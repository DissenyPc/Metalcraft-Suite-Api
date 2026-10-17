package com.carpinteria.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing Filling Materials (Materiales de Relleno / Huecos).
 * Examples: Vidrio / Cristal (Climalit, Laminado, Templado), Paneles Sandwich, Policarbonato, Chapa, Metacrilato.
 * Adapted to fit the huecos.csv catalogue with finish/color and min/max dimension constraints.
 */
@Entity
@Table(name = "filling_materials", indexes = {
    @Index(name = "idx_fill_reference", columnList = "reference"),
    @Index(name = "idx_fill_material_type", columnList = "material_type")
})
public class FillingMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La referencia es obligatoria")
    @Column(nullable = false, unique = true, length = 100)
    private String reference;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 200)
    private String name;

    /**
     * Category / Type of material (e.g. "Cristal", "Cristal de Seguridad", "Cristal Aislante",
     * "Plásticos", "Chapa", "Panel Aislante").
     */
    @NotBlank(message = "El tipo/categoría de material es obligatorio")
    @Column(name = "material_type", nullable = false, length = 100)
    private String materialType;

    /**
     * Finish or color description (e.g. "Incoloro/Gris/Bronce", "Blanco RAL 9010",
     * "Imitación Madera", "Standard", "Transparente/Opaco").
     */
    @Column(name = "finish_color", length = 200)
    private String finishColor;

    /**
     * Thickness in millimeters (espesor mm, p. ej. 4mm, 16mm, 24mm).
     */
    @PositiveOrZero(message = "El espesor debe ser positivo")
    @Column(name = "thickness_mm", precision = 10, scale = 2)
    private BigDecimal thicknessMm;

    /**
     * Measurement unit (typically "M2" for square meter, or "UD").
     */
    @Column(length = 20, nullable = false)
    private String unit = "M2";

    /**
     * Sales price per square meter (€/m²).
     */
    @NotNull(message = "El precio por m2 es obligatorio")
    @PositiveOrZero(message = "El precio por m2 debe ser positivo")
    @Column(name = "price_per_square_meter", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerSquareMeter;

    /**
     * Cost price per square meter (€/m²).
     */
    @PositiveOrZero(message = "El precio de coste debe ser positivo")
    @Column(name = "cost_price", precision = 10, scale = 2)
    private BigDecimal costPrice;

    // =========================================================================
    // Dimension constraints (from huecos.csv catalogue)
    // =========================================================================

    /**
     * Minimum width in mm that can be fabricated/cut for this material.
     */
    @PositiveOrZero
    @Column(name = "min_width_mm")
    private Integer minWidthMm;

    /**
     * Minimum height in mm that can be fabricated/cut for this material.
     */
    @PositiveOrZero
    @Column(name = "min_height_mm")
    private Integer minHeightMm;

    /**
     * Maximum width in mm that can be fabricated/cut for this material.
     */
    @PositiveOrZero
    @Column(name = "max_width_mm")
    private Integer maxWidthMm;

    /**
     * Maximum height in mm that can be fabricated/cut for this material.
     */
    @PositiveOrZero
    @Column(name = "max_height_mm")
    private Integer maxHeightMm;

    /**
     * Current available stock in square meters.
     */
    @PositiveOrZero
    @Column(name = "stock_m2", precision = 10, scale = 2)
    private BigDecimal stockM2 = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public FillingMaterial() {
    }

    public FillingMaterial(String reference, String name, String materialType, BigDecimal thicknessMm,
                           String unit, BigDecimal pricePerSquareMeter, BigDecimal costPrice, BigDecimal stockM2) {
        this.reference = reference;
        this.name = name;
        this.materialType = materialType;
        this.thicknessMm = thicknessMm;
        this.unit = unit != null ? unit : "M2";
        this.pricePerSquareMeter = pricePerSquareMeter;
        this.costPrice = costPrice;
        this.stockM2 = stockM2 != null ? stockM2 : BigDecimal.ZERO;
        this.active = true;
    }

    /**
     * Checks if the given dimensions (in mm) fall within this material's fabrication limits.
     * Returns true if dimensions are valid or if no limits are defined.
     */
    public boolean fitsWithinLimits(double widthMm, double heightMm) {
        if (minWidthMm != null && widthMm < minWidthMm) return false;
        if (minHeightMm != null && heightMm < minHeightMm) return false;
        if (maxWidthMm != null && widthMm > maxWidthMm) return false;
        if (maxHeightMm != null && heightMm > maxHeightMm) return false;
        return true;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.active == null) {
            this.active = true;
        }
        if (this.unit == null) {
            this.unit = "M2";
        }
        if (this.stockM2 == null) {
            this.stockM2 = BigDecimal.ZERO;
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

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getFinishColor() {
        return finishColor;
    }

    public void setFinishColor(String finishColor) {
        this.finishColor = finishColor;
    }

    public BigDecimal getThicknessMm() {
        return thicknessMm;
    }

    public void setThicknessMm(BigDecimal thicknessMm) {
        this.thicknessMm = thicknessMm;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getPricePerSquareMeter() {
        return pricePerSquareMeter;
    }

    public void setPricePerSquareMeter(BigDecimal pricePerSquareMeter) {
        this.pricePerSquareMeter = pricePerSquareMeter;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public Integer getMinWidthMm() {
        return minWidthMm;
    }

    public void setMinWidthMm(Integer minWidthMm) {
        this.minWidthMm = minWidthMm;
    }

    public Integer getMinHeightMm() {
        return minHeightMm;
    }

    public void setMinHeightMm(Integer minHeightMm) {
        this.minHeightMm = minHeightMm;
    }

    public Integer getMaxWidthMm() {
        return maxWidthMm;
    }

    public void setMaxWidthMm(Integer maxWidthMm) {
        this.maxWidthMm = maxWidthMm;
    }

    public Integer getMaxHeightMm() {
        return maxHeightMm;
    }

    public void setMaxHeightMm(Integer maxHeightMm) {
        this.maxHeightMm = maxHeightMm;
    }

    public BigDecimal getStockM2() {
        return stockM2;
    }

    public void setStockM2(BigDecimal stockM2) {
        this.stockM2 = stockM2;
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
