package com.carpinteria.api.repository;

import com.carpinteria.api.model.Budget;
import com.carpinteria.api.model.BudgetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByBudgetNumber(String budgetNumber);

    List<Budget> findByStatus(BudgetStatus status);

    List<Budget> findByClientId(Long clientId);

    List<Budget> findByIssueDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT b FROM Budget b WHERE " +
           "LOWER(b.budgetNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.clientNameSnapshot) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.clientTaxIdSnapshot) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Budget> search(@Param("query") String query);
}
