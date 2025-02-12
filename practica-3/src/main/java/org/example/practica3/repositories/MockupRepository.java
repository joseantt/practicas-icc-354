package org.example.practica3.repositories;

import org.example.practica3.entities.Mockup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MockupRepository extends JpaRepository<Mockup, Long> {
    Mockup findByPathAndAccessMethod(String path, String method);
}
