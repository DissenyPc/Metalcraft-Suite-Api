package com.carpinteria.api.repository;

import com.carpinteria.api.model.BudgetItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetItemRepository extends JpaRepository<BudgetItem, Long> {

    List<BudgetItem> findByBudgetIdOrderByItemOrderAsc(Long budgetId);

    void deleteByBudgetId(Long budgetId);
}
