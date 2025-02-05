package org.example.practica3.views.components;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@Tag("header")
public class Header extends HorizontalLayout {
    public Header() {
        setWidthFull();
        setPadding(true);
        setAlignItems(Alignment.CENTER);
        getStyle().set("background", "#f8f9fa").set("padding", "10px");

        H1 title = new H1("REST Mock APP");
        title.getStyle().set("margin", "0").set("font-size", "24px");

        Button logoutButton = new Button("Logout", VaadinIcon.SIGN_OUT.create());

        var layout = new HorizontalLayout(title, logoutButton);
        add(layout);
        setJustifyContentMode(JustifyContentMode.BETWEEN);
    }

}
