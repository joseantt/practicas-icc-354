package org.example.practica3.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.example.practica3.views.components.Header;

@PermitAll
@Route("")
public class MainAppLayout extends AppLayout {
    public MainAppLayout() {
        addToNavbar(new Header());
    }
}
