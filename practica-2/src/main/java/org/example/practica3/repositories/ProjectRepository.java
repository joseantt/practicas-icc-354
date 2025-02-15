package org.example.practica3.repositories;

import com.vaadin.flow.component.template.Id;
import org.example.practica3.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Project findByName(String name);
    List<Project> findByUserInfo_Id(Long userId);
    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.mockups WHERE p.id = :id")
    Optional<Project> findByIdWithMockups(Long id);

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.mockups WHERE p.userInfo.id = :userId")
    List<Project> findByUserIdWithMockups(@Param("userId") Long userId);
}

