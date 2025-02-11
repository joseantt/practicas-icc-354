package org.example.practica3.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
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
public class ProjectManagementView extends VerticalLayout {
    private final ProjectService projectService;
    private final Grid<Project> grid = new Grid<>(Project.class);
    private final TextField searchField = new TextField();
    private Dialog projectDialog;
    private List<Project> originalProjects;

    public ProjectManagementView(ProjectService projectService) {
        this.projectService = projectService;

        // Configuración inicial
        configureGrid();
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

        // Cargar datos iniciales
        updateList();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.setColumns("name");
        grid.addColumn(project -> "0").setHeader("Endpoints");  // Placeholder para endpoints

        // Columna de acciones
        grid.addComponentColumn(project -> {
            HorizontalLayout actions = new HorizontalLayout();

            Button editButton = new Button(new Icon(VaadinIcon.EDIT));
            editButton.addClickListener(e -> showProjectDialog(project));

            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addClickListener(e -> deleteProject(project));

            Button detailsButton = new Button(new Icon(VaadinIcon.EXTERNAL_LINK));
            detailsButton.addClickListener(e -> showProjectDetails(project));

            actions.add(editButton, detailsButton, deleteButton);
            return actions;
        }).setHeader("Acciones");
    }

    private void configureSearchField() {
        searchField.setPlaceholder("Buscar proyecto...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setClearButtonVisible(true);
        searchField.addValueChangeListener(e -> {
            String searchTerm = e.getValue().toLowerCase();

            if (searchTerm.isEmpty()) {
                grid.setItems(originalProjects);
            } else {
                grid.setItems(
                        originalProjects.stream()
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
        content.add(new Button("Añadir Endpoint", new Icon(VaadinIcon.PLUS)));

        // Placeholder para la lista de endpoints
        Grid<Object> endpointsGrid = new Grid<>();
        endpointsGrid.setHeight("200px");
        content.add(endpointsGrid);

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

        originalProjects = projectService.findByUserId(currentUser.getId());
        grid.setItems(originalProjects);
        //grid.setItems(projectService.findByUserId(currentUser.getId()));
    }
}