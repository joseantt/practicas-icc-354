package org.example.practica8.views;

import com.vaadin.flow.component.Tag;
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
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import lombok.NonNull;
import org.example.practica8.entities.Event;
import org.example.practica8.services.EventService;
import org.vaadin.stefan.fullcalendar.*;
import org.vaadin.stefan.fullcalendar.dataprovider.AbstractEntryProvider;
import org.vaadin.stefan.fullcalendar.dataprovider.EntryQuery;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

@Tag("calendar-view")
public class CalendarView extends VerticalLayout {
    private final transient EventService eventService;
    private final FullCalendar calendar;
    private final Binder<Event> binder = new Binder<>(Event.class);

    private TextField title;
    private TextArea description;
    private TimePicker startTime;
    private TimePicker endTime;
    private DatePicker startDate;
    private DatePicker endDate;

    public CalendarView(EventService eventService) {
        this.eventService = eventService;

        calendar = FullCalendarBuilder.create().build();
        configureCalendar();
        initializeFormFields();

        setSizeFull();
        add(calendar);
        setFlexGrow(1, calendar);
    }

    private void configureCalendar() {
        calendar.setSizeFull();
        calendar.setTimeslotsSelectable(true);
        calendar.setLocale(Locale.US);
        calendar.setTimezone(Timezone.getSystem());
        calendar.addTimeslotsSelectedListener(this::showEventDialog);
        calendar.setPrefetchEnabled(true);
        calendar.setEntryProvider(new BackendEntryProvider(eventService));
    }

    private void addEventToCalendar(Event event) {
        Entry entry = new Entry(String.valueOf(event.getId()));
        entry.setTitle(event.getTitle());
        entry.setDescription(event.getDescription());
        entry.setStart(event.getStartDate());
        entry.setEnd(event.getEndDate());
        calendar.getEntryProvider().refreshAll();
    }

    private void showEventDialog(TimeslotsSelectedEvent slot) {
        LocalDate selectedDate = slot.getStart().toLocalDate();

        Dialog dialog = new Dialog("Register new event");
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);

        startDate.setValue(selectedDate);
        endDate.setValue(selectedDate);

        FormLayout formLayout = createFormLayout();
        HorizontalLayout buttonLayout = createButtonLayout(dialog);

        dialog.add(formLayout, buttonLayout);
        dialog.open();
    }

    private void initializeFormFields() {
        title = new TextField("Title");
        description = new TextArea("Description");
        startDate = new DatePicker("From");
        startTime = new TimePicker("Start time");
        endDate = new DatePicker("To");
        endTime = new TimePicker("End time");

        description.setMaxRows(4);
        configureBindings();
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

    private HorizontalLayout createButtonLayout(Dialog dialog) {
        Button saveButton = new Button("Save", click -> saveEvent(dialog));
        saveButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        saveButton.setIcon(VaadinIcon.CHECK.create());
        saveButton.setIconAfterText(true);

        Button cancelButton = new Button("Cancel", click -> dialog.close());

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton);
        buttonLayout.getStyle().set("margin-top", "10px");
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        return buttonLayout;
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

    private void saveEvent(Dialog dialog) {
        Event event = new Event();

        if (!binder.writeBeanIfValid(event)) {
            return;
        }

        // TODO: Validar que no se cree un evento con la misma fecha y hora
        event.setUsername(""); // TODO: Set username from session
        eventService.saveEvent(event);
        addEventToCalendar(event);

        Notification.show("Event created successfully")
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        dialog.close();
        binder.readBean(null);
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