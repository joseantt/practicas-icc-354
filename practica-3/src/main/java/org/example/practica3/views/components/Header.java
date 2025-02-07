package org.example.practica3.views.components;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.security.AuthenticationContext;

@Tag("header")
public class Header extends HorizontalLayout {
    public Header(AuthenticationContext authenticationContext) {
        setWidthFull();
        setPadding(true);
        setAlignItems(Alignment.CENTER);
        getStyle().setBackgroundColor("#f8f9fa").setPadding("10px");

        var leftDiv = new Div(new DrawerToggle(), new AppTitle("20px"));
        leftDiv.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("margin-left", "10px");

        var layout = new FlexLayout(leftDiv, threeDotsMenuBar(authenticationContext));
        layout.setWidth("100%");
        layout.setAlignItems(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        add(layout);
    }

    public MenuBar threeDotsMenuBar(AuthenticationContext authenticationContext) {
        var menu = new MenuBar();
        menu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
        var dropdownThreeDots = menu.addItem(VaadinIcon.ELLIPSIS_DOTS_V.create());
        addLogoutItem(dropdownThreeDots, authenticationContext);
        return menu;
    }

    public void addLogoutItem(MenuItem menuItem, AuthenticationContext authenticationContext) {
        var redColor = "var(--lumo-error-text-color)";

        var logoutItem = menuItem.getSubMenu().addItem(VaadinIcon.SIGN_OUT.create());
        logoutItem.getStyle().setColor(redColor).setPadding("10px"); // TODO: FIX THIS BUTTON AND THAT'S IT
        var text = new Span("Log out");
        text.getStyle()
                .setMarginRight("10px")
                .setFontWeight("500")
                .setColor(redColor);
        logoutItem.addComponentAsFirst(text);
        logoutItem.addClickListener(click -> authenticationContext.logout());
    }
}
