package org.example.practica3.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.annotation.security.PermitAll;
import org.example.practica3.entities.Mockup;
import org.example.practica3.entities.Project;
import org.example.practica3.services.MockupService;
import org.example.practica3.services.ProjectService;
import org.example.practica3.utils.Validations;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.example.practica3.utils.Validations.userCanEnter;

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

        // Configuraciones básicas del grid
        grid.setWidthFull();
        grid.getStyle().set("word-wrap", "break-word");

        // Columnas de datos
        grid.addColumn(Mockup::getName).setHeader("Name").setAutoWidth(true);
        grid.addColumn(Mockup::getPath).setHeader("Path").setAutoWidth(true);
        grid.addColumn(Mockup::getAccessMethod).setHeader("Method").setAutoWidth(true);
        grid.addColumn(Mockup::getResponseCode).setHeader("Response Code").setAutoWidth(true);
        grid.addColumn(Mockup::getContentType).setHeader("Content Type").setAutoWidth(true);
        grid.addColumn(Mockup::getExpirationTimeInHours).setHeader("Expiration (hours)").setAutoWidth(true);

        // Columna de acciones
        grid.addComponentColumn(mockup -> {
                    HorizontalLayout actions = new HorizontalLayout();
                    actions.setSpacing(true);
                    actions.setPadding(true);
                    actions.setMargin(false);
                    actions.setAlignItems(Alignment.CENTER);
                    actions.setJustifyContentMode(JustifyContentMode.CENTER);
                    actions.setMinWidth("250px");

                    // Botón para copiar URL
                    Button copyUrlButton = new Button(new Icon(VaadinIcon.COPY));
                    copyUrlButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SUCCESS);
                    copyUrlButton.getElement().setAttribute("title", "Copiar URL");
                    copyUrlButton.addClickListener(e -> {
                        String baseUrl = getBaseUrl();
                        String fullUrl = baseUrl + "/" + mockup.getPath();
                        copyToClipboard(fullUrl);
                        Notification.show("URL copiada al portapapeles: " + fullUrl,
                                        3000, Notification.Position.BOTTOM_END)
                                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    });

                    // Botón para copiar JWT
                    Button copyJwtButton = new Button(new Icon(VaadinIcon.KEY));
                    copyJwtButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_PRIMARY);
                    copyJwtButton.getElement().setAttribute("title", "Copiar JWT");
                    copyJwtButton.setVisible(mockup.isRequiresJwt() && mockup.getJwtToken() != null);
                    copyJwtButton.addClickListener(e -> {
                        copyToClipboard(mockup.getJwtToken());
                        Notification.show("JWT copiado al portapapeles",
                                        3000, Notification.Position.BOTTOM_END)
                                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    });

                    Button editButton = new Button(new Icon(VaadinIcon.EDIT));
                    editButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_PRIMARY);
                    editButton.getElement().setAttribute("title", "Editar");
                    editButton.addClickListener(e -> navigateToEdit(mockup));

                    Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
                    deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
                    deleteButton.getElement().setAttribute("title", "Eliminar");
                    deleteButton.addClickListener(e -> confirmDelete(mockup));

                    actions.getStyle().set("gap", "8px");
                    actions.add(copyUrlButton, copyJwtButton, editButton, deleteButton);
                    return actions;
                }).setHeader("Actions")
                .setWidth("250px")
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.CENTER);
    }

    private String getBaseUrl() {

        return "localhost:8080/mockup-server";
    }

    private void copyToClipboard(String text) {
        UI.getCurrent().getPage().executeJs(
                "navigator.clipboard.writeText($0)", text);
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

        if(!userCanEnter(project)){
            event.rerouteTo(MainLayout.class);
            UI.getCurrent().navigate(MainLayout.class);
            return;
        }

        refreshGrid();
    }


}