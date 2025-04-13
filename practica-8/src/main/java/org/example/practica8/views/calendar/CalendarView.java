package org.example.practica8.views.calendar;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.NonNull;
import org.example.practica8.entities.Event;
import org.example.practica8.services.EventService;
import org.example.practica8.services.UserInfoService;
import org.vaadin.stefan.fullcalendar.*;
import org.vaadin.stefan.fullcalendar.dataprovider.AbstractEntryProvider;
import org.vaadin.stefan.fullcalendar.dataprovider.EntryQuery;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

@Tag("calendar-view")
public class CalendarView extends VerticalLayout {
    private final transient EventService eventService;
    private final transient UserInfoService userInfoService;
    private final FullCalendar calendar;

    public CalendarView(EventService eventService, UserInfoService userInfoService) {
        this.eventService = eventService;
        this.userInfoService = userInfoService;

        calendar = FullCalendarBuilder.create().build();
        configureCalendar();

        setSizeFull();
        add(calendar);
        setFlexGrow(1, calendar);
    }

    private void configureCalendar() {
        calendar.setSizeFull();
        calendar.setTimeslotsSelectable(true);
        calendar.setLocale(Locale.US);
        calendar.setTimezone(Timezone.getSystem());
        calendar.addTimeslotsSelectedListener(this::showCreateEventDialog);
        calendar.addEntryClickedListener(this::showUpdateEventDialog);
        calendar.setPrefetchEnabled(true);
        calendar.setEntryProvider(new BackendEntryProvider(eventService));
    }

    private void showUpdateEventDialog(EntryClickedEvent entryClickedEvent) {
        EventDialog eventDialog = new EventDialog(eventService, userInfoService, this::addEventToCalendar);
        eventDialog.open(null, entryClickedEvent.getEntry());
    }

    private void addEventToCalendar(Event event) {
        Entry entry = new Entry(String.valueOf(event.getId()));
        entry.setTitle(event.getTitle());
        entry.setDescription(event.getDescription());
        entry.setStart(event.getStartDate());
        entry.setEnd(event.getEndDate());
        calendar.getEntryProvider().refreshItem(entry);
    }

    private void showCreateEventDialog(TimeslotsSelectedEvent slot) {
        LocalDate selectedDate = slot.getStart().toLocalDate();
        EventDialog eventDialog = new EventDialog(eventService, userInfoService, this::addEventToCalendar);
        eventDialog.open(selectedDate, null);
    }

    private static class BackendEntryProvider extends AbstractEntryProvider<Entry> {
        private final EventService service;

        public BackendEntryProvider(EventService service) {
            this.service = service;
        }

        @Override
        public Stream<Entry> fetch(@NonNull EntryQuery query) {
            return service.streamEntries(query);
        }

        @Override
        public Optional<Entry> fetchById(@NonNull String id) {
            return service.getEntry(id);
        }
    }
}