package org.example.practica3.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import org.example.practica3.entities.Project;
import org.example.practica3.entities.UserDetailsImpl;
import org.example.practica3.entities.UserInfo;
import org.example.practica3.services.ProjectService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

@PermitAll
@Route(value = "project-management", layout = MainLayout.class)
@PageTitle("Projects | MockupAPP")
public class ProjectManagementView extends VerticalLayout implements HasUrlParameter<String> {
    private final ProjectService projectService;
    private final Grid<Project> grid = new Grid<>(Project.class);
    private final TextField searchField = new TextField();
    private Dialog projectDialog;
    private List<Project> projects;
    private String actualUserRole;

    public ProjectManagementView(ProjectService projectService) {
        this.projectService = projectService;

        // Configuración inicial
        configureSearchField();
        configureProjectDialog();

        // Layout principal
        H3 title = new H3("Gestión de Proyectos");
        Button addButton = new Button("Nuevo Proyecto", new Icon(VaadinIcon.PLUS));
        addButton.addClickListener(e -> showProjectDialog(new Project()));

        HorizontalLayout toolbar = new HorizontalLayout(searchField, addButton);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        add(title, toolbar, grid);
        setSizeFull();

    }

    private void configureGrid() {
        grid.removeAllColumns();

        // Configuración general del grid
        grid.getStyle()
                .set("--lumo-space-xs", "0.25rem")
                .set("--lumo-space-s", "0.5rem")
                .set("--lumo-space-m", "0.75rem")
                .set("--lumo-size-xs", "1.5rem")
                .set("--lumo-size-s", "1.75rem")
                .set("--lumo-row-padding-s", "0.25rem");

        // Establecer tema compacto
        grid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_BORDER);
        grid.setAllRowsVisible(true);

        // Columna de nombre más compacta
        grid.addColumn(Project::getName)
                .setHeader("Nombre")
                .setFlexGrow(1);

        if(actualUserRole.equals("ROLE_ADMIN")) {
            grid.addColumn(project -> project.getUserInfo().getUsername())
                    .setHeader("Creator")
                    .setFlexGrow(1);
        }

        // Columna de acciones más compacta
        grid.addComponentColumn(project -> {
                    HorizontalLayout actions = new HorizontalLayout();
                    actions.setSpacing(false);
                    actions.setPadding(false);
                    actions.setAlignItems(Alignment.CENTER);

                    Button viewMockupsButton = new Button(new Icon(VaadinIcon.LIST));
                    viewMockupsButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SUCCESS);
                    viewMockupsButton.getElement().setAttribute("title", "Ver mockups");
                    viewMockupsButton.addClickListener(e -> {
                        UI.getCurrent().navigate(
                                MockupListView.class,
                                new RouteParameters("projectId", String.valueOf(project.getId()))
                        );
                    });

                    Button editButton = new Button(new Icon(VaadinIcon.EDIT));
                    editButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_PRIMARY);
                    editButton.getElement().setAttribute("title", "Editar");
                    editButton.addClickListener(e -> showProjectDialog(project));

                    Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
                    deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
                    deleteButton.getElement().setAttribute("title", "Eliminar");
                    deleteButton.addClickListener(e -> confirmDelete(project));

                    // Configurar los botones para que sean más pequeños
                    for (Component button : new Component[]{viewMockupsButton, editButton, deleteButton}) {
                        button.getElement().getStyle()
                                .set("min-width", "var(--lumo-size-xs)")
                                .set("width", "var(--lumo-size-xs)")
                                .set("height", "var(--lumo-size-xs)")
                                .set("margin", "0")
                                .set("padding", "0");
                    }

                    actions.add(viewMockupsButton, editButton, deleteButton);
                    actions.getStyle()
                            .set("gap", "0.25rem")
                            .set("margin", "0")
                            .set("padding", "0");
                    return actions;
                }).setHeader("Acciones")
                .setWidth("130px")
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.CENTER);
    }

    private void confirmDelete(Project project) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setCloseOnEsc(false);
        confirmDialog.setCloseOnOutsideClick(false);

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add("¿Está seguro que desea eliminar este proyecto?");

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button confirmButton = new Button("Eliminar", e -> {
            deleteProject(project);
            confirmDialog.close();
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancelar", e -> confirmDialog.close());
        buttonLayout.add(confirmButton, cancelButton);

        dialogLayout.add(buttonLayout);
        confirmDialog.add(dialogLayout);
        confirmDialog.open();
    }

    private void configureSearchField() {
        searchField.setPlaceholder("Buscar proyecto...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setClearButtonVisible(true);
        searchField.addValueChangeListener(e -> {
            String searchTerm = e.getValue().toLowerCase();

            if (searchTerm.isEmpty()) {
                grid.setItems(projects);
            } else {
                grid.setItems(
                        projects.stream()
                                .filter(project ->
                                        project.getName().toLowerCase().contains(searchTerm))
                                .collect(Collectors.toList())
                );
            }
        });
    }

    private void configureProjectDialog() {
        projectDialog = new Dialog();
        projectDialog.setHeaderTitle("Proyecto");

        // Los componentes del diálogo se configurarán cuando se muestre
    }

    private void showProjectDialog(Project project) {
        // Configurar el formulario
        TextField nameField = new TextField("Nombre");
        nameField.setValue(project.getName() != null ? project.getName() : "");

        Button saveButton = new Button("Guardar", e -> {
            project.setName(nameField.getValue());
            saveProject(project);
            projectDialog.close();
        });

        Button cancelButton = new Button("Cancelar", e -> projectDialog.close());

        VerticalLayout dialogLayout = new VerticalLayout(nameField,
                new HorizontalLayout(saveButton, cancelButton));

        projectDialog.removeAll();
        projectDialog.add(dialogLayout);
        projectDialog.open();
    }

    private void showProjectDetails(Project project) {
        Dialog detailsDialog = new Dialog();
        detailsDialog.setHeaderTitle("Detalles del Proyecto: " + project.getName());

        // Aquí irían los detalles del proyecto y sus endpoints
        VerticalLayout content = new VerticalLayout();
        content.add(new H1("Endpoints"));

        // Botón para ver mockups
        Button viewMockupsButton = new Button("Ver mockups", new Icon(VaadinIcon.LIST));
        viewMockupsButton.addClickListener(e -> {
            detailsDialog.close(); // Cerramos el diálogo antes de navegar
            UI.getCurrent().navigate(
                    MockupListView.class,
                    new RouteParameters("projectId", String.valueOf(project.getId()))
            );
        });

        Button addEndpointButton = new Button("Añadir Endpoint", new Icon(VaadinIcon.PLUS));

        // Layout horizontal para los botones
        HorizontalLayout buttonLayout = new HorizontalLayout(viewMockupsButton, addEndpointButton);
        content.add(buttonLayout);

        // Placeholder para la lista de endpoints
        //Grid<Object> endpointsGrid = new Grid<>();
        //endpointsGrid.setHeight("200px");
        //content.add(endpointsGrid);

        Button deleteButton = new Button("Eliminar Proyecto", new Icon(VaadinIcon.TRASH));
        deleteButton.addClickListener(e -> {
            deleteProject(project);
            detailsDialog.close();
        });

        content.add(deleteButton);
        detailsDialog.add(content);
        detailsDialog.open();
    }

    private void saveProject(Project project) {
        // Obtener el usuario actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UserInfo currentUser = userDetails.getUserInfo();

        // Asignar el usuario al proyecto
        project.setUserInfo(currentUser);

        // Guardar el proyecto
        projectService.saveProject(project);
        updateList();
        Notification.show("Proyecto guardado correctamente");
    }

    private void deleteProject(Project project) {
        projectService.deleteProject(project);
        updateList();
        Notification.show("Proyecto eliminado correctamente");
    }

    private void updateList() {
        // Use Authentication to get the user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the principal is of the correct type or convert it
        Object principal = authentication.getPrincipal();
        UserInfo currentUser;

        if (principal instanceof UserDetailsImpl) {
            // If it's UserDetailsImpl, you might need to extract UserInfo
            currentUser = ((UserDetailsImpl) principal).getUserInfo();
        } else if (principal instanceof UserInfo) {
            currentUser = (UserInfo) principal;
        } else {
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
        }

        projects = actualUserRole.equals("ROLE_ADMIN") ? projectService.getAllProjects()
                                                       : projectService.findByUserId(currentUser.getId());

        grid.setItems(projects);
        //grid.setItems(projectService.findByUserId(currentUser.getId()));
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        QueryParameters queryParameters = event.getLocation().getQueryParameters();
        boolean mockupWasCreated = queryParameters.getParameters().containsKey("success");

        if (mockupWasCreated) {
            Notification notification = Notification.show("Mockup creado correctamente");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.BOTTOM_END);
        }

        var authContext = SecurityContextHolder.getContext().getAuthentication();
        this.actualUserRole = authContext.getAuthorities().stream().findFirst().get().getAuthority();

        configureGrid();
        updateList();
    }
}