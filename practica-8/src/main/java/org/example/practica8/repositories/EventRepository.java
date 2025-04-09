package org.example.practica8.repositories;

import org.example.practica8.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE (:start IS NULL OR e.endDate >= :start) AND (:end IS NULL OR e.startDate <= :end)")
    List<Event> findEventsWithinRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<Event> findByUsername(String username);
}
