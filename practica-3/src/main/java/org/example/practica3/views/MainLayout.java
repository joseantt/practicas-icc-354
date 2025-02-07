package org.example.practica3.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;
import org.example.practica3.views.components.Drawer;
import org.example.practica3.views.components.Header;

@PermitAll
@Route("")
@PageTitle("Home view | MockupAPP")
public class MainLayout extends AppLayout {
    public MainLayout(AuthenticationContext authenticationContext) {
        addToNavbar(new Header(authenticationContext));
        addToDrawer(new Drawer());
    }
}
