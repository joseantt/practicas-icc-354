package org.example.practica8.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.example.practica8.views.components.NavBar;

@PermitAll
@Route("")
@PageTitle("Home | Time Grid")
public class MainLayout extends AppLayout {
    public MainLayout(){
        addToNavbar(new NavBar());
        setContent(new CalendarView());
    }
}
