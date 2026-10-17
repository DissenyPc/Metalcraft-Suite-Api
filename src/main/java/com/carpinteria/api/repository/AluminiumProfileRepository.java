package com.carpinteria.api.repository;

import com.carpinteria.api.model.AluminiumProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AluminiumProfileRepository extends JpaRepository<AluminiumProfile, Long> {

    Optional<AluminiumProfile> findByReference(String reference);

    Optional<AluminiumProfile> findFirstByReference(String reference);

    List<AluminiumProfile> findBySeriesIgnoreCase(String series);

    List<AluminiumProfile> findByActiveTrue();

    @Query("SELECT DISTINCT a.series FROM AluminiumProfile a WHERE a.series IS NOT NULL ORDER BY a.series")
    List<String> findDistinctSeries();

    @Query("SELECT a FROM AluminiumProfile a WHERE " +
           "LOWER(a.reference) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.series) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.finish) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<AluminiumProfile> search(@Param("query") String query);
}
