package com.carpinteria.api.repository;

import com.carpinteria.api.model.IronProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IronProfileRepository extends JpaRepository<IronProfile, Long> {

    Optional<IronProfile> findByReference(String reference);

    List<IronProfile> findBySeriesIgnoreCase(String series);

    List<IronProfile> findByActiveTrue();

    @Query("SELECT DISTINCT i.series FROM IronProfile i WHERE i.series IS NOT NULL ORDER BY i.series")
    List<String> findDistinctSeries();

    @Query("SELECT i FROM IronProfile i WHERE " +
           "LOWER(i.reference) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(i.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(i.series) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<IronProfile> search(@Param("query") String query);
}
