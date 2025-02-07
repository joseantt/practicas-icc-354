package org.example.practica3.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

// @RolesAllowed(Role.ADMIN) // TODO: FIX ROLE PERMISSIONS
@PermitAll
@Route(value = "admin-panel/user-management", layout = MainLayout.class)
@PageTitle("User management | MockupAPP")
public class UserManagementView extends VerticalLayout {
    public UserManagementView() {
        TextField searchField = new TextField();
        searchField.setPlaceholder("Buscar usuarios...");
        add(searchField);
    }
}
