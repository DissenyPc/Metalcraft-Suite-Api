package com.carpinteria.api.repository;

import com.carpinteria.api.model.FillingMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FillingMaterialRepository extends JpaRepository<FillingMaterial, Long> {

    Optional<FillingMaterial> findByReference(String reference);

    List<FillingMaterial> findByMaterialTypeIgnoreCase(String materialType);

    List<FillingMaterial> findByFinishColorIgnoreCase(String finishColor);

    List<FillingMaterial> findByActiveTrue();

    @Query("SELECT DISTINCT f.materialType FROM FillingMaterial f WHERE f.materialType IS NOT NULL ORDER BY f.materialType")
    List<String> findDistinctMaterialTypes();

    @Query("SELECT DISTINCT f.finishColor FROM FillingMaterial f WHERE f.finishColor IS NOT NULL ORDER BY f.finishColor")
    List<String> findDistinctFinishColors();

    @Query("SELECT f FROM FillingMaterial f WHERE " +
           "LOWER(f.reference) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.materialType) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.finishColor) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<FillingMaterial> search(@Param("query") String query);
}
