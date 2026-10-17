package com.carpinteria.api;

import com.carpinteria.api.dto.DuplicateBudgetRequest;
import com.carpinteria.api.model.Budget;
import com.carpinteria.api.model.BudgetItem;
import com.carpinteria.api.model.BudgetStatus;
import com.carpinteria.api.model.Client;
import com.carpinteria.api.repository.BudgetItemRepository;
import com.carpinteria.api.repository.BudgetRepository;
import com.carpinteria.api.repository.ClientRepository;
import com.carpinteria.api.service.BudgetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private BudgetItemRepository itemRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private BudgetService budgetService;

    private Budget budget;
    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client(
                "Juan Pérez",
                "Construcciones Pérez SL",
                "B-12345678",
                "juan@example.com",
                "600112233",
                "Calle Real 10",
                "Sevilla",
                "41001",
                "Sevilla"
        );
        client.setId(1L);

        budget = new Budget("PRE-2026-0001", "Ventanas vivienda", LocalDate.now(), client);
        budget.setId(10L);

        BudgetItem item1 = new BudgetItem(
                1,
                "ALUMINIO",
                "AL-EUR-40",
                "Ventana Corredera 120x100",
                new BigDecimal("2.00"),
                "UD",
                new BigDecimal("150.00"),
                new BigDecimal("10.00"), // 10% discount -> 270.00
                "Ventana serie europea"
        );

        BudgetItem item2 = new BudgetItem(
                2,
                "VIDRIO",
                "FILL-CRIS-CLIM",
                "Cristal Climalit 4/12/4",
                new BigDecimal("2.40"),
                "M2",
                new BigDecimal("50.00"),
                BigDecimal.ZERO, // 120.00
                "Doble acristalamiento"
        );

        budget.addItem(item1);
        budget.addItem(item2);
        budget.calculateTotals();
    }

    @Test
    void testCalculateTotals() {
        // item1: 2 * 150 * 0.9 = 270.00
        // item2: 2.4 * 50 = 120.00
        // Subtotal: 390.00
        assertEquals(new BigDecimal("390.00"), budget.getSubtotal());

        // 21% IVA of 390 = 81.90
        assertEquals(new BigDecimal("81.90"), budget.getTaxAmount());

        // Total: 390 + 81.90 = 471.90
        assertEquals(new BigDecimal("471.90"), budget.getTotal());
    }

    @Test
    void testDuplicateBudget() {
        when(budgetRepository.findById(10L)).thenReturn(Optional.of(budget));
        when(budgetRepository.save(any(Budget.class))).thenAnswer(invocation -> {
            Budget saved = invocation.getArgument(0);
            saved.setId(20L);
            return saved;
        });

        DuplicateBudgetRequest request = new DuplicateBudgetRequest();
        request.setNewBudgetNumber("PRE-2026-0002");
        request.setNewTitle("Ventanas vivienda (Duplicado)");

        Budget duplicated = budgetService.duplicateBudget(10L, request);

        assertNotNull(duplicated);
        assertEquals("PRE-2026-0002", duplicated.getBudgetNumber());
        assertEquals("Ventanas vivienda (Duplicado)", duplicated.getTitle());
        assertEquals(BudgetStatus.DRAFT, duplicated.getStatus());
        assertEquals(2, duplicated.getItems().size());
        assertEquals(new BigDecimal("390.00"), duplicated.getSubtotal());
        assertEquals(new BigDecimal("471.90"), duplicated.getTotal());
        assertEquals("Juan Pérez", duplicated.getClientNameSnapshot());
    }
}
