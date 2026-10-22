package com.carpinteria.api.repository;

import com.carpinteria.api.model.Budget;
import com.carpinteria.api.model.BudgetStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    @Override
    @EntityGraph(attributePaths = {"items", "client"})
    List<Budget> findAll();

    @Override
    @EntityGraph(attributePaths = {"items", "client"})
    Optional<Budget> findById(Long id);

    @EntityGraph(attributePaths = {"items", "client"})
    Optional<Budget> findByBudgetNumber(String budgetNumber);

    @EntityGraph(attributePaths = {"items", "client"})
    List<Budget> findByStatus(BudgetStatus status);

    @EntityGraph(attributePaths = {"items", "client"})
    List<Budget> findByClientId(Long clientId);

    @EntityGraph(attributePaths = {"items", "client"})
    List<Budget> findByIssueDateBetween(LocalDate start, LocalDate end);

    @EntityGraph(attributePaths = {"items", "client"})
    @Query("SELECT b FROM Budget b WHERE " +
           "LOWER(b.budgetNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.clientNameSnapshot) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.clientTaxIdSnapshot) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Budget> search(@Param("query") String query);
}
