package org.example.practica8.views.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;

public class ThreeDotsDropdown extends MenuBar {

    public ThreeDotsDropdown() {
        this.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

        MenuItem dropdown = this.addItem(VaadinIcon.ELLIPSIS_DOTS_V.create());
    }

    public ThreeDotsDropdown addDropDownItem(String text,
                                             VaadinIcon icon,
                                             String color,
                                             ComponentEventListener<ClickEvent<Div>> clickListener) {
        MenuItem dropdown = this.getItems().get(0);
        addDropDownButton(dropdown.getSubMenu(), text, icon, color, clickListener);
        return this;
    }

    private void addDropDownButton(SubMenu subMenu, String text, VaadinIcon icon, String color,
                                   ComponentEventListener<ClickEvent<Div>> clickListener) {
        String baseColor = color != null ? color : "var(--lumo-contrast)";

        Div buttonContent = new Div();
        buttonContent.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "space-between")
                .set("width", "100%");

        Span textSpan = new Span(text);
        textSpan.getStyle()
                .set("margin-right", "10px")
                .set("font-weight", "500")
                .set("color", baseColor);

        var iconComponent = icon.create();
        iconComponent.getStyle().set("color", baseColor);

        buttonContent.add(textSpan, iconComponent);
        buttonContent.addClickListener(clickListener);
        subMenu.addItem(buttonContent);
    }
}
