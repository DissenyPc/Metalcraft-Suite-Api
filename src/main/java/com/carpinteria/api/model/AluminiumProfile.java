package com.carpinteria.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Entity representing Aluminium Profiles (Perfiles de Aluminio).
 * Adapted to fit the Alugom catalogue format with support for all commercial finishes:
 * PLATA, L Blanco, Col std, L Color, Bronce, Inox, Madera, EZZY, METAL, Anod Negro, BICOL01, BICOL08, BICOL09.
 */
@Entity
@Table(name = "aluminium_profiles", indexes = {
    @Index(name = "idx_alum_reference", columnList = "reference"),
    @Index(name = "idx_alum_series", columnList = "series")
})
public class AluminiumProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Code / Reference (e.g. "100", "10000M", "100X100").
     */
    @NotBlank(message = "El código/referencia es obligatorio")
    @Column(nullable = false, length = 100)
    private String reference;

    /**
     * Description / Name of the profile (e.g. "CERCO VENTANA 28 S100").
     */
    @NotBlank(message = "La descripción/nombre es obligatoria")
    @Column(nullable = false, length = 255)
    private String name;

    /**
     * Series (e.g. "100", "SILVER 65 RPT", "AL 50", "IBIZA", "NORMALIZADOS").
     */
    @NotBlank(message = "La serie es obligatoria")
    @Column(nullable = false, length = 100)
    private String series;

    // =========================================================================
    // Prices per meter according to finish / treatment (Tarifa Alugom)
    // =========================================================================

    @Column(name = "price_plata", precision = 10, scale = 2)
    private BigDecimal pricePlata; // Anodizado Plata

    @Column(name = "price_l_blanco", precision = 10, scale = 2)
    private BigDecimal priceLBlanco; // Lacado Blanco

    @Column(name = "price_col_std", precision = 10, scale = 2)
    private BigDecimal priceColStd; // Color Estándar

    @Column(name = "price_l_color", precision = 10, scale = 2)
    private BigDecimal priceLColor; // Lacado Color

    @Column(name = "price_bronce", precision = 10, scale = 2)
    private BigDecimal priceBronce; // Anodizado Bronce

    @Column(name = "price_inox", precision = 10, scale = 2)
    private BigDecimal priceInox; // Anodizado Inox

    @Column(name = "price_madera", precision = 10, scale = 2)
    private BigDecimal priceMadera; // Lacado Madera

    @Column(name = "price_ezzy", precision = 10, scale = 2)
    private BigDecimal priceEzzy; // Acabado EZZY

    @Column(name = "price_metal", precision = 10, scale = 2)
    private BigDecimal priceMetal; // Acabado Metalizado

    @Column(name = "price_anod_negro", precision = 10, scale = 2)
    private BigDecimal priceAnodNegro; // Anodizado Negro

    @Column(name = "price_bicol01", precision = 10, scale = 2)
    private BigDecimal priceBicol01; // Bicolor 01

    @Column(name = "price_bicol08", precision = 10, scale = 2)
    private BigDecimal priceBicol08; // Bicolor 08

    @Column(name = "price_bicol09", precision = 10, scale = 2)
    private BigDecimal priceBicol09; // Bicolor 09

    // =========================================================================
    // General Calculation & Stock Fields
    // =========================================================================

    /**
     * Base price per linear meter (€/m), defaulted to the primary active finish (usually Lacado Blanco or Plata).
     */
    @NotNull(message = "El precio por metro es obligatorio")
    @PositiveOrZero(message = "El precio debe ser positivo")
    @Column(name = "price_per_meter", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerMeter = BigDecimal.ZERO;

    /**
     * Base price per whole commercial bar (€/barra).
     */
    @NotNull(message = "El precio por barra es obligatorio")
    @PositiveOrZero(message = "El precio por barra debe ser positivo")
    @Column(name = "price_per_bar", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerBar = BigDecimal.ZERO;

    /**
     * Standard bar length in meters (typically 6.00m or 6.50m).
     */
    @NotNull(message = "La longitud en metros es obligatoria")
    @PositiveOrZero(message = "La longitud debe ser positiva")
    @Column(name = "length_meters", precision = 10, scale = 2)
    private BigDecimal lengthMeters = new BigDecimal("6.00");

    /**
     * Weight per meter in kg/m.
     */
    @PositiveOrZero
    @Column(name = "weight_per_meter", precision = 10, scale = 3)
    private BigDecimal weightPerMeter;

    /**
     * Current stock in whole bars.
     */
    @PositiveOrZero
    @Column(name = "stock_bars")
    private Integer stockBars = 0;

    /**
     * Selected default finish name (e.g. "L Blanco", "PLATA", "Madera").
     */
    @Column(length = 100)
    private String finish = "L Blanco";

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public AluminiumProfile() {
    }

    public AluminiumProfile(String reference, String name, String series, String finish,
                            BigDecimal lengthMeters, BigDecimal weightPerMeter,
                            BigDecimal pricePerMeter, BigDecimal pricePerBar, Integer stockBars) {
        this.reference = reference;
        this.name = name;
        this.series = series;
        this.finish = finish;
        this.lengthMeters = lengthMeters != null ? lengthMeters : new BigDecimal("6.00");
        this.weightPerMeter = weightPerMeter;
        this.pricePerMeter = pricePerMeter != null ? pricePerMeter : BigDecimal.ZERO;
        this.pricePerBar = pricePerBar != null ? pricePerBar : BigDecimal.ZERO;
        this.stockBars = stockBars != null ? stockBars : 0;
        this.active = true;
    }

    /**
     * Retrieves the price corresponding to a specific finish name.
     * Falls back to pricePerMeter if finish is not matched or unavailable (9999).
     */
    public BigDecimal getPriceForFinish(String finishName) {
        if (finishName == null || finishName.isBlank()) {
            return this.pricePerMeter;
        }
        String clean = finishName.trim().toUpperCase();
        BigDecimal p = null;
        if (clean.contains("PLATA")) p = this.pricePlata;
        else if (clean.contains("BLANCO")) p = this.priceLBlanco;
        else if (clean.contains("COL STD") || clean.contains("STD")) p = this.priceColStd;
        else if (clean.contains("COLOR")) p = this.priceLColor;
        else if (clean.contains("BRONCE")) p = this.priceBronce;
        else if (clean.contains("INOX")) p = this.priceInox;
        else if (clean.contains("MADERA")) p = this.priceMadera;
        else if (clean.contains("EZZY")) p = this.priceEzzy;
        else if (clean.contains("METAL")) p = this.priceMetal;
        else if (clean.contains("NEGRO")) p = this.priceAnodNegro;
        else if (clean.contains("BICOL01")) p = this.priceBicol01;
        else if (clean.contains("BICOL08")) p = this.priceBicol08;
        else if (clean.contains("BICOL09")) p = this.priceBicol09;

        if (p != null && p.compareTo(new BigDecimal("9000")) < 0) {
            return p;
        }
        return this.pricePerMeter;
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
        if (this.lengthMeters == null) {
            this.lengthMeters = new BigDecimal("6.00");
        }
        computeBasePricesIfNeeded();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        computeBasePricesIfNeeded();
    }

    private void computeBasePricesIfNeeded() {
        if (this.pricePerMeter == null || this.pricePerMeter.compareTo(BigDecimal.ZERO) == 0) {
            // Pick first valid finish price (< 9000)
            if (this.priceLBlanco != null && this.priceLBlanco.compareTo(new BigDecimal("9000")) < 0) {
                this.pricePerMeter = this.priceLBlanco;
            } else if (this.pricePlata != null && this.pricePlata.compareTo(new BigDecimal("9000")) < 0) {
                this.pricePerMeter = this.pricePlata;
            } else if (this.priceColStd != null && this.priceColStd.compareTo(new BigDecimal("9000")) < 0) {
                this.pricePerMeter = this.priceColStd;
            } else {
                this.pricePerMeter = BigDecimal.ZERO;
            }
        }
        if ((this.pricePerBar == null || this.pricePerBar.compareTo(BigDecimal.ZERO) == 0)
                && this.pricePerMeter != null && this.lengthMeters != null) {
            this.pricePerBar = this.pricePerMeter.multiply(this.lengthMeters).setScale(2, RoundingMode.HALF_UP);
        }
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

    public BigDecimal getPricePlata() {
        return pricePlata;
    }

    public void setPricePlata(BigDecimal pricePlata) {
        this.pricePlata = pricePlata;
    }

    public BigDecimal getPriceLBlanco() {
        return priceLBlanco;
    }

    public void setPriceLBlanco(BigDecimal priceLBlanco) {
        this.priceLBlanco = priceLBlanco;
    }

    public BigDecimal getPriceColStd() {
        return priceColStd;
    }

    public void setPriceColStd(BigDecimal priceColStd) {
        this.priceColStd = priceColStd;
    }

    public BigDecimal getPriceLColor() {
        return priceLColor;
    }

    public void setPriceLColor(BigDecimal priceLColor) {
        this.priceLColor = priceLColor;
    }

    public BigDecimal getPriceBronce() {
        return priceBronce;
    }

    public void setPriceBronce(BigDecimal priceBronce) {
        this.priceBronce = priceBronce;
    }

    public BigDecimal getPriceInox() {
        return priceInox;
    }

    public void setPriceInox(BigDecimal priceInox) {
        this.priceInox = priceInox;
    }

    public BigDecimal getPriceMadera() {
        return priceMadera;
    }

    public void setPriceMadera(BigDecimal priceMadera) {
        this.priceMadera = priceMadera;
    }

    public BigDecimal getPriceEzzy() {
        return priceEzzy;
    }

    public void setPriceEzzy(BigDecimal priceEzzy) {
        this.priceEzzy = priceEzzy;
    }

    public BigDecimal getPriceMetal() {
        return priceMetal;
    }

    public void setPriceMetal(BigDecimal priceMetal) {
        this.priceMetal = priceMetal;
    }

    public BigDecimal getPriceAnodNegro() {
        return priceAnodNegro;
    }

    public void setPriceAnodNegro(BigDecimal priceAnodNegro) {
        this.priceAnodNegro = priceAnodNegro;
    }

    public BigDecimal getPriceBicol01() {
        return priceBicol01;
    }

    public void setPriceBicol01(BigDecimal priceBicol01) {
        this.priceBicol01 = priceBicol01;
    }

    public BigDecimal getPriceBicol08() {
        return priceBicol08;
    }

    public void setPriceBicol08(BigDecimal priceBicol08) {
        this.priceBicol08 = priceBicol08;
    }

    public BigDecimal getPriceBicol09() {
        return priceBicol09;
    }

    public void setPriceBicol09(BigDecimal priceBicol09) {
        this.priceBicol09 = priceBicol09;
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

    public Integer getStockBars() {
        return stockBars;
    }

    public void setStockBars(Integer stockBars) {
        this.stockBars = stockBars;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
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
