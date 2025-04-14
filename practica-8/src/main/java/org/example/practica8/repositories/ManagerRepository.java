package org.example.practica8.repositories;

import org.example.practica8.entities.Manager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {

    @Query("SELECT m FROM Manager m WHERE " +
            "LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(m.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Manager> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);
}