package org.example.practica3.views;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PermitAll
@Route(value = "admin-panel/user-management", layout = MainLayout.class)
@PageTitle("User management | MockupAPP")
public class MockupFormView {


}
