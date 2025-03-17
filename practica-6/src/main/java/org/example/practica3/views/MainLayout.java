package org.example.practica3.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;
import org.example.practica3.views.components.AppTitle;
import org.example.practica3.views.components.Drawer;
import org.example.practica3.views.components.Header;

@PermitAll
@Route(value = "")
@PageTitle("Home view | MockupAPP")
public class MainLayout extends AppLayout {
    public MainLayout(AuthenticationContext authenticationContext) {
        Div verticalSpace = new Div();
        verticalSpace.getStyle().set("height", "15px");

        addToNavbar(new Header(authenticationContext));
        addToDrawer(new AppTitle("27px"), verticalSpace, new Drawer());
        setPrimarySection(Section.DRAWER);

        Icon icon = new Icon(VaadinIcon.ARROW_LEFT);
        icon.setSize("50px");
        icon.setColor("var(--lumo-primary-color)");

        H2 title = new H2("Welcome to MockupAPP!");
        title.getStyle().set("margin", "0");

        Paragraph message = new Paragraph("Select an option from the side menu to start");
        message.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("margin-top", "0");

        Paragraph message2 = new Paragraph("Application port: " + System.getenv("APP_PORT"));
        message2.getStyle().set("margin-top", "10");

        VerticalLayout container = new VerticalLayout(icon, title, message, message2);
        container.setSizeFull();
        container.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        container.setAlignItems(FlexComponent.Alignment.CENTER);
        container.setSpacing(true);
        container.getStyle()
                .set("background-color", "var(--lumo-base-color)")
                .set("border-radius", "12px")
                .set("padding", "3em")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)");

        setContent(container);
    }
}
