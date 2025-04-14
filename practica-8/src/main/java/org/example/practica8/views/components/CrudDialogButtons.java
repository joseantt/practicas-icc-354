package org.example.practica8.views.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class CrudDialogButtons extends HorizontalLayout {

    public CrudDialogButtons(Runnable saveAction, Dialog dialog) {
        Button saveButton = new Button("Save changes", click -> saveAction.run());
        saveButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        saveButton.setIcon(VaadinIcon.CHECK.create());
        saveButton.setIconAfterText(true);

        Button cancelButton = new Button("Cancel", click -> dialog.close());

        getStyle().set("margin-top", "10px");
        setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        add(cancelButton, saveButton);
    }

}


