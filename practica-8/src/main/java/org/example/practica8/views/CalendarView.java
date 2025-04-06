package org.example.practica8.views;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;

import java.time.LocalDate;

@Tag("calendar-view")
public class CalendarView extends VerticalLayout {

    private FullCalendar calendar = FullCalendarBuilder.create().build();

    public CalendarView() {
        setSizeFull();
        calendar.setSizeFull();
        add(calendar);
        setFlexGrow(1, calendar);

        Entry entry = new Entry();
        entry.setTitle("Some event");
        entry.setColor("#ff3333");

        entry.setStart(LocalDate.now().withDayOfMonth(3).atTime(10, 0));
        entry.setEnd(entry.getStart().plusHours(2));

        calendar.getEntryProvider().asInMemory().addEntries(entry);
    }
}
