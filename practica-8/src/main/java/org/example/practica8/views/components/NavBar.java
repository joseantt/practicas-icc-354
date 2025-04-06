package org.example.practica8.views.components;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.server.StreamResource;

@Tag("nav-bar")
public class NavBar extends Div {
    public NavBar() {
        setSizeFull();
        getStyle()
                .set("color", "var(--lumo-primary-contrast-color)")
                .set("padding", "0 16px");

        FlexLayout layout = new FlexLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        layout.add(logo(), threeDotsDropdown());
        add(layout);
    }

    private Image logo() {
        StreamResource logoResource = new StreamResource("logo.png",
                () -> getClass().getResourceAsStream("/static/images/logo.png"));
        Image logo = new Image(logoResource, "Logo");

        logo.getStyle().set("height", "65px");
        return logo;
    }

    public MenuBar threeDotsDropdown() {
        ThreeDotsDropdown menu = new ThreeDotsDropdown();

        menu.addDropDownItem("Account settings", VaadinIcon.COG, null, click -> {})
                .addDropDownItem("User management", VaadinIcon.USER, null, click -> {})
                .addDropDownItem("Log out", VaadinIcon.SIGN_OUT, "var(--lumo-error-text-color)", click -> {});

        return menu;
    }
}
