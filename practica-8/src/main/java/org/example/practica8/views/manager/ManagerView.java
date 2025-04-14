package org.example.practica8.views.manager;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.example.practica8.constants.Role;
import org.example.practica8.entities.Manager;
import org.example.practica8.services.ManagerService;
import org.example.practica8.views.MainLayout;
import org.example.practica8.views.components.ThreeDotsDropdown;
import org.springframework.data.domain.PageRequest;

@Route(value = "managers", layout = MainLayout.class)
@PageTitle("Manager Management | Time Grid")
@RolesAllowed(Role.ADMIN)
public class ManagerView extends VerticalLayout {

    private final ManagerService managerService;
    private final Grid<Manager> grid = new Grid<>(Manager.class, false);
    private final TextField searchField = new TextField();
    private CallbackDataProvider<Manager, Void> dataProvider;

    public ManagerView(ManagerService managerService) {
        this.managerService = managerService;
        setSizeFull();
        setPadding(true);

        configureDataProvider();
        configureGrid();

        add(
                createHeader(),
                createToolbar(),
                grid
        );
    }

    private HorizontalLayout createHeader() {
        H2 heading = new H2("Manager Management");
        heading.getStyle().set("margin", "0");

        HorizontalLayout header = new HorizontalLayout(heading);
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setPadding(true);
        return header;
    }

    private HorizontalLayout createToolbar() {
        searchField.setPlaceholder("Search...");
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> refreshGrid());
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());

        Button addButton = new Button("Add Manager");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.setIcon(VaadinIcon.PLUS.create());
        addButton.addClickListener(e -> openManagerDialog(new Manager()));

        HorizontalLayout toolbar = new HorizontalLayout(searchField, addButton);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        return toolbar;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);

        grid.addColumn(Manager::getName).setHeader("Name").setAutoWidth(true).setSortable(true);
        grid.addColumn(Manager::getEmail).setHeader("Email").setAutoWidth(true).setSortable(true);

        grid.addComponentColumn(this::createActionButtons)
                .setHeader("Actions")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.setItems(dataProvider);
    }

    private HorizontalLayout createActionButtons(Manager manager) {
        Button editButton = new Button(new Icon(VaadinIcon.EDIT));
        editButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        editButton.addClickListener(e -> openManagerDialog(manager));

        ThreeDotsDropdown dropdown = new ThreeDotsDropdown();
        dropdown.addDropDownItem(
                "Delete",
                VaadinIcon.TRASH,
                "var(--lumo-error-text-color)",
                click -> confirmDelete(manager)
        );

        HorizontalLayout actions = new HorizontalLayout(editButton, dropdown);
        actions.setSpacing(true);
        return actions;
    }

    private void confirmDelete(Manager manager) {
        ConfirmDialog dialog = new ConfirmDialog(
                "Confirm Delete",
                "Are you sure you want to delete this manager? This action cannot be undone.",
                "Delete",
                () -> {
                    managerService.delete(manager);
                    refreshGrid();
                    Notification.show("Manager deleted successfully")
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                }
        );
        dialog.open();
    }

    private void configureDataProvider() {
        dataProvider = DataProvider.fromCallbacks(
                query -> {
                    String searchTerm = searchField.getValue();
                    int offset = query.getOffset();
                    int limit = query.getLimit();

                    return managerService.findAll(searchTerm, PageRequest.of(offset / limit, limit))
                            .stream();
                },
                query -> {
                    String searchTerm = searchField.getValue();

                    return (int) managerService.findAll(searchTerm, PageRequest.of(0, 1))
                            .getTotalElements();
                }
        );
    }

    private void refreshGrid() {
        dataProvider.refreshAll();
    }

    private void openManagerDialog(Manager manager) {
        ManagerDialog dialog = new ManagerDialog(manager, managerService, this::refreshGrid);
        dialog.open();
    }
}