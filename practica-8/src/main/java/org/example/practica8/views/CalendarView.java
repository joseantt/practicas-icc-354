package org.example.practica8.views;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import org.example.practica8.entities.Event;
import org.example.practica8.services.EventService;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import org.vaadin.stefan.fullcalendar.Timezone;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

@Tag("calendar-view")
public class CalendarView extends VerticalLayout {
    private final transient EventService eventService;

    private final FullCalendar calendar = FullCalendarBuilder.create().build();

    private final TextField title = new TextField("Title");
    private final TextArea description = new TextArea("Description");
    private final TimePicker startDate = new TimePicker("From");
    private final TimePicker endDate = new TimePicker("To");
    private final Binder<Event> binder = new Binder<>(Event.class);

    public CalendarView(EventService eventService) {
        this.eventService = eventService;

        setSizeFull();
        calendar.setSizeFull();
        calendar.setTimeslotsSelectable(true);
        calendar.setLocale(Locale.US);
        calendar.setTimezone(Timezone.getSystem());
        add(calendar);
        setFlexGrow(1, calendar);

        setupListenerAddEventDialog();
    }

    private void addEventToCalendar(Event event) {
        Entry entry = new Entry();
        entry.setTitle(event.getTitle());
        entry.setStart(event.getStartDate());
        entry.setEnd(event.getEndDate());
        calendar.getEntryProvider().asInMemory().addEntries(entry);
        calendar.getEntryProvider().refreshItem(entry);
    }

    private void setupListenerAddEventDialog(){
        calendar.addTimeslotsSelectedListener(slot -> {
            Dialog dialog = new Dialog("Register new event");
            FormLayout formLayout = new FormLayout();

            setupFields(slot.getStart().toLocalDate());
            formLayout.add(title, description, startDate, endDate);
            formLayout.setColspan(title, 2);
            formLayout.setColspan(description, 2);

            Button saveButton = new Button("Save", click -> saveEvent(dialog));
            saveButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
            saveButton.setIcon(VaadinIcon.CHECK.create());
            saveButton.setIconAfterText(true);

            Button cancelButton = new Button("Cancel", e -> dialog.close());

            HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton);
            buttonLayout.getStyle().set("margin-top", "10px");
            buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

            dialog.add(formLayout, buttonLayout);
            dialog.open();
        });
    }

    private void setupFields(LocalDate date) {
        description.setMaxRows(4);
        startDate.setValue(LocalTime.now());
        endDate.setValue(LocalTime.now().plusHours(1));

        binder.forField(title)
                .asRequired("Title is required")
                .bind(Event::getTitle, Event::setTitle);
        binder.forField(description)
                .bind(Event::getDescription, Event::setDescription);
        binder.forField(startDate)
                .asRequired("Start time is required")
                .bind(event -> LocalTime.now(), (event, time) ->
                    event.setStartDate(LocalDateTime.of(date, time))
                );
        binder.forField(endDate)
                .asRequired("End time is required")
                .bind(event -> LocalTime.now(), (event, time) ->
                    event.setEndDate(LocalDateTime.of(date, time))
                );
    }

    private void saveEvent(Dialog dialog){
        Event event = new Event();

        if (!binder.writeBeanIfValid(event)) {
            binder.validate();
            return;
        }

        eventService.saveEvent(event);
        addEventToCalendar(event);

        Notification.show("Event created successfully")
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        dialog.close();
        binder.readBean(null);
    }
}
