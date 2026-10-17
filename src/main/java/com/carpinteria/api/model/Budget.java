package com.carpinteria.api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing Budgets (Presupuestos).
 * Linked with Client data and contains multiple Budget Items (Partidas).
 * Supports full CRUD, duplication, and automatic financial calculations.
 */
@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El número de presupuesto es obligatorio")
    @Column(name = "budget_number", nullable = false, unique = true, length = 100)
    private String budgetNumber;

    @NotBlank(message = "El título o descripción general es obligatorio")
    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "La fecha de emisión es obligatoria")
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate = LocalDate.now();

    @Column(name = "valid_until")
    private LocalDate validUntil;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private BudgetStatus status = BudgetStatus.DRAFT;

    /**
     * Linked client entity.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private Client client;

    // Snapshot fields to preserve exact client data on the budget invoice:
    @Column(name = "client_name_snapshot", length = 200)
    private String clientNameSnapshot;

    @Column(name = "client_tax_id_snapshot", length = 50)
    private String clientTaxIdSnapshot;

    @Column(name = "client_phone_snapshot", length = 50)
    private String clientPhoneSnapshot;

    @Column(name = "client_email_snapshot", length = 150)
    private String clientEmailSnapshot;

    @Column(name = "client_address_snapshot", length = 255)
    private String clientAddressSnapshot;

    /**
     * Budget line items (Partidas).
     */
    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @OrderBy("itemOrder ASC")
    private List<BudgetItem> items = new ArrayList<>();

    /**
     * Sum of all item total prices before global discounts and taxes.
     */
    @PositiveOrZero
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    /**
     * Global budget discount percentage (0% to 100%).
     */
    @PositiveOrZero
    @Column(name = "global_discount_percentage", precision = 5, scale = 2)
    private BigDecimal globalDiscountPercentage = BigDecimal.ZERO;

    /**
     * Global budget discount amount in euros (€).
     */
    @PositiveOrZero
    @Column(name = "global_discount_amount", precision = 12, scale = 2)
    private BigDecimal globalDiscountAmount = BigDecimal.ZERO;

    /**
     * Tax rate percentage (e.g. 21.00 for 21% IVA).
     */
    @NotNull
    @PositiveOrZero
    @Column(name = "tax_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal taxRate = new BigDecimal("21.00");

    /**
     * Tax amount in euros (€).
     */
    @NotNull
    @PositiveOrZero
    @Column(name = "tax_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    /**
     * Final total in euros (€).
     */
    @NotNull
    @PositiveOrZero
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "terms_and_conditions", columnDefinition = "TEXT")
    private String termsAndConditions;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Budget() {
    }

    public Budget(String budgetNumber, String title, LocalDate issueDate, Client client) {
        this.budgetNumber = budgetNumber;
        this.title = title;
        this.issueDate = issueDate != null ? issueDate : LocalDate.now();
        this.validUntil = this.issueDate.plusDays(30);
        this.client = client;
        if (client != null) {
            syncClientSnapshot(client);
        }
        this.status = BudgetStatus.DRAFT;
    }

    /**
     * Synchronizes client details to the snapshot fields.
     */
    public void syncClientSnapshot(Client client) {
        if (client != null) {
            this.clientNameSnapshot = client.getName();
            this.clientTaxIdSnapshot = client.getTaxId();
            this.clientPhoneSnapshot = client.getPhone();
            this.clientEmailSnapshot = client.getEmail();
            this.clientAddressSnapshot = client.getAddress();
        }
    }

    /**
     * Adds an item and binds it to this budget.
     */
    public void addItem(BudgetItem item) {
        if (item != null) {
            items.add(item);
            item.setBudget(this);
            calculateTotals();
        }
    }

    /**
     * Removes an item and unbinds it.
     */
    public void removeItem(BudgetItem item) {
        if (item != null) {
            items.remove(item);
            item.setBudget(null);
            calculateTotals();
        }
    }

    /**
     * Recalculates subtotal, discount, tax, and final total based on all line items.
     */
    public void calculateTotals() {
        BigDecimal sumItems = BigDecimal.ZERO;
        if (items != null) {
            for (BudgetItem item : items) {
                if (item.getTotalPrice() == null) {
                    item.calculateTotalPrice();
                }
                sumItems = sumItems.add(item.getTotalPrice());
            }
        }
        this.subtotal = sumItems.setScale(2, RoundingMode.HALF_UP);

        if (this.globalDiscountPercentage == null) {
            this.globalDiscountPercentage = BigDecimal.ZERO;
        }

        this.globalDiscountAmount = this.subtotal.multiply(
                this.globalDiscountPercentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
        ).setScale(2, RoundingMode.HALF_UP);

        BigDecimal taxableBase = this.subtotal.subtract(this.globalDiscountAmount);
        if (taxableBase.compareTo(BigDecimal.ZERO) < 0) {
            taxableBase = BigDecimal.ZERO;
        }

        if (this.taxRate == null) {
            this.taxRate = new BigDecimal("21.00");
        }

        this.taxAmount = taxableBase.multiply(
                this.taxRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
        ).setScale(2, RoundingMode.HALF_UP);

        this.total = taxableBase.add(this.taxAmount).setScale(2, RoundingMode.HALF_UP);
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.issueDate == null) {
            this.issueDate = LocalDate.now();
        }
        if (this.validUntil == null) {
            this.validUntil = this.issueDate.plusDays(30);
        }
        if (this.status == null) {
            this.status = BudgetStatus.DRAFT;
        }
        if (this.client != null && (this.clientNameSnapshot == null || this.clientNameSnapshot.isBlank())) {
            syncClientSnapshot(this.client);
        }
        calculateTotals();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        calculateTotals();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBudgetNumber() {
        return budgetNumber;
    }

    public void setBudgetNumber(String budgetNumber) {
        this.budgetNumber = budgetNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    public BudgetStatus getStatus() {
        return status;
    }

    public void setStatus(BudgetStatus status) {
        this.status = status;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
        if (client != null) {
            syncClientSnapshot(client);
        }
    }

    public String getClientNameSnapshot() {
        return clientNameSnapshot;
    }

    public void setClientNameSnapshot(String clientNameSnapshot) {
        this.clientNameSnapshot = clientNameSnapshot;
    }

    public String getClientTaxIdSnapshot() {
        return clientTaxIdSnapshot;
    }

    public void setClientTaxIdSnapshot(String clientTaxIdSnapshot) {
        this.clientTaxIdSnapshot = clientTaxIdSnapshot;
    }

    public String getClientPhoneSnapshot() {
        return clientPhoneSnapshot;
    }

    public void setClientPhoneSnapshot(String clientPhoneSnapshot) {
        this.clientPhoneSnapshot = clientPhoneSnapshot;
    }

    public String getClientEmailSnapshot() {
        return clientEmailSnapshot;
    }

    public void setClientEmailSnapshot(String clientEmailSnapshot) {
        this.clientEmailSnapshot = clientEmailSnapshot;
    }

    public String getClientAddressSnapshot() {
        return clientAddressSnapshot;
    }

    public void setClientAddressSnapshot(String clientAddressSnapshot) {
        this.clientAddressSnapshot = clientAddressSnapshot;
    }

    public List<BudgetItem> getItems() {
        return items;
    }

    public void setItems(List<BudgetItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        for (BudgetItem item : this.items) {
            item.setBudget(this);
        }
        calculateTotals();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getGlobalDiscountPercentage() {
        return globalDiscountPercentage;
    }

    public void setGlobalDiscountPercentage(BigDecimal globalDiscountPercentage) {
        this.globalDiscountPercentage = globalDiscountPercentage;
        calculateTotals();
    }

    public BigDecimal getGlobalDiscountAmount() {
        return globalDiscountAmount;
    }

    public void setGlobalDiscountAmount(BigDecimal globalDiscountAmount) {
        this.globalDiscountAmount = globalDiscountAmount;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
        calculateTotals();
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
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
