package org.example.practica3.repositories;

import org.example.practica3.entities.Mockup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MockupRepository extends JpaRepository<Mockup, Long> {
    @Query("SELECT m FROM Mockup m LEFT JOIN FETCH m.headers WHERE m.id = :id")
    Optional<Mockup> findByIdWithHeaders(@Param("id") Long id);
    Optional<Mockup> findByPath(String path);
    Mockup findByPathAndAccessMethod(String path, String method);
}
