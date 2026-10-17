package com.carpinteria.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Entity representing an individual Budget Line Item (Partida de Presupuesto).
 * Can represent materials (Aluminium, Iron, Hardware, Glass) or labor/installation.
 */
@Entity
@Table(name = "budget_items")
public class BudgetItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    @JsonBackReference
    private Budget budget;

    /**
     * Line order within the budget (e.g. 1, 2, 3...).
     */
    @Column(name = "item_order")
    private Integer itemOrder = 1;

    /**
     * Category / type: "ALUMINIO", "HIERRO", "HERRAJE", "VIDRIO", "MANO_DE_OBRA", "MONTAJE", "OTROS".
     */
    @Column(name = "item_category", length = 50)
    private String itemCategory;

    /**
     * Optional linked product reference (e.g., "AL-COR-60-BL", "CRIS-CLIM-4-12-4").
     */
    @Column(name = "related_reference", length = 100)
    private String relatedReference;

    /**
     * Detailed concept description (e.g., "Ventana 2 hojas corredera serie Europea 120x100 blanco con vidrio climalit 4/12/4").
     */
    @NotBlank(message = "El concepto de la partida es obligatorio")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String concept;

    /**
     * Quantity (e.g. 2.00 ventanas, 15.50 metros lineales).
     */
    @NotNull(message = "La cantidad es obligatoria")
    @PositiveOrZero(message = "La cantidad debe ser mayor o igual a cero")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity = BigDecimal.ONE;

    /**
     * Unit of measurement (UD, M, ML, M2, KG, H).
     */
    @Column(length = 20, nullable = false)
    private String unit = "UD";

    /**
     * Unit price in euros (€).
     */
    @NotNull(message = "El precio unitario es obligatorio")
    @PositiveOrZero(message = "El precio unitario debe ser positivo")
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    /**
     * Percentage discount applied to this item line (0.00% to 100.00%).
     */
    @PositiveOrZero
    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    /**
     * Total price for this line item = quantity * unitPrice * (1 - discount/100).
     */
    @NotNull
    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public BudgetItem() {
    }

    public BudgetItem(Integer itemOrder, String itemCategory, String relatedReference,
                      String concept, BigDecimal quantity, String unit,
                      BigDecimal unitPrice, BigDecimal discountPercentage, String notes) {
        this.itemOrder = itemOrder;
        this.itemCategory = itemCategory;
        this.relatedReference = relatedReference;
        this.concept = concept;
        this.quantity = quantity != null ? quantity : BigDecimal.ONE;
        this.unit = unit != null ? unit : "UD";
        this.unitPrice = unitPrice != null ? unitPrice : BigDecimal.ZERO;
        this.discountPercentage = discountPercentage != null ? discountPercentage : BigDecimal.ZERO;
        this.notes = notes;
        calculateTotalPrice();
    }

    /**
     * Calculates line total taking into account quantity, unit price, and discount percentage.
     */
    public void calculateTotalPrice() {
        if (this.quantity == null) {
            this.quantity = BigDecimal.ONE;
        }
        if (this.unitPrice == null) {
            this.unitPrice = BigDecimal.ZERO;
        }
        if (this.discountPercentage == null) {
            this.discountPercentage = BigDecimal.ZERO;
        }

        BigDecimal gross = this.quantity.multiply(this.unitPrice);
        BigDecimal discountFactor = BigDecimal.ONE.subtract(
                this.discountPercentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));

        this.totalPrice = gross.multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);
    }

    @PrePersist
    @PreUpdate
    protected void onSave() {
        calculateTotalPrice();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    public Integer getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(Integer itemOrder) {
        this.itemOrder = itemOrder;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getRelatedReference() {
        return relatedReference;
    }

    public void setRelatedReference(String relatedReference) {
        this.relatedReference = relatedReference;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
        calculateTotalPrice();
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateTotalPrice();
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
        calculateTotalPrice();
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
