package com.carpinteria.api.repository;

import com.carpinteria.api.model.HardwareAccessory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HardwareAccessoryRepository extends JpaRepository<HardwareAccessory, Long> {

    Optional<HardwareAccessory> findByReference(String reference);

    List<HardwareAccessory> findByCategoryIgnoreCase(String category);

    List<HardwareAccessory> findByActiveTrue();

    @Query("SELECT DISTINCT h.category FROM HardwareAccessory h WHERE h.category IS NOT NULL ORDER BY h.category")
    List<String> findDistinctCategories();

    @Query("SELECT h FROM HardwareAccessory h WHERE h.active = true AND h.stockQuantity <= h.minStockAlert")
    List<HardwareAccessory> findLowStock();

    @Query("SELECT h FROM HardwareAccessory h WHERE " +
           "LOWER(h.reference) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(h.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(h.category) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(h.brand) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<HardwareAccessory> search(@Param("query") String query);
}
