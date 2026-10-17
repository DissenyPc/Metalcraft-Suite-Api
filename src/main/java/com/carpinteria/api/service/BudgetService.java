package com.carpinteria.api.service;

import com.carpinteria.api.dto.DuplicateBudgetRequest;
import com.carpinteria.api.exception.ResourceNotFoundException;
import com.carpinteria.api.model.Budget;
import com.carpinteria.api.model.BudgetItem;
import com.carpinteria.api.model.BudgetStatus;
import com.carpinteria.api.model.Client;
import com.carpinteria.api.repository.BudgetItemRepository;
import com.carpinteria.api.repository.BudgetRepository;
import com.carpinteria.api.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetItemRepository itemRepository;
    private final ClientRepository clientRepository;

    public BudgetService(BudgetRepository budgetRepository,
                         BudgetItemRepository itemRepository,
                         ClientRepository clientRepository) {
        this.budgetRepository = budgetRepository;
        this.itemRepository = itemRepository;
        this.clientRepository = clientRepository;
    }

    @Transactional(readOnly = true)
    public List<Budget> getAll(BudgetStatus status, Long clientId, String query) {
        if (query != null && !query.isBlank()) {
            return budgetRepository.search(query.trim());
        }
        if (status != null) {
            return budgetRepository.findByStatus(status);
        }
        if (clientId != null) {
            return budgetRepository.findByClientId(clientId);
        }
        return budgetRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Budget getById(Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto", "id", id));
    }

    @Transactional(readOnly = true)
    public Budget getByBudgetNumber(String budgetNumber) {
        return budgetRepository.findByBudgetNumber(budgetNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto", "número", budgetNumber));
    }

    public Budget create(Budget budget) {
        if (budget.getBudgetNumber() == null || budget.getBudgetNumber().isBlank()) {
            budget.setBudgetNumber(generateBudgetNumber());
        } else if (budgetRepository.findByBudgetNumber(budget.getBudgetNumber()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un presupuesto con el número: " + budget.getBudgetNumber());
        }

        // Link client entity if client has ID
        if (budget.getClient() != null && budget.getClient().getId() != null) {
            Client client = clientRepository.findById(budget.getClient().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", budget.getClient().getId()));
            budget.setClient(client);
            budget.syncClientSnapshot(client);
        }

        // Ensure bidirectional relationship for items
        if (budget.getItems() != null) {
            int order = 1;
            for (BudgetItem item : budget.getItems()) {
                item.setBudget(budget);
                if (item.getItemOrder() == null) {
                    item.setItemOrder(order++);
                }
                item.calculateTotalPrice();
            }
        }

        budget.calculateTotals();
        return budgetRepository.save(budget);
    }

    public Budget update(Long id, Budget updated) {
        Budget existing = getById(id);

        if (!existing.getBudgetNumber().equalsIgnoreCase(updated.getBudgetNumber())
                && budgetRepository.findByBudgetNumber(updated.getBudgetNumber()).isPresent()) {
            throw new IllegalArgumentException("Ya existe otro presupuesto con el número: " + updated.getBudgetNumber());
        }

        existing.setBudgetNumber(updated.getBudgetNumber());
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setIssueDate(updated.getIssueDate());
        existing.setValidUntil(updated.getValidUntil());
        existing.setStatus(updated.getStatus());
        existing.setGlobalDiscountPercentage(updated.getGlobalDiscountPercentage());
        existing.setTaxRate(updated.getTaxRate());
        existing.setNotes(updated.getNotes());
        existing.setTermsAndConditions(updated.getTermsAndConditions());

        // Update Client linking
        if (updated.getClient() != null && updated.getClient().getId() != null) {
            Client client = clientRepository.findById(updated.getClient().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", updated.getClient().getId()));
            existing.setClient(client);
            existing.syncClientSnapshot(client);
        } else if (updated.getClient() == null) {
            existing.setClient(null);
        }

        // If items are provided in update payload, replace items cleanly
        if (updated.getItems() != null) {
            existing.getItems().clear();
            int order = 1;
            for (BudgetItem item : updated.getItems()) {
                item.setItemOrder(item.getItemOrder() != null ? item.getItemOrder() : order++);
                item.calculateTotalPrice();
                existing.addItem(item);
            }
        }

        existing.calculateTotals();
        return budgetRepository.save(existing);
    }

    public Budget updateStatus(Long id, BudgetStatus status) {
        Budget existing = getById(id);
        existing.setStatus(status);
        return budgetRepository.save(existing);
    }

    public void delete(Long id) {
        Budget existing = getById(id);
        budgetRepository.delete(existing);
    }

    /**
     * Duplicates an existing budget along with all its line items (Partidas).
     * Creates a new unique budgetNumber and resets status to DRAFT.
     */
    public Budget duplicateBudget(Long id, DuplicateBudgetRequest request) {
        Budget original = getById(id);

        Budget copy = new Budget();

        // 1. Determine new budget reference number
        if (request != null && request.getNewBudgetNumber() != null && !request.getNewBudgetNumber().isBlank()) {
            if (budgetRepository.findByBudgetNumber(request.getNewBudgetNumber().trim()).isPresent()) {
                throw new IllegalArgumentException("El número de presupuesto ya está en uso: " + request.getNewBudgetNumber());
            }
            copy.setBudgetNumber(request.getNewBudgetNumber().trim());
        } else {
            copy.setBudgetNumber(original.getBudgetNumber() + "-COPIA-" + System.currentTimeMillis() % 100000);
        }

        // 2. Set title and dates
        if (request != null && request.getNewTitle() != null && !request.getNewTitle().isBlank()) {
            copy.setTitle(request.getNewTitle().trim());
        } else {
            copy.setTitle(original.getTitle() + " (Copia)");
        }

        copy.setDescription(original.getDescription());

        LocalDate newIssueDate = (request != null && request.getNewIssueDate() != null)
                ? request.getNewIssueDate()
                : LocalDate.now();
        copy.setIssueDate(newIssueDate);

        int validity = (request != null && request.getValidityDays() != null) ? request.getValidityDays() : 30;
        copy.setValidUntil(newIssueDate.plusDays(validity));

        copy.setStatus(BudgetStatus.DRAFT);
        copy.setGlobalDiscountPercentage(original.getGlobalDiscountPercentage());
        copy.setTaxRate(original.getTaxRate());
        copy.setNotes(original.getNotes());
        copy.setTermsAndConditions(original.getTermsAndConditions());

        // 3. Link client
        if (request != null && request.getTargetClientId() != null) {
            Client newClient = clientRepository.findById(request.getTargetClientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", request.getTargetClientId()));
            copy.setClient(newClient);
            copy.syncClientSnapshot(newClient);
        } else {
            copy.setClient(original.getClient());
            copy.setClientNameSnapshot(original.getClientNameSnapshot());
            copy.setClientTaxIdSnapshot(original.getClientTaxIdSnapshot());
            copy.setClientPhoneSnapshot(original.getClientPhoneSnapshot());
            copy.setClientEmailSnapshot(original.getClientEmailSnapshot());
            copy.setClientAddressSnapshot(original.getClientAddressSnapshot());
        }

        // 4. Deep copy all line items (Partidas)
        List<BudgetItem> clonedItems = new ArrayList<>();
        if (original.getItems() != null) {
            for (BudgetItem origItem : original.getItems()) {
                BudgetItem clonedItem = new BudgetItem(
                        origItem.getItemOrder(),
                        origItem.getItemCategory(),
                        origItem.getRelatedReference(),
                        origItem.getConcept(),
                        origItem.getQuantity(),
                        origItem.getUnit(),
                        origItem.getUnitPrice(),
                        origItem.getDiscountPercentage(),
                        origItem.getNotes()
                );
                clonedItem.setBudget(copy);
                clonedItems.add(clonedItem);
            }
        }
        copy.setItems(clonedItems);
        copy.calculateTotals();

        return budgetRepository.save(copy);
    }

    // =========================================================================
    // Budget Items (Partidas) CRUD operations
    // =========================================================================

    @Transactional(readOnly = true)
    public List<BudgetItem> getBudgetItems(Long budgetId) {
        Budget budget = getById(budgetId);
        return budget.getItems();
    }

    @Transactional(readOnly = true)
    public BudgetItem getBudgetItem(Long budgetId, Long itemId) {
        Budget budget = getById(budgetId);
        return budget.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Partida de presupuesto", "id", itemId));
    }

    public BudgetItem addItemToBudget(Long budgetId, BudgetItem item) {
        Budget budget = getById(budgetId);

        if (item.getItemOrder() == null) {
            item.setItemOrder(budget.getItems().size() + 1);
        }
        item.calculateTotalPrice();
        budget.addItem(item);
        budgetRepository.save(budget);

        return item;
    }

    public BudgetItem updateBudgetItem(Long budgetId, Long itemId, BudgetItem updatedItem) {
        Budget budget = getById(budgetId);

        BudgetItem existingItem = budget.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Partida de presupuesto", "id", itemId));

        existingItem.setItemOrder(updatedItem.getItemOrder());
        existingItem.setItemCategory(updatedItem.getItemCategory());
        existingItem.setRelatedReference(updatedItem.getRelatedReference());
        existingItem.setConcept(updatedItem.getConcept());
        existingItem.setQuantity(updatedItem.getQuantity());
        existingItem.setUnit(updatedItem.getUnit());
        existingItem.setUnitPrice(updatedItem.getUnitPrice());
        existingItem.setDiscountPercentage(updatedItem.getDiscountPercentage());
        existingItem.setNotes(updatedItem.getNotes());
        existingItem.calculateTotalPrice();

        budget.calculateTotals();
        budgetRepository.save(budget);

        return existingItem;
    }

    public void removeBudgetItem(Long budgetId, Long itemId) {
        Budget budget = getById(budgetId);

        BudgetItem existingItem = budget.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Partida de presupuesto", "id", itemId));

        budget.removeItem(existingItem);
        budgetRepository.save(budget);
    }

    private String generateBudgetNumber() {
        String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        long count = budgetRepository.count() + 1;
        return String.format("PRE-%s-%04d", year, count);
    }
}
