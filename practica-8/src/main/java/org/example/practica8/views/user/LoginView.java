package org.example.practica8.views.user;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
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

        changeDefaultLoginText();

        login.getElement().getStyle()
                .set("box-shadow", "var(--lumo-box-shadow-m)")
                .set("border-radius", "var(--lumo-border-radius-m)");

        add(login);
    }

    private void changeDefaultLoginText(){
        LoginI18n i18n = LoginI18n.createDefault();
        LoginI18n.Form i18nForm = i18n.getForm();
        i18nForm.setTitle("Time Grid");
        i18nForm.setUsername("Email address");

        LoginI18n.ErrorMessage errorMessage = i18n.getErrorMessage();
        errorMessage.setTitle("Incorrect email or password\n");
        errorMessage.setMessage("Check that you have entered the correct username and password and try again.");

        login.setI18n(i18n);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        boolean containsError = event.getLocation().getQueryParameters().getParameters().containsKey("error");
        if (containsError) {
            login.setError(true);
        }
    }
}