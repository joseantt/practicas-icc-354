package org.example.practica8.views.manager;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ConfirmDialog extends Dialog {

    public ConfirmDialog(String title, String message, String confirmButtonText, Runnable confirmAction) {
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);
        setWidth("400px");

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);

        H3 headerText = new H3(title);
        headerText.getStyle().set("margin-top", "0");

        Paragraph messageText = new Paragraph(message);

        Button cancelButton = new Button("Cancel", e -> close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button confirmButton = new Button(confirmButtonText, e -> {
            confirmAction.run();
            close();
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        confirmButton.setIcon(VaadinIcon.TRASH.create());

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, confirmButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setWidthFull();

        layout.add(headerText, messageText, buttonLayout);
        add(layout);
    }
}