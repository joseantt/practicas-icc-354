package org.example.practica8.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;
import org.example.practica8.services.EventService;
import org.example.practica8.services.UserInfoService;
import org.example.practica8.views.calendar.CalendarView;
import org.example.practica8.views.components.NavBar;

@PermitAll
@PageTitle("Home | Time Grid")
public class MainLayout extends AppLayout {

    public MainLayout(EventService eventService, UserInfoService userInfoService, AuthenticationContext authenticationContext) {
        addToNavbar(new NavBar(userInfoService, authenticationContext));
        setContent(new CalendarView(eventService, userInfoService));
    }
}
