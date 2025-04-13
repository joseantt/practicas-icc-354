package org.example.practica8.views.calendar;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import org.example.practica8.entities.Event;
import org.example.practica8.services.EventService;
import org.vaadin.stefan.fullcalendar.Entry;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.function.Consumer;

public class EventDialog {
    private final EventService eventService;
    private final Consumer<Event> eventSavedCallback;
    private final Binder<Event> binder;
    private final Dialog dialog;
    private final TextField title;
    private final TextArea description;
    private final DatePicker startDate;
    private final TimePicker startTime;
    private final DatePicker endDate;
    private final TimePicker endTime;

    private Event currentEvent;

    public EventDialog(EventService eventService, Consumer<Event> eventSavedCallback) {
        this.eventService = eventService;
        this.eventSavedCallback = eventSavedCallback;

        binder = new Binder<>(Event.class);
        dialog = new Dialog();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);

        title = new TextField("Title");
        description = new TextArea("Description");
        startDate = new DatePicker("From");
        startTime = new TimePicker("Start time");
        endDate = new DatePicker("To");
        endTime = new TimePicker("End time");

        description.setMaxRows(4);

        configureBindings();

        FormLayout formLayout = createFormLayout();
        HorizontalLayout buttonLayout = createButtonLayout();

        dialog.add(formLayout, buttonLayout);
    }

    public void open(LocalDate selectedDate, Entry entry) {
        currentEvent = null;

        if(selectedDate != null) {
            startDate.setValue(selectedDate);
            endDate.setValue(selectedDate);
        }

        if (entry != null) {
            setEvent(entry);
        }

        dialog.open();
    }

    private FormLayout createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(
                title,
                description,
                startDate, startTime,
                endDate, endTime
        );
        formLayout.setColspan(title, 2);
        formLayout.setColspan(description, 2);
        return formLayout;
    }

    private HorizontalLayout createButtonLayout() {
        Button saveButton = new Button("Save", click -> saveEvent());
        saveButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        saveButton.setIcon(VaadinIcon.CHECK.create());
        saveButton.setIconAfterText(true);

        Button cancelButton = new Button("Cancel", click -> dialog.close());

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton);
        buttonLayout.getStyle().set("margin-top", "10px");
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        return buttonLayout;
    }

    private void setEvent(Entry entry) {
        Event event = eventService.getEventById(Long.valueOf(entry.getId()));
        currentEvent = event;

        title.setValue(event.getTitle());
        description.setValue(event.getDescription());
        startDate.setValue(event.getStartDate().toLocalDate());
        startTime.setValue(event.getStartDate().toLocalTime());
        endDate.setValue(event.getEndDate().toLocalDate());
        endTime.setValue(event.getEndDate().toLocalTime());
    }

    private void saveEvent() {
        boolean isNew = currentEvent == null;

        if (currentEvent == null){
            currentEvent = new Event();
        }

        if (!binder.writeBeanIfValid(currentEvent)) {
            return;
        }

        eventService.saveEvent(currentEvent);
        eventSavedCallback.accept(currentEvent);

        String message = isNew ? "Event created successfully" : "Event updated successfully";
        Notification.show(message).addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        dialog.close();
        binder.readBean(null);
    }

    private void configureBindings() {
        binder.forField(title)
                .asRequired("Title is required")
                .bind(Event::getTitle, Event::setTitle);

        binder.forField(description)
                .bind(Event::getDescription, Event::setDescription);

        binder.forField(startDate)
                .asRequired("Start date is required")
                .bind(
                        event -> event.getStartDate() != null ? event.getStartDate().toLocalDate() : null,
                        (event, date) -> {
                            LocalTime time = Optional.ofNullable(startTime.getValue())
                                    .orElse(LocalTime.now());
                            event.setStartDate(LocalDateTime.of(date, time));
                        }
                );

        binder.forField(startTime)
                .asRequired("Start time is required")
                .bind(
                        event -> event.getStartDate() != null ? event.getStartDate().toLocalTime() : null,
                        (event, time) -> {
                            LocalDate date = Optional.ofNullable(startDate.getValue())
                                    .orElse(LocalDate.now());
                            event.setStartDate(LocalDateTime.of(date, time));
                        }
                );

        binder.forField(endDate)
                .asRequired("End date is required")
                .bind(
                        event -> event.getEndDate() != null ? event.getEndDate().toLocalDate() : null,
                        (event, date) -> {
                            LocalTime time = Optional.ofNullable(endTime.getValue())
                                    .orElse(LocalTime.now());
                            event.setEndDate(LocalDateTime.of(date, time));
                        }
                );

        binder.forField(endTime)
                .asRequired("End time is required")
                .bind(
                        event -> event.getEndDate() != null ? event.getEndDate().toLocalTime() : null,
                        (event, time) -> {
                            LocalDate date = Optional.ofNullable(endDate.getValue())
                                    .orElse(LocalDate.now());
                            event.setEndDate(LocalDateTime.of(date, time));
                        }
                );
    }
}