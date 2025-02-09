package org.example.practica3.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PermitAll
@Route(value = "project-management", layout = MainLayout.class)
@PageTitle("Projects | MockupAPP")
public class ProjectManagementView extends VerticalLayout {
    public ProjectManagementView() {
        TextField searchField = new TextField();
        searchField.setPlaceholder("Buscar proyecto...");
        H1 title = new H1("Proyectos Placeholder");

        add(searchField, title);
    }
}
