package org.example.practica8.services;

import org.example.practica8.entities.Event;
import org.example.practica8.repositories.EventRepository;
import org.springframework.stereotype.Service;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.dataprovider.EntryQuery;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void saveEvent(Event event) {
        eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    public Stream<Entry> streamEntries(EntryQuery query) {
        return eventRepository
                .findEventsWithinRange(query.getStart(), query.getEnd())
                .stream().map(this::convertEventToEntry);
    }

    public Optional<Entry> getEntry(String id) {
        return eventRepository.findById(Long.valueOf(id))
                .map(this::convertEventToEntry);
    }

    private Entry convertEventToEntry(Event event) {
        Entry entry = new Entry(event.getId().toString());
        entry.setTitle(event.getTitle());
        entry.setDescription(event.getDescription());
        entry.setStart(event.getStartDate());
        entry.setEnd(event.getEndDate());
        return entry;
    }
}
