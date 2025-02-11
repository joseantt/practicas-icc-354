package org.example.practica3.repositories;

import org.example.practica3.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Project findByName(String name);
    List<Project> findByUserInfo_Id(Long userId);
}

