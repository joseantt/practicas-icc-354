package org.example.practica8.views.calendar;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.NonNull;
import org.example.practica8.entities.Event;
import org.example.practica8.services.EventService;
import org.example.practica8.services.UserInfoService;
import org.example.practica8.views.MainLayout;
import org.vaadin.stefan.fullcalendar.*;
import org.vaadin.stefan.fullcalendar.dataprovider.AbstractEntryProvider;
import org.vaadin.stefan.fullcalendar.dataprovider.EntryQuery;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Calendar | Time Grid")
@PermitAll
public class CalendarView extends VerticalLayout {
    private final transient EventService eventService;
    private final transient UserInfoService userInfoService;
    private final FullCalendar calendar;
    private MenuItem viewDate;

    public CalendarView(EventService eventService, UserInfoService userInfoService) {
        this.eventService = eventService;
        this.userInfoService = userInfoService;

        calendar = FullCalendarBuilder.create().build();
        configureCalendar();

        MenuBar navigationBar = createNavigationBar();
        navigationBar.getStyle().set("margin", "10px");

        HorizontalLayout navContainer = new HorizontalLayout(navigationBar);
        navContainer.setWidthFull();
        navContainer.setJustifyContentMode(JustifyContentMode.CENTER);
        navContainer.setPadding(false);

        setSizeFull();
        add(navContainer, calendar);
        setFlexGrow(1, calendar);
    }

    private MenuBar createNavigationBar() {
        Button datePickerButton;
        MenuBar dateMenuBar = new MenuBar();
        dateMenuBar.addThemeVariants(MenuBarVariant.LUMO_SMALL);

        dateMenuBar.addItem(VaadinIcon.ANGLE_LEFT.create(), e -> calendar.previous())
                .setId("period-previous-button");

        viewDate = dateMenuBar.addItem("Current month");

        dateMenuBar.addItem(VaadinIcon.ANGLE_RIGHT.create(), e -> calendar.next());

        datePickerButton = new Button(VaadinIcon.CALENDAR.create());
        datePickerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);

        return dateMenuBar;
    }

    private void configureCalendar() {
        calendar.setSizeFull();
        calendar.setTimeslotsSelectable(true);
        calendar.setLocale(Locale.US);
        calendar.setTimezone(Timezone.getSystem());
        calendar.setNowIndicatorShown(true);
        calendar.addTimeslotsSelectedListener(this::showCreateEventDialog);
        calendar.addEntryClickedListener(this::showUpdateEventDialog);
        calendar.setPrefetchEnabled(true);
        calendar.setEntryProvider(new BackendEntryProvider(eventService));

        calendar.addDatesRenderedListener(event -> updateIntervalLabel(event.getIntervalStart()));
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

    private void updateIntervalLabel(LocalDate date) {
        viewDate.setText(date.format(DateTimeFormatter.ofPattern("MMMM yyyy").withLocale(Locale.US)));
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