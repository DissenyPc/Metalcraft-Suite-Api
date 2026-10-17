package com.carpinteria.api.controller;

import com.carpinteria.api.dto.DuplicateBudgetRequest;
import com.carpinteria.api.model.Budget;
import com.carpinteria.api.model.BudgetItem;
import com.carpinteria.api.model.BudgetStatus;
import com.carpinteria.api.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService service;

    public BudgetController(BudgetService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Budget>> getAll(
            @RequestParam(required = false) BudgetStatus status,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(service.getAll(status, clientId, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Budget> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/number/{budgetNumber}")
    public ResponseEntity<Budget> getByBudgetNumber(@PathVariable String budgetNumber) {
        return ResponseEntity.ok(service.getByBudgetNumber(budgetNumber));
    }

    @PostMapping
    public ResponseEntity<Budget> create(@Valid @RequestBody Budget budget) {
        Budget created = service.create(budget);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Budget> update(
            @PathVariable Long id,
            @Valid @RequestBody Budget budget) {
        return ResponseEntity.ok(service.update(id, budget));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Budget> updateStatus(
            @PathVariable Long id,
            @RequestParam BudgetStatus status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Duplicate an existing budget with all its partidas (Budget Items),
     * optionally specifying a new reference number, title, or target client.
     */
    @PostMapping("/{id}/duplicate")
    public ResponseEntity<Budget> duplicate(
            @PathVariable Long id,
            @RequestBody(required = false) DuplicateBudgetRequest request) {
        Budget duplicated = service.duplicateBudget(id, request);
        return new ResponseEntity<>(duplicated, HttpStatus.CREATED);
    }

    // =========================================================================
    // Budget Items (Partidas) Endpoints
    // =========================================================================

    @GetMapping("/{id}/items")
    public ResponseEntity<List<BudgetItem>> getItems(@PathVariable Long id) {
        return ResponseEntity.ok(service.getBudgetItems(id));
    }

    @GetMapping("/{id}/items/{itemId}")
    public ResponseEntity<BudgetItem> getItem(
            @PathVariable Long id,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(service.getBudgetItem(id, itemId));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<BudgetItem> addItem(
            @PathVariable Long id,
            @Valid @RequestBody BudgetItem item) {
        BudgetItem created = service.addItemToBudget(id, item);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/items/{itemId}")
    public ResponseEntity<BudgetItem> updateItem(
            @PathVariable Long id,
            @PathVariable Long itemId,
            @Valid @RequestBody BudgetItem item) {
        BudgetItem updated = service.updateBudgetItem(id, itemId, item);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<Void> removeItem(
            @PathVariable Long id,
            @PathVariable Long itemId) {
        service.removeBudgetItem(id, itemId);
        return ResponseEntity.noContent().build();
    }
}
