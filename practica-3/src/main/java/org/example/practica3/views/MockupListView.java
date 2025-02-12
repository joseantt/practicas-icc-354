package org.example.practica3.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import org.example.practica3.entities.Mockup;
import org.example.practica3.entities.Project;
import org.example.practica3.services.MockupService;
import org.example.practica3.services.ProjectService;

import java.util.Optional;

@Route(value = "project-management/:projectId/mockups", layout = MainLayout.class)
@PageTitle("Mockup List | MockupAPP")
@PermitAll
public class MockupListView extends VerticalLayout implements BeforeEnterObserver {
    private final Grid<Mockup> grid = new Grid<>(Mockup.class);
    private final MockupService mockupService;
    private final ProjectService projectService;
    private Project project;
    private final H3 title;

    public MockupListView(MockupService mockupService, ProjectService projectService) {
        this.mockupService = mockupService;
        this.projectService = projectService;

        title = new H3("Mockup List");

        // Configurar el grid
        configureGrid();

        // Botón para crear nuevo mockup
        Button createButton = new Button("Create New Mockup", VaadinIcon.PLUS.create());
        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createButton.addClickListener(e -> navigateToCreate());

        add(title, createButton, grid);
    }

    private void configureGrid() {
        grid.removeAllColumns();

        // Configurar columnas
        grid.addColumn(Mockup::getName).setHeader("Name").setSortable(true);
        grid.addColumn(Mockup::getPath).setHeader("Path").setSortable(true);
        grid.addColumn(Mockup::getAccessMethod).setHeader("Method").setSortable(true);
        grid.addColumn(Mockup::getResponseCode).setHeader("Response Code").setSortable(true);
        grid.addColumn(Mockup::getContentType).setHeader("Content Type");
        grid.addColumn(Mockup::getExpirationTimeInHours).setHeader("Expiration (hours)");

        // Columna de acciones
        grid.addComponentColumn(mockup -> {
            HorizontalLayout actions = new HorizontalLayout();

            Button editButton = new Button(VaadinIcon.EDIT.create());
            editButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_PRIMARY);
            editButton.addClickListener(e -> navigateToEdit(mockup));

            Button deleteButton = new Button(VaadinIcon.TRASH.create());
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> confirmDelete(mockup));

            actions.add(editButton, deleteButton);
            return actions;
        }).setHeader("Actions").setFlexGrow(0);
    }

    private void confirmDelete(Mockup mockup) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setCloseOnEsc(false);
        confirmDialog.setCloseOnOutsideClick(false);

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add("Are you sure you want to delete this mockup?");

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button confirmButton = new Button("Delete", e -> {
            deleteMockup(mockup);
            confirmDialog.close();
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> confirmDialog.close());
        buttonLayout.add(confirmButton, cancelButton);

        dialogLayout.add(buttonLayout);
        confirmDialog.add(dialogLayout);
        confirmDialog.open();
    }

    private void deleteMockup(Mockup mockup) {
        mockupService.deleteMockup(mockup.getId());

        // Recargar el proyecto con sus mockups actualizados
        projectService.findByProjectIdWithMockups(project.getId()).ifPresent(updatedProject -> {
            this.project = updatedProject;
            grid.setItems(updatedProject.getMockups());
        });

        Notification.show("Mockup deleted successfully", 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void navigateToCreate() {
        UI.getCurrent().navigate(
                MockupFormView.class,
                new RouteParameters("projectId", String.valueOf(project.getId()))
        );
    }

    private void navigateToEdit(Mockup mockup) {
        UI.getCurrent().navigate(
                MockupFormView  .class,
                new RouteParameters(
                        new RouteParam("projectId", String.valueOf(project.getId())),
                        new RouteParam("mockupId", mockup.getId().toString())
                )
        );
    }

    private void refreshGrid() {
        if (project != null) {
            grid.setItems(project.getMockups());
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        var projectIdParam = event.getRouteParameters().get("projectId");

        if (projectIdParam.isEmpty()) {
            event.rerouteTo(MainLayout.class);
            return;
        }

        Optional<Project> projectOpt = projectService.findByProjectIdWithMockups(Long.parseLong(projectIdParam.get()));
        if (projectOpt.isEmpty()) {
            event.rerouteTo(MainLayout.class);
            return;
        }

        this.project = projectOpt.get();
        title.setText("Mockup List - Project: " + project.getName());

        refreshGrid();
    }
}