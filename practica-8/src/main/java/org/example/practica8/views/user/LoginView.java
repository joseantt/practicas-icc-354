package org.example.practica8.views.user;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.example.practica8.views.MainLayout;

@Route("login")
@PageTitle("Login | Time Grid")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {
    private final LoginForm login = new LoginForm();

    public LoginView() {
        if (UI.getCurrent().getSession().getAttribute("authenticated") != null) {
            UI.getCurrent().navigate(MainLayout.class);
        }

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        login.setForgotPasswordButtonVisible(false);
        login.setAction("login");

        add(login);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        boolean containsError = event.getLocation().getQueryParameters().getParameters().containsKey("error");
        if (containsError) {
            login.setError(true);
        }
    }
}
