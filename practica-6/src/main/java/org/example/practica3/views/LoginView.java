package org.example.practica3.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.example.practica3.views.components.AppTitle;

import java.io.Serializable;

@Route("login")
@PageTitle("Login | MockupAPP")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver, Serializable {
    private final LoginForm login = new LoginForm();

    // Evita acceder a UI.getCurrent() en el constructor
    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        login.setForgotPasswordButtonVisible(false);
        login.setAction("login");

        add(new AppTitle("32px"), login);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Mueve la lógica de autenticación aquí
        if (VaadinSession.getCurrent().getAttribute("authenticated") != null) {
            event.forwardTo("");  // Navega a la ruta principal
            return;
        }

        // Manejo del error de login
        boolean containsError = event.getLocation().getQueryParameters().getParameters().containsKey("error");
        if (containsError) {
            login.setError(true);
            // Notificación usando addAttachListener para evitar problemas de serialización
            this.getElement().executeJs("setTimeout(() => $0._showErrorNotification(), 100)",
                    getElement());
        }
    }

    // Método para mostrar la notificación
    private void _showErrorNotification() {
        Notification.show("Invalid username or password",
                        3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}